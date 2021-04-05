package com.reachfree.powerballandmega.viewmodels

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inappreview.InAppReviewView
import com.reachfree.powerballandmega.data.remote.Resource
import com.reachfree.powerballandmega.data.remote.repository.AdviceRepository
import com.reachfree.powerballandmega.data.remote.repository.MegaBallRepository
import com.reachfree.powerballandmega.data.remote.repository.PowerBallRepository
import com.reachfree.powerballandmega.data.remote.response.AdviceResponse
import com.reachfree.powerballandmega.data.remote.response.GenericResponse
import com.reachfree.powerballandmega.data.remote.response.MegaBallResponse
import com.reachfree.powerballandmega.data.remote.response.PowerBallResponse
import com.reachfree.powerballandmega.ui.home.HomeActivity
import com.reachfree.powerballandmega.utils.DispatcherProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel @ViewModelInject constructor(
    private val powerRepository: PowerBallRepository,
    private val megaRepository: MegaBallRepository,
    private val adviceRepository: AdviceRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    init {
        getRecentWinningNumbers()
        getAdvice()
    }

    sealed class PowerBallEvent {
        class Success(val resultList: List<PowerBallResponse>): PowerBallEvent()
        class Failure(val errorText: String): PowerBallEvent()
        object Loading: PowerBallEvent()
        object Empty: PowerBallEvent()
    }

    private val _powerBall = MutableStateFlow<PowerBallEvent>(PowerBallEvent.Empty)
    val powerBall: StateFlow<PowerBallEvent> = _powerBall

    sealed class MegaBallEvent {
        class Success(val resultList: List<MegaBallResponse>): MegaBallEvent()
        class Failure(val errorText: String): MegaBallEvent()
        object Loading: MegaBallEvent()
        object Empty: MegaBallEvent()
    }

    private val _megaBall = MutableStateFlow<MegaBallEvent>(MegaBallEvent.Empty)
    val megaBall: StateFlow<MegaBallEvent> = _megaBall

    sealed class AdviceEvent {
        class Success(val result: AdviceResponse): AdviceEvent()
        class Failure(val errorText: String): AdviceEvent()
        object Loading: AdviceEvent()
        object Empty: AdviceEvent()
    }

    private val _advice = MutableStateFlow<AdviceEvent>(AdviceEvent.Empty)
    val advice: StateFlow<AdviceEvent> = _advice

    fun getAdvice() {
        viewModelScope.launch(dispatchers.io) {
            try {
                when (val result = adviceRepository.getAdvice()) {
                    is Resource.Error -> {
                        _advice.value = AdviceEvent.Failure(result.message!!)
                    }
                    is Resource.Success -> {
                        _advice.value = AdviceEvent.Success(result.data!!)
                    }
                }
            } catch (e: Exception) {
                Log.d("DEBUG", "${e.message}")
            }
        }
    }

    fun getRecentWinningNumbers() {
        viewModelScope.launch(dispatchers.io) {
            try {
                withTimeout(TIMEOUT_DELAY) {
                    val task1 = async { powerRepository.getPowerBall() }
                    val task2 = async { megaRepository.getMegaBall() }

                    processData(task1.await(), task2.await())
                }
            } catch (e: TimeoutCancellationException) {
                _megaBall.value = MegaBallEvent.Failure("TimeoutCancellationException")
            } catch (e: Exception) {
                Log.d("DEBUG", "${e.message}")
            }
        }
    }

    private fun processData(
        powerBallResponse: Resource<List<PowerBallResponse>>,
        megaBallResponse: Resource<List<MegaBallResponse>>
    ) {
        when (powerBallResponse) {
            is Resource.Error -> {
                _powerBall.value = PowerBallEvent.Failure(powerBallResponse.message!!)
            }
            is Resource.Success -> {
                val powerBallList = powerBallResponse.data!!
                _powerBall.value = PowerBallEvent.Success(powerBallList)
            }
        }

        when (megaBallResponse) {
            is Resource.Error -> {
                _megaBall.value = MegaBallEvent.Failure(megaBallResponse.message!!)
            }
            is Resource.Success -> {
                val megaBallList = megaBallResponse.data!!
                _megaBall.value = MegaBallEvent.Success(megaBallList)
            }
        }
    }

    private lateinit var inAppReviewView: InAppReviewView

    fun setInAppReviewView(inAppReviewView: InAppReviewView) {
        this.inAppReviewView = inAppReviewView
    }

    fun openInAppReview() {
        inAppReviewView.showReviewFlow()
    }

    companion object {
        private const val TIMEOUT_DELAY = 10000L
    }

}