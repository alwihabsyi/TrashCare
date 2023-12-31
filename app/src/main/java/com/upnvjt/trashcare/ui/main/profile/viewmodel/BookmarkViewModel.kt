package com.upnvjt.trashcare.ui.main.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.upnvjt.trashcare.data.tacommerce.Product
import com.upnvjt.trashcare.util.Constants
import com.upnvjt.trashcare.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val firestore: FirebaseFirestore, private val auth: FirebaseAuth
) : ViewModel() {

    private val _getBookmark = MutableLiveData<State<List<Product>>>()
    val getBookmark: LiveData<State<List<Product>>> = _getBookmark

    init {
        getBookmark()
    }

    private fun getBookmark() {
        _getBookmark.value = State.Loading()
        firestore.collection(Constants.USER_COLLECTION).document(auth.uid!!).collection(
            Constants.BOOKMARKED_PRODUCTS
        ).addSnapshotListener { value, error ->
            if (error != null) {
                _getBookmark.value = State.Error(error.message.toString())
            }

            val bookmark = value!!.toObjects(Product::class.java)
            checkProduct(bookmark)
        }
    }

    private fun checkProduct(bookmark: List<Product>) {
        val bookmarkedProducts = mutableListOf<Product>()
        val limit = bookmark.size.toLong() + 2
        firestore.collection(Constants.PRODUCTS).limit(limit).get()
            .addOnSuccessListener {
                val products = it?.toObjects(Product::class.java)
                bookmark.forEach { bookmark ->
                    products!!.forEach { product ->
                        if (bookmark.id == product.id) {
                            if (product.stock != 0) {
                                bookmarkedProducts.add(bookmark)
                            } else {
                                deleteBookmarkedProduct(bookmark.id)
                            }
                        }
                    }
                }
                _getBookmark.value = State.Success(bookmarkedProducts)
            }.addOnFailureListener {
                _getBookmark.value = State.Error(it.message.toString())
            }
    }

    private fun deleteBookmarkedProduct(id: String) {
        firestore.collection(Constants.USER_COLLECTION).document(auth.uid!!).collection(
            Constants.BOOKMARKED_PRODUCTS
        ).document(id).delete().addOnFailureListener {
            _getBookmark.value = State.Error(it.message.toString())
        }
    }

}