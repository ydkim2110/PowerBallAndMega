package com.reachfree.powerballandmega.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [LottoEntity::class],
    version = 1
)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun getPowerMegaDao(): PowerMegaDao

}