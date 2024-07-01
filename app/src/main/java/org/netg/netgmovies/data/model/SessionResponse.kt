package org.netg.netgmovies.data.model

import com.google.gson.annotations.SerializedName

data class SessionResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("guest_session_id")
    val guestSessionId: String?,
    @SerializedName("expires_at")
    val expiresAt: String?,
    @SerializedName("status_code")
    val statusCode: Int?,
    @SerializedName("status_message")
    val statusMessage: String?
)