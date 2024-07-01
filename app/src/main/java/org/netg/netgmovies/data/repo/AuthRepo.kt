package org.netg.netgmovies.data.repo

import org.netg.netgmovies.data.api.AuthService
import org.netg.netgmovies.data.model.SessionResponse
import javax.inject.Inject

interface AuthRepo {
    suspend fun createSession(): SessionResponse
}

class AuthRepoImpl @Inject constructor(private val authService: AuthService) : AuthRepo {
    override suspend fun createSession(): SessionResponse {
        return authService.createSession()
    }

}
