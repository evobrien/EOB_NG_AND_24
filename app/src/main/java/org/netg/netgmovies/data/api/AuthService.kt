package org.netg.netgmovies.data.api

import org.netg.netgmovies.data.model.SessionResponse
import retrofit2.http.GET


interface AuthService {

    @GET("authentication/guest_session/new")
    suspend fun createSession(): SessionResponse

    @GET("authentication")
    suspend fun validateKey(): SessionResponse
}