package com.upnvjt.trashcare.data.tacampaign

sealed class TaskStatus(val taskStatus: String) {
    object Submitted: TaskStatus("Submitted")
    object Incomplete: TaskStatus("Incomplete")

}