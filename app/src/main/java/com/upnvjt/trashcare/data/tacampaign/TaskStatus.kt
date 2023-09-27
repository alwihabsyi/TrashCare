package com.upnvjt.trashcare.data.tacampaign

sealed class TaskStatus(val taskStatus: String) {
    object Complete: TaskStatus("Complete")
    object Submitted: TaskStatus("Submitted")
    object Incomplete: TaskStatus("Incomplete")

}

fun getTaskStatus(taskStatus: String): TaskStatus {
    return when (taskStatus) {
        "Complete" -> { TaskStatus.Complete }

        "Submitted" -> { TaskStatus.Submitted }

        else -> { TaskStatus.Incomplete }
    }
}