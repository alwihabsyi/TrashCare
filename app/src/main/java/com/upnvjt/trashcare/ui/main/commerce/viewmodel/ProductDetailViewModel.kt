package com.upnvjt.trashcare.ui.main.commerce.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.upnvjt.trashcare.data.tacommerce.Cart
import com.upnvjt.trashcare.data.tacommerce.Product
import com.upnvjt.trashcare.util.Constants
import com.upnvjt.trashcare.util.Constants.CART
import com.upnvjt.trashcare.util.Constants.USER_COLLECTION
import com.upnvjt.trashcare.util.FirebaseCommon
import com.upnvjt.trashcare.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    private val _getBookmark = MutableLiveData<State<Boolean>>()
    val getBookmark: LiveData<State<Boolean>> = _getBookmark

    private val _bookmarkProduct = MutableLiveData<State<Boolean>>()
    val bookmarkProduct: LiveData<State<Boolean>> = _bookmarkProduct

    private val _addToCart = MutableLiveData<State<Cart>>()
    val addToCart: LiveData<State<Cart>> = _addToCart

    fun getBookmark(productId: String) {
        _getBookmark.value = State.Loading()
        firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(
            Constants.BOOKMARKED_PRODUCTS
        ).document(productId).addSnapshotListener { value, error ->
            if (error != null) {
                _getBookmark.value = State.Error(error.message.toString())
                return@addSnapshotListener
            }

            value?.let {
                _getBookmark.value = State.Success(it.exists())
            }
        }
    }

    fun bookmarkProduct(product: Product, isBookmarked: Boolean) {
        _bookmarkProduct.value = State.Loading()
        val documentId = product.id
        firestore.runTransaction {
            if (isBookmarked) {
                firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(
                    Constants.BOOKMARKED_PRODUCTS
                ).document(documentId).delete()
            } else {
                firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(
                    Constants.BOOKMARKED_PRODUCTS
                ).document(documentId).set(product)
            }
        }.addOnFailureListener {
            _bookmarkProduct.value = State.Error(it.message.toString())
        }
    }

    fun addUpdateProductToCart(cart: Cart) {
        _addToCart.value = State.Loading()
        firestore.collection(USER_COLLECTION)
            .document(auth.uid!!)
            .collection(CART)
            .document(cart.product.id)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val product = it.toObject(Cart::class.java)
                    product?.let {
                        val newQuantity = product.quantity + cart.quantity
                        if (newQuantity <= product.product.stock) {
                            val document = cart.product.id
                            increaseQuantity(document, cart)
                        } else {
                            _addToCart.value =
                                State.Error("Barang di keranjang melebihi stok produk")
                        }
                    }
                } else {
                    addNewProduct(cart)
                }
            }
    }

    private fun addNewProduct(cart: Cart) {
        firebaseCommon.addProduct(cart) { addedProduct, e ->
            if (e == null) {
                _addToCart.value = State.Success(addedProduct!!)
            } else {
                _addToCart.value = State.Error(e.message.toString())
            }
        }
    }

    private fun increaseQuantity(documentId: String, cartProduct: Cart) {
        firebaseCommon.increaseQuantity(documentId, cartProduct.quantity) { _, e ->
            if (e == null) {
                _addToCart.value = State.Success(cartProduct)
            } else {
                _addToCart.value = State.Error(e.message.toString())
            }
        }
    }
}