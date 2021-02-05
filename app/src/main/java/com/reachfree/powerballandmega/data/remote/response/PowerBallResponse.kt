package com.reachfree.powerballandmega.data.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PowerBallResponse(
    val draw_date: String? = "",
    val winning_numbers: String? = "",
    val multiplier: String? = ""
) : Parcelable