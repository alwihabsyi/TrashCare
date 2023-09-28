package com.upnvjt.trashcare.ui.main.profile.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.upnvjt.trashcare.data.tacommerce.Product
import com.upnvjt.trashcare.ui.main.home.viewmodel.PagingInfo
import com.upnvjt.trashcare.util.Constants.BOOKMARKED_PRODUCTS
import com.upnvjt.trashcare.util.Constants.PRODUCTS
import com.upnvjt.trashcare.util.Constants.USER_COLLECTION
import com.upnvjt.trashcare.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val firestore: FirebaseFirestore, private val auth: FirebaseAuth
) : ViewModel() {

    private val _getBookmark = MutableLiveData<State<List<Product>>>()
    val getBookmark: LiveData<State<List<Product>>> = _getBookmark

    private val pagingInfo = PagingInfo()

    init {
        getBookmark()
    }

    fun getBookmark() {
        _getBookmark.value = State.Loading()
        firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(
            BOOKMARKED_PRODUCTS
        ).limit(pagingInfo.productsPage * 10).addSnapshotListener { value, error ->
            if (error != null) {
                _getBookmark.value = State.Error(error.message.toString())
            }

            val bookmark = value!!.toObjects(Product::class.java)
            pagingInfo.isPagingEnd = bookmark == pagingInfo.oldProducts
            pagingInfo.oldProducts = bookmark
            bookmark.forEach {
                checkProduct(it)
            }
            _getBookmark.value = State.Success(bookmark)
            pagingInfo.productsPage++
        }
    }

    private fun checkProduct(bookmark: Product) {
        firestore.runTransaction { transaction ->
            val bookmarkRef =
                firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(
                    BOOKMARKED_PRODUCTS
                ).document(bookmark.id)
            val docRef = firestore.collection(PRODUCTS).document(bookmark.id)
            val document = transaction.get(docRef)
            val docObj = document.toObject(Product::class.java)
            docObj?.let { product ->
                if (product.stock != bookmark.stock) {
                    transaction.set(bookmarkRef, product)
                } else {
                    return@runTransaction
                }
            }
        }.addOnFailureListener {
            Log.e("Bookmark Error", it.message.toString())
        }
    }

}