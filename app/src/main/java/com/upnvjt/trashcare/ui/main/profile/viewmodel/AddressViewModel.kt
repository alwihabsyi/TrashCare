package com.upnvjt.trashcare.ui.main.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.upnvjt.trashcare.data.user.UserAddress
import com.upnvjt.trashcare.util.Constants.ADDRESS
import com.upnvjt.trashcare.util.Constants.USER_COLLECTION
import com.upnvjt.trashcare.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _address = MutableLiveData<State<List<UserAddress>>>()
    val address: LiveData<State<List<UserAddress>>> = _address

    private val _addNewAddress = MutableLiveData<State<UserAddress>>()
    val addNewAddress: LiveData<State<UserAddress>> = _addNewAddress

    private val _deleteAddress = MutableLiveData<State<String>>()
    val deleteAddress: LiveData<State<String>> = _deleteAddress

    init {
        getAddress()
    }

    private fun getAddress() {
        _address.value = State.Loading()
        firestore.collection(USER_COLLECTION).document(auth.uid!!)
            .collection(ADDRESS).addSnapshotListener { value, error ->
                if (error != null) {
                    _address.value = State.Error(error.message.toString())
                    return@addSnapshotListener
                }

                val address = value?.toObjects(UserAddress::class.java)
                _address.value = State.Success(address!!)
            }
    }

    fun addAddress(address: UserAddress) {
        _addNewAddress.value = State.Loading()
        firestore.collection(USER_COLLECTION).document(auth.uid!!)
            .collection(ADDRESS).document(address.addressId).set(address)
            .addOnSuccessListener {
                _addNewAddress.value = State.Success(address)
            }
            .addOnFailureListener {
                _addNewAddress.value = State.Error(it.message.toString())
            }
    }

    fun deleteAddress(addressId: String) {
        _deleteAddress.value = State.Loading()
        firestore.collection(USER_COLLECTION).document(auth.uid!!)
            .collection(ADDRESS).document(addressId).delete()
            .addOnSuccessListener {
                _deleteAddress.value = State.Success("Alamat Berhasil Dihapus")
            }
            .addOnFailureListener {
                _deleteAddress.value = State.Error(it.message.toString())
            }
    }

}