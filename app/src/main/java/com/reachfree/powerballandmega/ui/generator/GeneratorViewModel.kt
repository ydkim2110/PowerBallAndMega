package com.reachfree.powerballandmega.ui.generator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GeneratorViewModel : ViewModel() {

    private var _txtCount = MutableLiveData<String>()
    val txtCount = _txtCount

    private var _txtCountPowerPlay = MutableLiveData<String>()
    val txtCountPowerPlay = _txtCountPowerPlay

    init {
        _txtCount.value = "0 selected(Min 12)"
        _txtCountPowerPlay.value = "0 selected(Min 2)"
    }

    fun countSelectedItem(count: Int) {
        val result = "$count selected(Min 12)"
        _txtCount.value = result
    }

    fun countPowerPlaySelectedItem(count: Int) {
        val result = "$count selected(Min 2)"
        _txtCountPowerPlay.value = result
    }
}