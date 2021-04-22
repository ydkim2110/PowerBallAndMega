package com.example.inappreview.dialog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.inappreview.InAppReviewManager
import com.example.inappreview.R
import com.example.inappreview.databinding.FragmentInAppReviewPromptDialogBinding
import com.example.inappreview.preferences.InAppReviewPreferences
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class InAppReviewPromptDialog : DialogFragment() {

    @Inject
    lateinit var preferences: InAppReviewPreferences

//    @Inject
//    lateinit var generalSettingsPrefs: GeneralSettingsPrefs

    @Inject
    lateinit var inAppReviewManager: InAppReviewManager

    private var binding: FragmentInAppReviewPromptDialogBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInAppReviewPromptDialogBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        dialog?.setCanceledOnTouchOutside(false)
    }

    private fun initListeners() {
        val binding = binding ?: return

        binding.leaveReview.setOnClickListener { onLeaveReviewTapped() }
        binding.reviewLater.setOnClickListener { onRateLaterTapped() }
    }

    private fun onLeaveReviewTapped() {
        preferences.setUserRatedApp(true)
        inAppReviewManager.startReview(requireActivity())
        dismissAllowingStateLoss()
    }

    private fun onRateLaterTapped() {
        preferences.setUserChosenRateLater(true)
        preferences.setRateLater(getLaterTime())
        dismissAllowingStateLoss()
    }

    /**
     * Styles the dialog to have a transparent background and window insets.
     * */
    override fun onStart() {
        super.onStart()
        initStyle()
    }

    private fun initStyle() {
        val back = ColorDrawable(Color.TRANSPARENT)
        dialog?.window?.setBackgroundDrawable(back)

        dialog?.window?.setLayout(
            resources.getDimensionPixelSize(R.dimen.ratePromptWidth),
            resources.getDimensionPixelSize(R.dimen.ratePromptHeight)
        )

//        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
//        val modePreferences = generalSettingsPrefs.getNightMode()
//
//        val isInNightMode =
//            (modePreferences == AppCompatDelegate.MODE_NIGHT_YES
//                    || (modePreferences == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
//                    && currentNightMode == Configuration.UI_MODE_NIGHT_YES))
//
//        binding?.progressBar?.setImageResource(
//            if (isInNightMode) R.drawable.progress_white else R.drawable.progress_black
//        )
    }

    /**
     * If the user cancels the dialog, we process that as if they chose to "Rate Later".
     * */
    override fun onCancel(dialog: DialogInterface) {
        preferences.setUserChosenRateLater(true)
        preferences.setRateLater(getLaterTime())
        super.onCancel(dialog)
    }

    private fun getLaterTime(): Long {
        return System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)
    }
}