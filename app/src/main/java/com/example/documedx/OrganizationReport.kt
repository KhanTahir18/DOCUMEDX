package com.example.documedx

import android.net.Uri

data class OrganizationReport(
    val id: String? = null,
    val fromOrg: String? = null,
    val toPatient: String? = null,
    val dateWhenSharred: String? = null,
    val timeWhenShared: String? = null,
    val reportUrl: String? = null
)