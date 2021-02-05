package com.reachfree.powerballandmega.data.remote.repository

import com.reachfree.powerballandmega.data.remote.Resource
import com.reachfree.powerballandmega.data.remote.response.AdviceResponse

interface AdviceRepository {

    suspend fun getAdvice(): Resource<AdviceResponse>

}