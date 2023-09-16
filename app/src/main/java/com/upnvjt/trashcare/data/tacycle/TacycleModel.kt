package com.upnvjt.trashcare.data.tacycle

import com.upnvjt.trashcare.data.UserAddress
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random.Default.nextLong

data class TacycleModel(
    val statusOrder: String = "",
    val jenisSampah: List<String> = emptyList(),
    val lokasiPengambilan: UserAddress = UserAddress(),
    val waktuPengambilan: String = "",
    val orderId: String = (nextLong(0, 100_000_000)).toString() + SimpleDateFormat(
        "yyyyMMdd",
        Locale.ENGLISH
    ).format(Date())
)
