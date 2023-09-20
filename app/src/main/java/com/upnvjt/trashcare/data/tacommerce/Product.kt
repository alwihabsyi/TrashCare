package com.upnvjt.trashcare.data.tacommerce

data class Product(
    val id: String ,
    val name: String,
    val price: Int,
    val stock: Int,
    val description: String,
    val photoUrl: String
)
