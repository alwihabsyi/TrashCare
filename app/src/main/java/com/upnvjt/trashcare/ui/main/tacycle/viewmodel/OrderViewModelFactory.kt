package com.upnvjt.trashcare.ui.main.tacycle.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.upnvjt.trashcare.data.tacommerce.OrderStatus
import com.upnvjt.trashcare.data.tacycle.TaCycleStatus

class OrderViewModelFactory(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val status: TaCycleStatus,
    private val orderStatus: OrderStatus
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OrderViewModel(firestore, auth, status, orderStatus) as T
    }

}