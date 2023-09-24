package com.upnvjt.trashcare.data.tacycle

import com.upnvjt.trashcare.data.user.UserAddress
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class TacycleModel(
    val statusOrder: String = "",
    val jenisSampah: List<String> = emptyList(),
    val lokasiPengambilan: UserAddress = UserAddress(),
    val waktuPengambilan: String = "",
    val orderId: String = UUID.randomUUID().toString() + SimpleDateFormat(
        "yyyyMMdd",
        Locale.ENGLISH
    ).format(Date()),
    val tanggalOrder: Date = Date(),
    val userId: String? = null
)
