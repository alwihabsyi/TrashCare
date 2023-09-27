package com.upnvjt.trashcare.ui.main.tacycle.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.upnvjt.trashcare.data.tacommerce.OrderStatus
import com.upnvjt.trashcare.data.tacommerce.Orders
import com.upnvjt.trashcare.data.tacycle.TaCycleStatus
import com.upnvjt.trashcare.data.tacycle.TacycleModel
import com.upnvjt.trashcare.ui.main.commerce.MyCartFragment.Companion.ORDER
import com.upnvjt.trashcare.util.Constants.TACYCLE_ORDER_COLLECTION
import com.upnvjt.trashcare.util.Constants.USER_COLLECTION
import com.upnvjt.trashcare.util.State

class OrderViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val cycleStatus: TaCycleStatus,
    private val orderStatus: OrderStatus
) : ViewModel() {

    private val _allOrders = MutableLiveData<State<List<TacycleModel>>>()
    val allOrders: LiveData<State<List<TacycleModel>>> = _allOrders

    private val _commerceOrders = MutableLiveData<State<List<Orders>>>()
    val commerceOrders: LiveData<State<List<Orders>>> = _commerceOrders

    fun getTaCycleOrders() {
        _allOrders.value = State.Loading()
        firestore.collection(USER_COLLECTION).document(auth.uid!!)
            .collection(TACYCLE_ORDER_COLLECTION)
            .whereEqualTo("statusOrder", cycleStatus.cycleStatus)
            .orderBy("tanggalOrder", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                val orders = it.toObjects(TacycleModel::class.java)
                _allOrders.value = State.Success(orders)
            }
            .addOnFailureListener {
                _allOrders.value = State.Error(it.message.toString())
            }
    }

    fun getCommerceOrders() {
        _commerceOrders.value = State.Loading()
        firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(ORDER)
            .whereEqualTo("orderStatus", orderStatus.orderStatus)
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {
                val orders = it.toObjects(Orders::class.java)
                _commerceOrders.value = State.Success(orders)
            }
            .addOnFailureListener {

                _commerceOrders.value = State.Error(it.message.toString())
            }
    }

}