package com.upnvjt.trashcare.ui.main.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.upnvjt.trashcare.data.user.User
import com.upnvjt.trashcare.util.Constants
import com.upnvjt.trashcare.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _updateProfile = MutableLiveData<State<User>>()
    val updateProfile: LiveData<State<User>> = _updateProfile

    private val _updatePassword = MutableLiveData<State<String>>()
    val updatePassword: LiveData<State<String>> = _updatePassword

    fun updateProfile(user: User, photo: ByteArray?) {
        _updateProfile.value = State.Loading()
        if (photo == null) {
            saveData(user)
        } else {
            var image = ""
            viewModelScope.launch {
                try {
                    async {
                        val id = UUID.randomUUID().toString()
                        launch {
                            val imageStorage = storage.reference.child("users/images/$id")
                            val result = imageStorage.putBytes(photo).await()
                            val downloadUrl = result.storage.downloadUrl.await().toString()
                            image = downloadUrl
                        }
                    }.await()
                } catch (e: Exception) {
                    _updateProfile.value = State.Error(e.message.toString())
                }

                val userData = user.copy(imagePath = image)
                saveData(userData)
            }
        }
    }

    private fun saveData(userData: User) {
        firestore.collection(Constants.USER_COLLECTION).document(auth.uid!!).set(userData)
            .addOnSuccessListener {
                _updateProfile.value = State.Success(userData)
            }.addOnFailureListener {
                _updateProfile.value = State.Error(it.message.toString())
            }
    }

    fun updatePassword(oldPassword: String, newPassword: String) {
        _updatePassword.value = State.Loading()
        val credential = EmailAuthProvider.getCredential(auth.currentUser!!.email!!, oldPassword)
        auth.currentUser!!.reauthenticate(credential).addOnSuccessListener {
            auth.currentUser!!.updatePassword(newPassword)
                .addOnSuccessListener {
                    _updatePassword.value = State.Success("Berhasil update password")
                }
                .addOnFailureListener {
                    _updatePassword.value = State.Error(it.message.toString())
                }
        }.addOnFailureListener {
            _updatePassword.value = State.Error(it.message.toString())
        }
    }

}