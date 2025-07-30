package com.documedx.models

data class Patient(
    val id: String,
    val name: String,
    val reportCount: Int
)

data class MedicalReport(
    val id: String,
    val title: String,
    val receivedDate: String,
    val filePath: String
)