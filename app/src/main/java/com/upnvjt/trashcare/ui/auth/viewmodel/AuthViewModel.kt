package com.upnvjt.trashcare.ui.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.upnvjt.trashcare.data.User
import com.upnvjt.trashcare.util.Constants
import com.upnvjt.trashcare.util.SignInResult
import com.upnvjt.trashcare.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _google = MutableLiveData<State<String>>()
    val google: LiveData<State<String>> = _google

    fun googleSignIn(result: SignInResult) {
        _google.value = State.Loading()
        firestore.collection(Constants.USER_COLLECTION).document(result.id!!).get()
            .addOnCompleteListener {
                if (it.result.exists()){
                    _google.value = State.Success("Berhasil masuk")
                }else {
                    addNewUser(result.id, result.data!!)
                }
            }
    }

    private fun addNewUser(userId: String, user: User) {
        firestore.collection(Constants.USER_COLLECTION).document(userId)
            .set(user)
            .addOnSuccessListener {
                _google.value = State.Success("Berhasil masuk")
            }
            .addOnFailureListener {
                _google.value = State.Error(it.message.toString())
            }
    }

}