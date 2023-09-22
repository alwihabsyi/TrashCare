package com.upnvjt.trashcare.ui.main.commerce.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.upnvjt.trashcare.data.tacommerce.Cart
import com.upnvjt.trashcare.util.Constants.CART
import com.upnvjt.trashcare.util.Constants.USER_COLLECTION
import com.upnvjt.trashcare.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {

    private val _cart = MutableLiveData<State<List<Cart>>>()
    val cart: LiveData<State<List<Cart>>> = _cart

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

                val cart = value?.toObjects(Cart::class.java)
                _cart.value = State.Success(cart!!)
            }
    }
}