package com.upnvjt.trashcare.util

import android.opengl.Visibility
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar


fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}
fun Fragment.toast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.snackbar(view: View, msg: String) {
    Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
}

fun EditText.string(): String{
    return text.toString()
}

fun validateEmail(email: String): Validation{
    if(email.isEmpty()){
        return Validation.Failed("Email tidak boleh kosong")
    }

    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
        return Validation.Failed("Harap isi dengan email yang valid")
    }

    return Validation.Success
}

fun validatePassword(password: String): Validation{
    if(password.isEmpty()){
        return Validation.Failed("Password tidak boleh kosong")
    }

    if(password.length < 6){
        return Validation.Failed("Password harus terdiri dari 6 huruf atau lebih")
    }

    return Validation.Success
}