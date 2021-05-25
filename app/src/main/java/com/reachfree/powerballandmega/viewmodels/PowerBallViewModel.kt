package com.reachfree.powerballandmega.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reachfree.powerballandmega.data.remote.Resource
import com.reachfree.powerballandmega.data.remote.repository.PowerBallRepository
import com.reachfree.powerballandmega.data.remote.response.PowerBallResponse
import com.reachfree.powerballandmega.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PowerBallViewModel @Inject constructor(
    private val repository: PowerBallRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    sealed class PowerBallEvent {
        class Success(val resultList: List<PowerBallResponse>): PowerBallEvent()
        class Failure(val errorText: String): PowerBallEvent()
        object Loading: PowerBallEvent()
        object Empty: PowerBallEvent()
    }

    private val _powerBall = MutableStateFlow<PowerBallEvent>(PowerBallEvent.Empty)
    val powerBall: StateFlow<PowerBallEvent> = _powerBall

    fun getPowerBall() {
        viewModelScope.launch(dispatchers.io) {
            when (val result = repository.getPowerBall()) {
                is Resource.Error -> {
                    _powerBall.value = PowerBallEvent.Failure(result.message!!)
                }
                is Resource.Success -> {
                    val powerBallList = result.data!!
                    _powerBall.value = PowerBallEvent.Success(powerBallList)
                }
            }
        }
    }

}