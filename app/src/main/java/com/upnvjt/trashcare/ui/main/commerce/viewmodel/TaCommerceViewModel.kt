package com.upnvjt.trashcare.ui.main.commerce.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

    private val _searchProducts = MutableLiveData<State<List<Product>>>()
    val searchProducts: LiveData<State<List<Product>>> = _searchProducts

    init {
        getAllProducts()
    }

    private fun getAllProducts() {
        _allProducts.value = State.Loading()
        firestore.collection(PRODUCTS).get().addOnSuccessListener {
            val products = it.toObjects(Product::class.java)
            _allProducts.value = State.Success(products)
        }.addOnFailureListener {
            _allProducts.value = State.Error(it.message.toString())
        }
    }

    fun getSearchedProducts(productName: String) {
        _searchProducts.value = State.Loading()
        firestore.collection(PRODUCTS)
            .whereEqualTo("name", productName.replaceFirstChar {
                it.uppercase()
            }).get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                _searchProducts.value = State.Success(products)
            }.addOnFailureListener {
                _searchProducts.value = State.Error(it.message.toString())
            }
    }

    fun getFilteredProducts(isAscending: Boolean) {
        _allProducts.value = State.Loading()
        val filter = if (isAscending) Query.Direction.ASCENDING else Query.Direction.DESCENDING
        firestore.collection(PRODUCTS)
            .orderBy("price", filter)
            .get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                _allProducts.value = State.Success(products)
            }.addOnFailureListener {
                _allProducts.value = State.Error(it.message.toString())
            }
    }
}