package com.upnvjt.trashcare.ui.main.tacycle.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.upnvjt.trashcare.data.tacycle.TaCycleStatus

class TaCycleOrderViewModelFactory(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val status: TaCycleStatus
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TaCycleOrderViewModel(firestore, auth, status) as T
    }

}