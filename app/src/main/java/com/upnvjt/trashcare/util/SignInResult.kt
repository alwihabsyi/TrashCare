package com.upnvjt.trashcare.util

import com.upnvjt.trashcare.data.User

data class SignInResult(
    val data: User?,
    val id: String?,
    val errorMessage: String?
)