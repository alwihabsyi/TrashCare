package com.upnvjt.trashcare.data.tacommerce

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

@Parcelize
data class Product(
    val name: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val description: String = "",
    val photoUrl: String = "",
    val id: String = Random.nextLong(0, 1_000_000_000).toString()
): Parcelable
