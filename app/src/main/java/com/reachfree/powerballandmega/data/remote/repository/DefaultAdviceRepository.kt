package com.reachfree.powerballandmega.data.remote.repository

import com.reachfree.powerballandmega.data.remote.Resource
import com.reachfree.powerballandmega.data.remote.api.AdviceApi
import com.reachfree.powerballandmega.data.remote.response.AdviceResponse
import javax.inject.Inject

class DefaultAdviceRepository @Inject constructor(
    private val api: AdviceApi
) : AdviceRepository {
    override suspend fun getAdvice(): Resource<AdviceResponse> {
        return try {
            val response = api.getAdvice()
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