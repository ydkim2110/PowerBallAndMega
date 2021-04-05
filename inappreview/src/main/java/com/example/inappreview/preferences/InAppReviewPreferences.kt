package com.example.inappreview.preferences

interface InAppReviewPreferences {

    fun hasUserRatedApp(): Boolean

    fun setUserRatedApp(hasRated: Boolean)

    fun hasUserChosenRateLater(): Boolean

    fun setUserChosenRateLater(hasChosenRateLater: Boolean)

    fun getRateLaterTime(): Long

    fun setRateLater(time: Long)

    fun clearIfUserDidNotRate()

}