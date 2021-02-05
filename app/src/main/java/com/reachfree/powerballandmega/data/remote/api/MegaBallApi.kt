package com.reachfree.powerballandmega.data.remote.api

import com.reachfree.powerballandmega.data.remote.response.GenericResponse
import com.reachfree.powerballandmega.data.remote.response.MegaBallResponse
import retrofit2.Response
import retrofit2.http.GET

interface MegaBallApi {

    @GET("5xaw-6ayf.json")
    suspend fun getMegaBall(): Response<List<MegaBallResponse>>


    @GET("5xaw-6ayf.json")
    suspend fun getMegaBallByGeneric(): Response<List<GenericResponse<MegaBallResponse>>>

}