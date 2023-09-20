package com.upnvjt.trashcare.data.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserAddress(
    val namaJalan: String,
    val kelurahan: String,
    val kecamatan: String,
    val kota: String,
    val provinsi: String,
    val kodePos: String,
    val judulAlamat: String
) : Parcelable {
    constructor(): this("", "", "", "" , "", "", "")
}
