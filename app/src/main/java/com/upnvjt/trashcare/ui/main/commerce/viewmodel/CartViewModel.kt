package com.upnvjt.trashcare.ui.main.commerce.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.upnvjt.trashcare.data.tacommerce.Cart
import com.upnvjt.trashcare.data.tacommerce.Product
import com.upnvjt.trashcare.util.Constants.CART
import com.upnvjt.trashcare.util.Constants.PRODUCTS
import com.upnvjt.trashcare.util.Constants.USER_COLLECTION
import com.upnvjt.trashcare.util.FirebaseCommon
import com.upnvjt.trashcare.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    private val _cart = MutableLiveData<State<List<Cart>>>()
    val cart: LiveData<State<List<Cart>>> = _cart

    private val _itemQuantity = MutableLiveData<State<Cart>>()
    val itemQuantity: LiveData<State<Cart>> = _itemQuantity

    private val _deleteDialog = MutableLiveData<Cart>()
    val deleteDialog: LiveData<Cart> = _deleteDialog

    init {
        getAllCartProducts()
    }

    private fun getAllCartProducts() {
        _cart.value = State.Loading()
        firestore.collection(USER_COLLECTION).document(auth.uid!!)
            .collection(CART).addSnapshotListener { value, error ->
                if (error != null) {
                    _cart.value = State.Error(error.message.toString())
                    return@addSnapshotListener
                }

                val cart = value!!.toObjects(Cart::class.java)
                cart.forEach {
                    checkProduct(it)
                }
                _cart.value = State.Success(cart)
            }
    }

    private fun checkProduct(cart: Cart) {
        firestore.runTransaction { transaction ->
            val cartRef =
                firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(
                    CART
                ).document(cart.product.id)
            val docRef = firestore.collection(PRODUCTS).document(cart.product.id)
            val document = transaction.get(docRef)
            val docObj = document.toObject(Product::class.java)
            docObj?.let { product ->
                if (product.stock != cart.product.stock) {
                    if (product.stock == 0) {
                        deleteCartProduct(cart.product.id)
                        return@runTransaction
                    }

                    val cartData = cart.copy(product = product)
                    transaction.set(cartRef, cartData)
                } else {
                    return@runTransaction
                }
            }
        }.addOnFailureListener {
            Log.e("Cart Error", it.message.toString())
        }
    }

    fun changeQuantity(cart: Cart, addQuantity: Boolean) {
        _itemQuantity.value = State.Loading()
        firestore.collection(USER_COLLECTION).document(auth.uid!!)
            .collection(CART).document(cart.product.id).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val product = it.toObject(Cart::class.java)
                    product?.let { cart ->
                        if (addQuantity) {
                            val newQuantity = cart.quantity + 1
                            if (newQuantity <= cart.product.stock) {
                                val document = cart.product.id
                                increaseQuantity(document)
                            } else {
                                _itemQuantity.value =
                                    State.Error("Barang di keranjang melebihi stok produk")
                            }
                        } else {
                            if (cart.quantity == 1) {
                                _deleteDialog.value = cart
                                return@addOnSuccessListener
                            }

                            decreaseQuantity(cart.product.id)
                        }
                    }
                }
            }
    }

    fun deleteCartProduct(productId: String) {
        firestore.collection(USER_COLLECTION).document(auth.uid!!)
            .collection(CART).document(productId).delete()
    }


    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId, 1) { _, e ->
            if (e != null) {
                _itemQuantity.value = State.Error(e.message.toString())
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId) { _, exception ->
            if (exception != null) {
                _itemQuantity.value = State.Error(exception.message.toString())
            }
        }
    }
}