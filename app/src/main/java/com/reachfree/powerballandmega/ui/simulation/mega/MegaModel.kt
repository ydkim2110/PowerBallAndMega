package com.reachfree.powerballandmega.ui.simulation.mega

import com.reachfree.powerballandmega.data.remote.response.MegaBallResponse
import com.reachfree.powerballandmega.data.remote.response.PowerBallResponse
import com.reachfree.powerballandmega.utils.RANK

data class MegaModel(
    val megaBall: MegaBallResponse,
    val rank: RANK,
    val isMegaBallHit: Boolean,
    val isMegaPlay: Boolean
)