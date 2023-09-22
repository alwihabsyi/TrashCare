package com.upnvjt.trashcare.data.tacommerce

data class Cart (
    val product: Product = Product(),
    val quantity: Int = 0,
)