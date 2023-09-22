package com.upnvjt.trashcare.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.upnvjt.trashcare.data.tacommerce.Cart
import com.upnvjt.trashcare.util.Constants.CART
import com.upnvjt.trashcare.util.Constants.USER_COLLECTION

class FirebaseCommon(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth
) {

    private val cartCollection =
        firestore.collection(USER_COLLECTION).document(auth.uid!!).collection(CART)

    fun addProduct(cartProduct: Cart, onResult: (Cart?, Exception?) -> Unit) {
        cartCollection.document(cartProduct.product.id).set(cartProduct)
            .addOnSuccessListener {
                onResult(cartProduct, null)
            }
            .addOnFailureListener {
                onResult(null, it)
            }
    }

    fun increaseQuantity(
        documentId: String,
        orderAmount: Int,
        onResult: (String?, Exception?) -> Unit
    ) {
        firestore.runTransaction { transaction ->
            val documentRef = cartCollection.document(documentId)
            val document = transaction.get(documentRef)
            val productObject = document.toObject(Cart::class.java)
            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity + orderAmount
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transaction.set(documentRef, newProductObject)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

}