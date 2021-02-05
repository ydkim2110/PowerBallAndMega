package com.reachfree.powerballandmega.data.remote.repository

import android.util.Log
import com.reachfree.powerballandmega.data.remote.api.PowerBallApi
import com.reachfree.powerballandmega.data.remote.response.PowerBallResponse
import com.reachfree.powerballandmega.data.remote.Resource
import com.reachfree.powerballandmega.data.remote.response.GenericResponse
import javax.inject.Inject

class DefaultPowerBallRepository @Inject constructor(
    private val api: PowerBallApi
) : PowerBallRepository {

    override suspend fun getPowerBall(): Resource<List<PowerBallResponse>> {
        return try {
            val response = api.getPowerBall()
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

    override suspend fun getPowerBallByGeneric(): Resource<List<GenericResponse<PowerBallResponse>>> {
        return try {
            val response = api.getPowerBallByGeneric()
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