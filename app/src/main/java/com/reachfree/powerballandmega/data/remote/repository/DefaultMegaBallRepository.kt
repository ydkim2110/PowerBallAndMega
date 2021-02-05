package com.reachfree.powerballandmega.data.remote.repository

import com.reachfree.powerballandmega.data.remote.Resource
import com.reachfree.powerballandmega.data.remote.api.MegaBallApi
import com.reachfree.powerballandmega.data.remote.response.GenericResponse
import com.reachfree.powerballandmega.data.remote.response.MegaBallResponse
import javax.inject.Inject

class DefaultMegaBallRepository @Inject constructor(
    private val api: MegaBallApi
) : MegaBallRepository {

    override suspend fun getMegaBall(): Resource<List<MegaBallResponse>> {
        return try {
            val response = api.getMegaBall()
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred.")
        }
    }

    override suspend fun getMegaBallByGeneric(): Resource<List<GenericResponse<MegaBallResponse>>> {
        return try {
            val response = api.getMegaBallByGeneric()
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred.")
        }
    }

}