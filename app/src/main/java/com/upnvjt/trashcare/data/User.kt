package com.upnvjt.trashcare.data

data class User(
    val firstname: String? = null,
    val lastname: String? = null,
    val username: String = "",
    val email: String = "",
    var imagePath: String = ""
)
