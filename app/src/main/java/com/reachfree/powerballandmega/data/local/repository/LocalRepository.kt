package com.reachfree.powerballandmega.data.local.repository

import com.reachfree.powerballandmega.data.local.LottoEntity
import com.reachfree.powerballandmega.data.remote.Resource

interface LocalRepository {

    suspend fun insertLotto(lottoEntity: LottoEntity)
    suspend fun insertLottoList(lottoList: List<LottoEntity>)
    suspend fun getAllLotto(): Resource<List<LottoEntity>>
    suspend fun getLottoByType(type: String): Resource<List<LottoEntity>>
    suspend fun deleteLotto(lottoEntity: LottoEntity)

}