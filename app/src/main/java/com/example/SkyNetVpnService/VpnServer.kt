package com.example.SkyNetVpnService

data class VpnServer(
    val ip: String,
    val location: String,
    var isSelected: Boolean = false
)
