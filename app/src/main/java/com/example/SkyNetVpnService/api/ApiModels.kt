package com.example.SkyNetVpnService.api

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("role") val role: String = "User"
)

data class AuthResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("userId") val userId: Int? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("isApproved") val isApproved: Boolean = false,
    @SerializedName("isSubscribed") val isSubscribed: Boolean = false,
    @SerializedName("token") val token: String? = null
)

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String,
    @SerializedName("isApproved") val isApproved: Boolean,
    @SerializedName("isSubscribed") val isSubscribed: Boolean,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class BaseResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

data class UpdateStatusRequest(
    @SerializedName("isSubscribed") val isSubscribed: Boolean
)
