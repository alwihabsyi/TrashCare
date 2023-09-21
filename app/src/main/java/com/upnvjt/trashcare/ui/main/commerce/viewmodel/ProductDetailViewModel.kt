package com.upnvjt.trashcare.ui.main.commerce.viewmodel

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
class ProductDetailViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _getBookmark = MutableLiveData<State<Boolean>>()
    val getBookmark: LiveData<State<Boolean>> = _getBookmark

    private val _bookmarkProduct = MutableLiveData<State<Boolean>>()
    val bookmarkProduct: LiveData<State<Boolean>> = _bookmarkProduct

    fun getBookmark(productId: String) {
        _getBookmark.value = State.Loading()
        firestore.collection(Constants.USER_COLLECTION).document(auth.uid!!).collection(
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
                firestore.collection(Constants.USER_COLLECTION).document(auth.uid!!).collection(
                    Constants.BOOKMARKED_PRODUCTS
                ).document(documentId).delete()
            } else {
                firestore.collection(Constants.USER_COLLECTION).document(auth.uid!!).collection(
                    Constants.BOOKMARKED_PRODUCTS
                ).document(documentId).set(product)
            }
        }.addOnFailureListener {
            _bookmarkProduct.value = State.Error(it.message.toString())
        }
    }
}