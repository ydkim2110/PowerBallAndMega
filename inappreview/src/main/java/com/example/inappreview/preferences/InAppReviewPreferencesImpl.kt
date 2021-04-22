package com.example.inappreview.preferences

import android.content.SharedPreferences
import androidx.core.content.edit
import java.sql.Time
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class InAppReviewPreferencesImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : InAppReviewPreferences {

    companion object {
        const val KEY_IN_APP_REVIEW_PREFERENCES = "inAppReviewPreferences"
        private const val KEY_HAS_RATED_APP = "hasRateApp"
        private const val KEY_CHOSEN_RATE_LATER = "rateLater"
        private const val KEY_RATE_LATER_TIME = "rateLaterTime"
        private const val KEY_FIRST_TIME = "firstTime"
        private const val KEY_FIRST_LAUNCH = "firstLaunch"
    }

    override fun hasUserRatedApp(): Boolean =
        sharedPreferences.getBoolean(KEY_HAS_RATED_APP, false)

    override fun setUserRatedApp(hasRated: Boolean) =
        sharedPreferences.edit { putBoolean(KEY_HAS_RATED_APP, hasRated) }

    override fun hasUserChosenRateLater(): Boolean =
        sharedPreferences.getBoolean(KEY_CHOSEN_RATE_LATER, false)

    override fun setUserChosenRateLater(hasChosenRateLater: Boolean) =
        sharedPreferences.edit { putBoolean(KEY_CHOSEN_RATE_LATER, hasChosenRateLater) }

    override fun getRateLaterTime(): Long =
        sharedPreferences.getLong(KEY_RATE_LATER_TIME, System.currentTimeMillis())

    override fun setRateLater(time: Long) =
        sharedPreferences.edit { putLong(KEY_RATE_LATER_TIME, time) }

    override fun getFirstTime(): Long  =
        sharedPreferences.getLong(KEY_FIRST_TIME, System.currentTimeMillis())

    override fun setFirstTime(time: Long) =
        sharedPreferences.edit { putLong(KEY_FIRST_TIME, time) }

    override fun getFirstLaunch() =
        sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true)

    override fun setFirstLaunch(isFirst: Boolean) =
        sharedPreferences.edit { putBoolean(KEY_FIRST_LAUNCH, false) }

    override fun clearIfUserDidNotRate() {
        if (!hasUserRatedApp()) {
            sharedPreferences.edit {
                putBoolean(KEY_CHOSEN_RATE_LATER, false)
                putLong(KEY_RATE_LATER_TIME, 0)
            }
        }
    }
}