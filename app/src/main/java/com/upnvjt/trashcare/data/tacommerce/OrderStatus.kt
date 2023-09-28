package com.upnvjt.trashcare.data.tacommerce

sealed class OrderStatus(val orderStatus: String) {
    object OnGoing: OrderStatus("On Process")
    object Done: OrderStatus("Done")
    object Declined: OrderStatus("Declined")
}

fun getOrderStatus(cycleStatus: String): OrderStatus {
    return when (cycleStatus) {
        "On Process" -> { OrderStatus.OnGoing }

        "Declined" -> { OrderStatus.Declined }

        else -> { OrderStatus.Done }
    }
}