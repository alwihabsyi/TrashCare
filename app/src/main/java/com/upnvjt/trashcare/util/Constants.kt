package com.upnvjt.trashcare.util

import android.Manifest

object Constants {
    const val USER_COLLECTION = "user"
    const val ADDRESS = "address"
    const val PRODUCTS = "products"
    const val TASK = "task"
    const val CART = "cart"
    const val BOOKMARKED_PRODUCTS = "bookmark"
    const val WEB_CLIENT_ID = "695830574128-pcphlfudg06au5anbd3qrr52c5uib1ao.apps.googleusercontent.com"
    const val USER_TACYCLE_COLLECTION = "tacycle"
    const val TACYCLE_ORDER_COLLECTION = "tacycle"
    const val REQUEST_ADDRESS = 101
    const val GUIDE_BOOK_LINK = "https://drive.google.com/drive/folders/14xRPblZVj8GMFq_cTQLqxv86JtRh3sj6?usp=share_link"
    val storagePermission = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
}