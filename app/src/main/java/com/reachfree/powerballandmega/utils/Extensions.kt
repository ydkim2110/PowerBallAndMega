package com.reachfree.powerballandmega.utils

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.reachfree.powerballandmega.R

fun showADMOB(): Boolean {
    return (1..3).random() / 3 == 1
}

fun Activity.showWaitingDialog(): AlertDialog {
    val dialog = AlertDialog.Builder(this)
    dialog.setView(R.layout.dialog_waiting)
    dialog.setCancelable(false)
    return dialog.create()
}

fun Activity.showAnalyzingDialog(): AlertDialog {
    val dialog = AlertDialog.Builder(this).apply {
        setView(R.layout.dialog_analyzing)
        setCancelable(false)
    }
    return dialog.create()
}

fun Activity.showSimulatingDialog(): AlertDialog {
    val dialog = AlertDialog.Builder(this).apply {
        setView(R.layout.dialog_simulating)
        setCancelable(false)
    }
    return dialog.create()
}

fun MaterialButton.activateButton() {
    this.isEnabled = true
    this.isClickable = true
    this.backgroundTintList =
        ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.colorAction))
}

fun MaterialButton.inActivateButton() {
    this.isEnabled = false
    this.isClickable = false
    this.backgroundTintList =
        ColorStateList.valueOf(ContextCompat.getColor(this.context, R.color.colorIndicatorInactive))
}

fun Context.toastShort(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.toastLong(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

fun View.setOnSingleClickListener(action: (View) -> Unit) {
    val oneClick = OnSingleClickListener {
        action(it)
    }
    setOnClickListener(oneClick)
}

fun runDelayed(delayMillis: Long = 0, action: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(action, delayMillis)
}