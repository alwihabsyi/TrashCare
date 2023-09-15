package com.upnvjt.trashcare.ui.main.tacycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.upnvjt.trashcare.data.tacycle.TacycleModel
import com.upnvjt.trashcare.util.Constants.TACYCLE_ORDER_COLLECTION
import com.upnvjt.trashcare.util.Constants.USER_TACYCLE_COLLECTION
import com.upnvjt.trashcare.util.Constants.USER_COLLECTION
import com.upnvjt.trashcare.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaCycleViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {

    private val _cycleOrder = MutableLiveData<State<TacycleModel>>()
    val cycleOrder: LiveData<State<TacycleModel>> = _cycleOrder

    fun placeCycleOrder(cycle: TacycleModel) {
        _cycleOrder.value = State.Loading()
        firestore.runBatch {
            firestore.collection(USER_COLLECTION)
                .document(auth.uid!!)
                .collection(USER_TACYCLE_COLLECTION)
                .document()
                .set(cycle)

            firestore.collection(TACYCLE_ORDER_COLLECTION)
                .document()
                .set(cycle)
        }.addOnSuccessListener {
            _cycleOrder.value = State.Success(cycle)
        }.addOnFailureListener {
            _cycleOrder.value = State.Error(it.message.toString())
        }
    }

}