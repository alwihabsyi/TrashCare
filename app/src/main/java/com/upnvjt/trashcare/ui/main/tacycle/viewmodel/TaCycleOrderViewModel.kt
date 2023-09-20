package com.upnvjt.trashcare.ui.main.tacycle.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.upnvjt.trashcare.data.tacycle.TaCycleStatus
import com.upnvjt.trashcare.data.tacycle.TacycleModel
import com.upnvjt.trashcare.util.Constants.TACYCLE_ORDER_COLLECTION
import com.upnvjt.trashcare.util.Constants.USER_COLLECTION
import com.upnvjt.trashcare.util.State

class TaCycleOrderViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val status: TaCycleStatus
): ViewModel(){

    private val _allOrders = MutableLiveData<State<List<TacycleModel>>>()
    val allOrders: LiveData<State<List<TacycleModel>>> = _allOrders

    init {
        getAllOrders()
    }

    private fun getAllOrders() {
        _allOrders.value = State.Loading()
        firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(TACYCLE_ORDER_COLLECTION)
            .whereEqualTo("statusOrder", status.cycleStatus).get()
            .addOnSuccessListener {
                val orders = it.toObjects(TacycleModel::class.java)
                _allOrders.value = State.Success(orders)
            }
            .addOnFailureListener {
                _allOrders.value = State.Error(it.message.toString())
            }
    }

}