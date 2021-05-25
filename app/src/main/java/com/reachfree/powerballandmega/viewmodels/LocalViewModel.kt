package com.reachfree.powerballandmega.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reachfree.powerballandmega.data.local.LottoEntity
import com.reachfree.powerballandmega.data.local.repository.LocalRepository
import com.reachfree.powerballandmega.data.remote.Resource
import com.reachfree.powerballandmega.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    sealed class LocalDatabaseEvent {
        class Success(val resultList: List<LottoEntity>): LocalDatabaseEvent()
        class Failure(val errorText: String): LocalDatabaseEvent()
        object Loading: LocalDatabaseEvent()
        object Empty: LocalDatabaseEvent()
    }

    private val _lottoResponse = MutableStateFlow<LocalDatabaseEvent>(LocalDatabaseEvent.Empty)
    val lottoResponse: StateFlow<LocalDatabaseEvent> = _lottoResponse

    fun insertLotto(lottoEntity: LottoEntity) {
        viewModelScope.launch(dispatchers.io) {
            try {
                localRepository.insertLotto(lottoEntity)
            } catch (e: Exception) {
                Log.d("DEBUG", "ERROR: ${e.message}")
            }
        }
    }

    fun insertLottoList(lottoList: List<LottoEntity>) {
        viewModelScope.launch(dispatchers.io) {
            try {
                localRepository.insertLottoList(lottoList)
            } catch (e: Exception) {
                Log.d("DEBUG", "ERROR: ${e.message}")
            }
        }
    }

    fun getLottoByType(type: String) {
        viewModelScope.launch(dispatchers.io) {
            _lottoResponse.value = LocalDatabaseEvent.Loading
            try {
                when (val result = localRepository.getLottoByType(type)) {
                    is Resource.Error -> _lottoResponse.value = LocalDatabaseEvent.Failure(result.message!!)
                    is Resource.Success -> {
                        if (result.data == null) {
                            _lottoResponse.value = LocalDatabaseEvent.Failure("Unexpected error.")
                        } else {
                            _lottoResponse.value = LocalDatabaseEvent.Success(result.data)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("DEBUG", "ERROR: ${e.message}")
            }
        }
    }

    fun getAllLotto()  {
        viewModelScope.launch(dispatchers.io) {
            _lottoResponse.value = LocalDatabaseEvent.Loading
            try {
                when (val result = localRepository.getAllLotto()) {
                    is Resource.Error -> _lottoResponse.value = LocalDatabaseEvent.Failure(result.message!!)
                    is Resource.Success -> {
                        if (result.data == null) {
                            _lottoResponse.value = LocalDatabaseEvent.Failure("Unexpected error.")
                        } else {
                            _lottoResponse.value = LocalDatabaseEvent.Success(result.data)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("DEBUG", "ERROR: ${e.message}")
            }
        }
    }

    fun deleteLotto(lottoEntity: LottoEntity) {
        viewModelScope.launch(dispatchers.io) {
            try {
                localRepository.deleteLotto(lottoEntity)
            } catch (e: Exception) {
                Log.d("DEBUG", "ERROR: ${e.message}")
            }
        }
    }
}