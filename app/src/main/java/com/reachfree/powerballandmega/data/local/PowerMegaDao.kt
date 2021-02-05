package com.reachfree.powerballandmega.data.local

import androidx.room.*

@Dao
interface PowerMegaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLotto(lottoEntity: LottoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLottoList(lottoList: List<LottoEntity>)

    @Query("SELECT * FROM lotto_table")
    suspend fun getAllLotto(): List<LottoEntity>

    @Query("SELECT * FROM lotto_table WHERE type = :type")
    suspend fun getLottoByType(type: String): List<LottoEntity>

    @Delete
    suspend fun deleteLotto(lottoEntity: LottoEntity)

}