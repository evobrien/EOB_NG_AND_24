package org.netg.netgmovies.data.api

import org.netg.netgmovies.data.model.MovieSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieSearchService {

    @GET("search/movie")
    suspend fun searchByName(
        @Query("query") query: String,
        @Query("Page") page: Int,
        @Query("include_adult") includeAdult: Boolean = false
    ): MovieSearchResponse

}