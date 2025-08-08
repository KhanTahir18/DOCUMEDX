package com.example.documedx.patient

data class Report(
    val id: String,
    val title: String,
    val time: String,
    val date: String,
    val filePath: String,
    val reportId: String,
    val fromHospital: String = "",
    val hasPrescription: Boolean = false
)