package com.upnvjt.trashcare.data.tacycle

sealed class TaCycleStatus(val cycleStatus: String) {
    object OnGoing: TaCycleStatus("On Process")
    object Done: TaCycleStatus("Done")
}

fun getCycleStatus(cycleStatus: String): TaCycleStatus {
    return when (cycleStatus) {
        "On Process" -> { TaCycleStatus.OnGoing }

        else -> { TaCycleStatus.Done }
    }
}