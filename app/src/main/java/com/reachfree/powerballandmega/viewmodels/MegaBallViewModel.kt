package com.reachfree.powerballandmega.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reachfree.powerballandmega.data.remote.Resource
import com.reachfree.powerballandmega.data.remote.repository.MegaBallRepository
import com.reachfree.powerballandmega.data.remote.response.MegaBallResponse
import com.reachfree.powerballandmega.utils.DispatcherProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MegaBallViewModel @ViewModelInject constructor(
    private val repository: MegaBallRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    sealed class MegaBallEvent {
        class Success(val resultList: List<MegaBallResponse>): MegaBallEvent()
        class Failure(val errorText: String): MegaBallEvent()
        object Loading: MegaBallEvent()
        object Empty: MegaBallEvent()
    }

    private val _megaBall = MutableStateFlow<MegaBallEvent>(MegaBallEvent.Empty)
    val megaBall: StateFlow<MegaBallEvent> = _megaBall

    fun getMegaBall() {
        viewModelScope.launch(dispatchers.io) {
            when (val result = repository.getMegaBall()) {
                is Resource.Error -> {
                    _megaBall.value = MegaBallEvent.Failure(result.message!!)
                }
                is Resource.Success -> {
                    val megaBallList = result.data!!
                    _megaBall.value = MegaBallEvent.Success(megaBallList)
                }
            }
        }
    }

}