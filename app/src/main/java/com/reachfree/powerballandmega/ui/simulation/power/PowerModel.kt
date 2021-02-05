package com.reachfree.powerballandmega.ui.simulation.power

import com.reachfree.powerballandmega.data.remote.response.PowerBallResponse
import com.reachfree.powerballandmega.utils.RANK

data class PowerModel(
    val powerBall: PowerBallResponse,
    val rank: RANK,
    val isPowerBallHit: Boolean,
    val isPowerPlay: Boolean
)