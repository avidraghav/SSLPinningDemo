package com.raghav.sslpinningdemo.ui

import com.raghav.sslpinningdemo.DemoResponse
import retrofit2.Response
import retrofit2.http.GET

interface DemoApi {
    @GET("/demo_response")
    suspend fun getDemoResponse(): Response<DemoResponse>
}
