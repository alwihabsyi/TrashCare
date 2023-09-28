package com.upnvjt.trashcare.ui.main.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.upnvjt.trashcare.data.tacampaign.TaskData
import com.upnvjt.trashcare.data.tacommerce.Product
import com.upnvjt.trashcare.data.user.User
import com.upnvjt.trashcare.util.Constants
import com.upnvjt.trashcare.util.Constants.TASK
import com.upnvjt.trashcare.util.Constants.USER_COLLECTION
import com.upnvjt.trashcare.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _task = MutableLiveData<State<List<TaskData>>>()
    val task: LiveData<State<List<TaskData>>> = _task

    private val _user = MutableLiveData<State<User>>()
    val user: LiveData<State<User>> = _user

    private val _allProducts = MutableLiveData<State<List<Product>>>()
    val allProducts: LiveData<State<List<Product>>> = _allProducts

    private val _searchProducts = MutableLiveData<State<List<Product>>>()
    val searchProducts: LiveData<State<List<Product>>> = _searchProducts

    private val pagingInfo = PagingInfo()

    init {
        getAllTask()
        getUser()
        getAllProducts()
    }

    private fun getAllTask() {
        _task.value = State.Loading()
        firestore.collection(USER_COLLECTION).document(auth.uid!!)
            .collection(TASK)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _task.value = State.Error(error.message.toString())
                }

                val task = value?.toObjects(TaskData::class.java)
                _task.value = State.Success(task!!)
            }
    }

    private fun getUser() {
        _user.value = State.Loading()
        firestore.collection(USER_COLLECTION).document(auth.uid!!)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _user.value = State.Error(error.message.toString())
                }

                val user = value?.toObject(User::class.java)
                _user.value = State.Success(user!!)
            }
    }

    fun getAllProducts() {
        _allProducts.value = State.Loading()
        firestore.collection(Constants.PRODUCTS).limit(pagingInfo.productsPage * 10)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _allProducts.value = State.Error(error.message.toString())
                }

                val products = value!!.toObjects(Product::class.java)
                pagingInfo.isPagingEnd = products == pagingInfo.oldProducts
                pagingInfo.oldProducts = products
                _allProducts.value = State.Success(products)
                pagingInfo.productsPage++
            }
    }

    fun getSearchedProducts(productName: String) {
        _searchProducts.value = State.Loading()
        firestore.collection(Constants.PRODUCTS)
            .whereEqualTo("name", productName.replaceFirstChar {
                it.uppercase()
            }).limit(pagingInfo.productsPage * 10).get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                pagingInfo.isPagingEnd = products == pagingInfo.oldProducts
                pagingInfo.oldProducts = products
                _searchProducts.value = State.Success(products)
                pagingInfo.productsPage++
            }.addOnFailureListener {
                _searchProducts.value = State.Error(it.message.toString())
            }
    }

    fun getFilteredProducts(isAscending: Boolean) {
        _allProducts.value = State.Loading()
        val filter = if (isAscending) Query.Direction.ASCENDING else Query.Direction.DESCENDING
        firestore.collection(Constants.PRODUCTS)
            .orderBy("price", filter)
            .limit(pagingInfo.productsPage * 10)
            .get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                pagingInfo.isPagingEnd = products == pagingInfo.oldProducts
                pagingInfo.oldProducts = products
                _allProducts.value = State.Success(products)
                pagingInfo.productsPage++
            }.addOnFailureListener {
                _allProducts.value = State.Error(it.message.toString())
            }
    }

    fun logout() {
        auth.signOut()
    }

}

internal data class PagingInfo(
    var productsPage: Long = 1,
    var oldProducts: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)