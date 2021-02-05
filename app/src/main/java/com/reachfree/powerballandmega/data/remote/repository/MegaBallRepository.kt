package com.reachfree.powerballandmega.data.remote.repository

import com.reachfree.powerballandmega.data.remote.Resource
import com.reachfree.powerballandmega.data.remote.response.GenericResponse
import com.reachfree.powerballandmega.data.remote.response.MegaBallResponse

interface MegaBallRepository {

    suspend fun getMegaBall(): Resource<List<MegaBallResponse>>
    suspend fun getMegaBallByGeneric(): Resource<List<GenericResponse<MegaBallResponse>>>

}