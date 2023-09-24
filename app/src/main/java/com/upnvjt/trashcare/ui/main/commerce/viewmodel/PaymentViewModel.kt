package com.upnvjt.trashcare.ui.main.commerce.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.upnvjt.trashcare.data.tacommerce.Orders
import com.upnvjt.trashcare.ui.main.commerce.MyCartFragment.Companion.ORDER
import com.upnvjt.trashcare.util.Constants.CART
import com.upnvjt.trashcare.util.Constants.PRODUCTS
import com.upnvjt.trashcare.util.Constants.USER_COLLECTION
import com.upnvjt.trashcare.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
): ViewModel() {

    private val _submitOrder = MutableLiveData<State<Orders>>()
    val submitOrder: LiveData<State<Orders>> = _submitOrder

    fun submitOrder(data: Orders, photo: ByteArray) {
        _submitOrder.value = State.Loading()
        var image = ""
        viewModelScope.launch {
            try {
                async {
                    val id = UUID.randomUUID().toString()
                    launch {
                        val imageStorage = storage.reference.child("products/images/$id")
                        val result = imageStorage.putBytes(photo).await()
                        val downloadUrl = result.storage.downloadUrl.await().toString()
                        image = downloadUrl
                    }
                }.await()
            }catch (e: Exception) {
                _submitOrder.value = State.Error(e.message.toString())
            }

            val order = data.copy(paymentPhoto = image)
            storeData(order)
        }
    }

    private fun storeData(order: Orders) {
        firestore.runBatch {
            firestore.collection(USER_COLLECTION)
                .document(auth.uid!!)
                .collection(ORDER)
                .document(order.orderId)
                .set(order)

            val adminOrder = order.copy(userId = auth.uid)
            firestore.collection(ORDER)
                .document(order.orderId)
                .set(adminOrder)

            order.products.forEach {
                firestore.collection(USER_COLLECTION)
                    .document(auth.uid!!)
                    .collection(CART)
                    .document(it.product.id)
                    .delete()

                val quantity = it.product.stock - it.quantity
                val product = it.product.copy(stock = quantity)
                firestore.collection(PRODUCTS)
                    .document(it.product.id)
                    .set(product)
            }
        }.addOnSuccessListener {
            _submitOrder.value = State.Success(order)
        }.addOnFailureListener {
            _submitOrder.value = State.Error(it.message.toString())
        }
    }

}