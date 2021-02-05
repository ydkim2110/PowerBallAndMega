package com.reachfree.powerballandmega.data.remote.api

import com.reachfree.powerballandmega.data.remote.response.AdviceResponse
import retrofit2.Response
import retrofit2.http.GET

interface AdviceApi {

    @GET("advice")
    suspend fun getAdvice(): Response<AdviceResponse>

}