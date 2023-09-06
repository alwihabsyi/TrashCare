package com.upnvjt.trashcare.ui.data

data class Product(
    val name: String?,
    val img: Int,
    val price: Int?,
    val stock: Int?,
    val rating: Double?,
    val isDiscount: Boolean?,
    val priceDiscount: Int?,
    val description: String
)
