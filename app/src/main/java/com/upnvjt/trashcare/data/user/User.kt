package com.upnvjt.trashcare.data.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val firstname: String? = null,
    val lastname: String? = null,
    val email: String = "",
    val phoneNo: String = "",
    val taCoins: Int = 0,
    var imagePath: String = ""
): Parcelable
