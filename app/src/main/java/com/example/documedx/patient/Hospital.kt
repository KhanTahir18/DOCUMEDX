package com.example.documedx.patient

data class Hospital(
    val id: String,
    val name: String,
    val type: String,
    val phoneNumber: String,
    val email: String,
    val imageRes: Int = 0, // For drawable resource, use 0 if using URL
    val imageUrl: String = ""
)