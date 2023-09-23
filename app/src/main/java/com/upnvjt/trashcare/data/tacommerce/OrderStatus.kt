package com.upnvjt.trashcare.data.tacommerce

sealed class OrderStatus(val orderStatus: String) {
    object OnGoing: OrderStatus("On Process")
    object Done: OrderStatus("Done")

}

fun getOrderStatus(cycleStatus: String): OrderStatus {
    return when (cycleStatus) {
        "On Process" -> { OrderStatus.OnGoing }

        else -> { OrderStatus.Done }
    }
}