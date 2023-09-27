package com.upnvjt.trashcare.data.tacycle

import com.upnvjt.trashcare.data.user.UserAddress
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random.Default.nextLong

data class TacycleModel(
    val statusOrder: String = "",
    val jenisSampah: List<String> = emptyList(),
    val lokasiPengambilan: UserAddress = UserAddress(),
    val waktuPengambilan: String = "",
    val orderId: String = nextLong(0, 1_000_000_00).toString() + SimpleDateFormat(
        "yyyyMMdd",
        Locale.ENGLISH
    ).format(Date()),
    val tanggalOrder: Date = Date(),
    val userId: String? = null
)
