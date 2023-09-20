package com.upnvjt.trashcare.data.tacampaign

data class TaskData(
    val id: Int,
    val nama: String,
    val listTugas : List<String>,
    val photoUrl: String? = null
)