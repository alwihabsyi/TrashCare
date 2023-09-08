package com.upnvjt.trashcare.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.upnvjt.trashcare.data.User
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
class SignInViewModel @Inject constructor(
    private val auth: FirebaseAuth
): ViewModel() {

    private val _login = MutableLiveData<State<String>>()
    val login: LiveData<State<String>> = _login

    private val _validation = Channel<FieldsState>()
    val validation = _validation.receiveAsFlow()

    private val _resetPassword = MutableLiveData<State<String>>()
    val resetPassword: LiveData<State<String>> = _resetPassword

    fun login(email: String, password: String){
        if(checkValidation(email, password)){
            _login.value = State.Loading()
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    _login.value = State.Success("Login success")
                }
                .addOnFailureListener {
                    _login.value = State.Error(it.message.toString())
                }
        }else{
            val registerFieldsState = FieldsState(
                validateEmail(email), validatePassword(password)
            )
            runBlocking {
                _validation.send(registerFieldsState)
            }
        }
    }

    private fun checkValidation(email: String, password: String): Boolean {
        val emailValidation = validateEmail(email)
        val passwordValidation = validatePassword(password)

        return emailValidation is Validation.Success &&
                passwordValidation is Validation.Success
    }

}