package com.reachfree.powerballandmega.utils

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import java.lang.NumberFormatException
import java.text.ParseException
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

object AppUtils {

    fun getAdViewHeightDP(activity: Activity): Int {
        return when (getScreenHeightDP(activity)) {
            in 0 until 400 -> 32
            in 400..720 -> 50
            else -> 90
        }
    }

    private fun getScreenHeightDP(activity: Activity): Int {
        val displayMetrics = activity.resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels / displayMetrics.density
        return screenHeight.roundToInt()
    }

    fun validated(vararg views: View): Boolean {
        var ok = true
        for (v in views) {
            if (v is EditText) {
                if (TextUtils.isEmpty(v.text.toString())) {
                    ok = false
                }
            }
        }
        return ok
    }

    fun dipToPx(context: Context, dipValue: Int): Float {
        val density = context.resources.displayMetrics.density
        return dipValue * density
    }

    fun convertMultiplier(multiplierText: String, isMega: Boolean = false): String  {
        return if (isMega) {
            try {
                "MEGAPLIER ${multiplierText.toInt()}X"
            } catch (e: NumberFormatException) {
                ""
            }
        } else {
            try {
                "Power Play ${multiplierText.toInt()}X"
            } catch (e: NumberFormatException) {
                ""
            }
        }
    }

    fun convertDrawDate(drawDate: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val toDateFormat = SimpleDateFormat("E, MM/dd/yy")
        return try {
            val date = dateFormat.parse(drawDate)
            toDateFormat.format(date!!)
        } catch (e: ParseException) {
            Log.e("ERROR", "ParseException: ${e.message}")
            "Error"
        }
    }

    fun generatePowerFiveNumber(): List<Int> {
        val ranNumber = ArrayList<Int>()

        for (i in 1..69) {
            ranNumber.add(i)
        }

        return ranNumber.shuffled().subList(0, 5).sorted()
    }

    fun generatePowerPlayNumber(): List<Int> {
        val ranNumber = ArrayList<Int>()

        for (i in 1..26) {
            ranNumber.add(i)
        }

        return ranNumber.shuffled().subList(0, 1).sorted()
    }
}