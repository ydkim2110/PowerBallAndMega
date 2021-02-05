package com.reachfree.powerballandmega.data.remote.api

import com.reachfree.powerballandmega.data.remote.response.GenericResponse
import com.reachfree.powerballandmega.data.remote.response.PowerBallResponse
import retrofit2.Response
import retrofit2.http.GET

interface PowerBallApi {

    @GET("d6yy-54nr.json")
    suspend fun getPowerBall(): Response<List<PowerBallResponse>>

    @GET("d6yy-54nr.json")
    suspend fun getPowerBallByGeneric(): Response<List<GenericResponse<PowerBallResponse>>>
}