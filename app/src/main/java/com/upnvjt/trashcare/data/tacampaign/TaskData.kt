package com.upnvjt.trashcare.data.tacampaign

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TaskData(
    val taskStatus: String = "",
    val id: String = "",
    val tugas: List<String> = emptyList(),
    val campaignNo: Int = 0,
    val photoUrl: String? = null,
    val userId: String? = null
): Parcelable

val dailyTask1 = arrayListOf(
    "Mengambil Sampah di lingkungan sekitar",
    "Menggunakan tempat sampah berdasarkan kategori"
)

val dailyTask2 = arrayListOf(
    "Melakukan penanaman pohon di lingkungan sekitar",
    "Membersihkan got di lingkungan rumah dari sampah"
)

val dailyTask3 = arrayListOf(
    "Melakukan sosialisasi pembuangan sampah pada lingkungan sekitar"
)