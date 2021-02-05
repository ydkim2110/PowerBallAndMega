package com.reachfree.powerballandmega.ui.generator

import android.widget.CheckBox

interface GenerateNumberCallback {

    fun generateNumberList(
        generatedNumberList: ArrayList<GeneratedNumber>,
        checkBoxList: ArrayList<CheckBox>
    )

}