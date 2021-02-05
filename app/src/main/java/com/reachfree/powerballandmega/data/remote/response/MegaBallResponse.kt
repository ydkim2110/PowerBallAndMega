package com.reachfree.powerballandmega.data.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MegaBallResponse(
    val draw_date: String? = "",
    val winning_numbers: String? = "",
    val mega_ball: String? = "",
    val multiplier: String? = "",
) : Parcelable

