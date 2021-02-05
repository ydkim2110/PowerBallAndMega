package com.reachfree.powerballandmega.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "lotto_table"
)
data class LottoEntity(
    var type: String? = "",
    var category: Int? = 0,
    var number1: Int? = 0,
    var number2: Int? = 0,
    var number3: Int? = 0,
    var number4: Int? = 0,
    var number5: Int? = 0,
    var number6: Int? = 0
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
