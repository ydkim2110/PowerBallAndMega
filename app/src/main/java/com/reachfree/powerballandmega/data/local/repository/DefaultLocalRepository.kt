package com.reachfree.powerballandmega.data.local.repository

import android.util.Log
import com.reachfree.powerballandmega.data.local.LottoEntity
import com.reachfree.powerballandmega.data.local.PowerMegaDao
import com.reachfree.powerballandmega.data.remote.Resource
import javax.inject.Inject

class DefaultLocalRepository @Inject constructor(
    private val dao: PowerMegaDao
) : LocalRepository {

    override suspend fun insertLotto(lottoEntity: LottoEntity) {
        return dao.insertLotto(lottoEntity)
    }

    override suspend fun insertLottoList(lottoList: List<LottoEntity>) {
        try {
            return dao.insertLottoList(lottoList)
        } catch (e: Exception) {
            Log.d("DEBUG", "ERROR: ${e.message}")
        }
    }

    override suspend fun getAllLotto(): Resource<List<LottoEntity>> {
        val result = dao.getAllLotto()
        return try {
            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred.")
        }
    }

    override suspend fun getLottoByType(type: String): Resource<List<LottoEntity>> {
        val result = dao.getLottoByType(type)
        return try {
            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred.")
        }
    }

    override suspend fun deleteLotto(lottoEntity: LottoEntity) {
        return dao.deleteLotto(lottoEntity)
    }
}