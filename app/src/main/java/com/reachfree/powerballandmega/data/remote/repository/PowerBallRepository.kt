package com.reachfree.powerballandmega.data.remote.repository

import com.reachfree.powerballandmega.data.remote.response.PowerBallResponse
import com.reachfree.powerballandmega.data.remote.Resource
import com.reachfree.powerballandmega.data.remote.response.GenericResponse

interface PowerBallRepository {

    suspend fun getPowerBall(): Resource<List<PowerBallResponse>>
    suspend fun getPowerBallByGeneric(): Resource<List<GenericResponse<PowerBallResponse>>>

}