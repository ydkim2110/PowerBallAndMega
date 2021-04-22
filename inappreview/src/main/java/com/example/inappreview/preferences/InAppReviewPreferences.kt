package com.example.inappreview.preferences

interface InAppReviewPreferences {

    fun hasUserRatedApp(): Boolean

    fun setUserRatedApp(hasRated: Boolean)

    fun hasUserChosenRateLater(): Boolean

    fun setUserChosenRateLater(hasChosenRateLater: Boolean)

    fun getRateLaterTime(): Long

    fun setRateLater(time: Long)

    fun getFirstTime(): Long

    fun setFirstTime(time: Long)

    fun getFirstLaunch(): Boolean

    fun setFirstLaunch(isFirst: Boolean)

    fun clearIfUserDidNotRate()

}