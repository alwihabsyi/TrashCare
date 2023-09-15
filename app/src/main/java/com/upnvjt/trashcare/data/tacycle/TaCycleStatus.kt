package com.upnvjt.trashcare.data.tacycle

sealed class TaCycleStatus(val cycleStatus: String) {
    object OnGoing: TaCycleStatus("OnGoing")
    object Done: TaCycleStatus("Done")

}

fun getCycleStatus(cycleStatus: String): TaCycleStatus {
    return when (cycleStatus) {
        "OnGoing" -> { TaCycleStatus.OnGoing }

        else -> { TaCycleStatus.Done }
    }
}