package org.netg.netgmovies.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    @Qualifiers.BaseUrl
    fun provideBaseUrl(): String = "https://api.themoviedb.org/3/"

    @Provides
    @Singleton
    @Qualifiers.AccessToken
    fun provideAccessToken(): String =
        "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIzNDYzOTc4ZWVjODdlZDk0ZWM1ZTI1OTc1ODk0ZmVkMyIsIm5iZiI6MTcxOTE2ODI3NC42NzQyODcsInN1YiI6IjUwOGNhMDc5MTljMjk1NjVkNjAwMDc1YSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.KQWr4VC5QViloJL9HEktZhV-5UFbjVN7eRo1TCOKnC0"

    @Provides
    @Singleton
    fun providesGson(): Gson =
        GsonBuilder().enableComplexMapKeySerialization().serializeNulls().setLenient()
            .create()

    @Provides
    @Singleton
    fun providesRetrofit(
        @Qualifiers.BaseUrl baseUrl: String,
        gson: Gson,
        okHttpClient: OkHttpClient.Builder
    ): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(
            GsonConverterFactory.create(gson)
        ).client(okHttpClient.build()).build()
    }

    @Provides
    @Singleton
    fun providesOkHttp(@Qualifiers.AccessToken accessToken: String): OkHttpClient.Builder {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).addInterceptor { chain ->
            val requestBuilder: Request.Builder = chain.request().newBuilder()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", accessToken)
            val request: Request = requestBuilder.build()
            chain.proceed(request)
        }.readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
        return httpClient
    }


}

interface Qualifiers {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AccessToken

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class BaseUrl
}