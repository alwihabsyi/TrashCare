package com.upnvjt.trashcare.data.tacommerce

import android.os.Parcelable
import com.upnvjt.trashcare.data.user.UserAddress
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@Parcelize
data class Orders(
    val orderStatus: String = "",
    val totalPrice: Double = 0.0,
    val products: List<Cart> = emptyList(),
    val address: UserAddress = UserAddress(),
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()),
    val orderId: Long = Random.nextLong(0, 100_000_000_000) + totalPrice.toLong()
): Parcelable