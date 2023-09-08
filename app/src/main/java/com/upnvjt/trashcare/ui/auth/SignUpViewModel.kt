package com.upnvjt.trashcare.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.upnvjt.trashcare.data.User
import com.upnvjt.trashcare.util.Constants.USER_COLLECTION
import com.upnvjt.trashcare.util.FieldsState
import com.upnvjt.trashcare.util.State
import com.upnvjt.trashcare.util.Validation
import com.upnvjt.trashcare.util.validateEmail
import com.upnvjt.trashcare.util.validatePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
): ViewModel() {

    private val _register = MutableLiveData<State<String>>()
    val register: LiveData<State<String>> = _register

    private val _validation = Channel<FieldsState>()
    val validation = _validation.receiveAsFlow()

    fun signUpWithEmail(user: User, password: String) {
        if (checkValidation(user, password)) {
            _register.value = State.Loading()
            auth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    it.user?.let { firebaseUser ->
                        saveUserInfo(firebaseUser.uid, user)
                    }
                }
                .addOnFailureListener {
                    _register.value = State.Error(it.message.toString())
                }
        }else {
            val registerFieldsState = FieldsState(validateEmail(user.email), validatePassword(password))
            runBlocking { _validation.send(registerFieldsState) }
        }
    }

    private fun saveUserInfo(userUid: String, user: User) {
        db.collection(USER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = State.Success("User registered successfully")
            }
            .addOnFailureListener {
                _register.value = State.Error(it.message.toString())
            }
    }

    private fun checkValidation(user: User, password: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)

        return emailValidation is Validation.Success &&
                passwordValidation is Validation.Success
    }

}