package com.example.inappreview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.inappreview.preferences.InAppReviewPreferences
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.tasks.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

class InAppReviewManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val reviewManager: ReviewManager,
    private val inAppReviewPreferences: InAppReviewPreferences
) : InAppReviewManager {

    companion object {
        private const val KEY_REVIEW = "reviewFlow"
    }

    private var reviewInfo: ReviewInfo? = null

    init {
        if (isEligibleForReview()) {
            reviewManager.requestReviewFlow().addOnCompleteListener {
                if (it.isComplete && it.isSuccessful) {
                    this.reviewInfo = it.result
                }
            }
        }
    }

    override fun isEligibleForReview(): Boolean {
        return (!inAppReviewPreferences.hasUserRatedApp()
                && !inAppReviewPreferences.hasUserChosenRateLater() && enoughTimePassed())
                || (inAppReviewPreferences.hasUserChosenRateLater() && enoughTimePassed())
    }

    private fun enoughTimePassed(): Boolean {
        val rateLaterTimestamp = inAppReviewPreferences.getRateLaterTime()

        return abs(rateLaterTimestamp - System.currentTimeMillis()) >= TimeUnit.DAYS.toMillis(2)
    }

    override fun startReview(activity: Activity) {
        if (reviewInfo != null) {
            reviewManager.launchReviewFlow(activity, reviewInfo).addOnCompleteListener { reviewFlow ->
                onReviewFlowLaunchCompleted(reviewFlow)
            }
        } else {
            sendUserToPlayStore()
        }
    }

    private fun onReviewFlowLaunchCompleted(reviewFlow: Task<Void>) {
        if (reviewFlow.isSuccessful) {
            logSuccess()
        } else {
            sendUserToPlayStore()
        }
    }

    private fun sendUserToPlayStore() {
        val appPackageName = context.packageName
        Log.d("DEBUG", "appPackageName: $appPackageName")
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        } catch (error: Exception) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }
    }

    private fun logSuccess() {
        if (BuildConfig.DEBUG) {
            Log.d(KEY_REVIEW, "Review complete!")
        }
    }

}