package com.example.inappreview

import android.app.Activity

interface InAppReviewManager {

    fun startReview(activity: Activity)

    fun isEligibleForReview(): Boolean

    fun isFirstTime()
}