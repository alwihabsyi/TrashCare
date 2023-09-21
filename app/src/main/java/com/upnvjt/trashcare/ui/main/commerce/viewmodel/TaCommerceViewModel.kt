package com.upnvjt.trashcare.ui.main.commerce.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.upnvjt.trashcare.data.tacommerce.Product
import com.upnvjt.trashcare.util.Constants.PRODUCTS
import com.upnvjt.trashcare.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaCommerceViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _allProducts = MutableLiveData<State<List<Product>>>()
    val allProducts: LiveData<State<List<Product>>> = _allProducts

    init {
        getAllProducts()
    }

    private fun getAllProducts() {
        _allProducts.value = State.Loading()
        firestore.collection(PRODUCTS).get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                _allProducts.value = State.Success(products)
            }
            .addOnFailureListener {
                _allProducts.value = State.Error(it.message.toString())
            }
    }
}