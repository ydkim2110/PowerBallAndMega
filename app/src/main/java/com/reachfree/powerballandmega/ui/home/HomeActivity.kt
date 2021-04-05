package com.reachfree.powerballandmega.ui.home

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import com.example.inappreview.InAppReviewManager
import com.example.inappreview.InAppReviewView
import com.example.inappreview.dialog.InAppReviewPromptDialog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task
import com.reachfree.powerballandmega.BuildConfig
import com.reachfree.powerballandmega.R
import com.reachfree.powerballandmega.data.remote.response.MegaBallResponse
import com.reachfree.powerballandmega.data.remote.response.PowerBallResponse
import com.reachfree.powerballandmega.databinding.HomeActivityBinding
import com.reachfree.powerballandmega.ui.base.BaseActivity
import com.reachfree.powerballandmega.ui.generator.GeneratorActivity
import com.reachfree.powerballandmega.ui.megalist.MegaListActivity
import com.reachfree.powerballandmega.ui.powerlist.PowerListActivity
import com.reachfree.powerballandmega.ui.roulette.RouletteActivity
import com.reachfree.powerballandmega.ui.savednumber.SavedNumberActivity
import com.reachfree.powerballandmega.ui.scratch.ScratchActivity
import com.reachfree.powerballandmega.ui.simulation.mega.MegaSimulationActivity
import com.reachfree.powerballandmega.ui.simulation.power.PowerSimulationActivity
import com.reachfree.powerballandmega.ui.slot.SlotActivity
import com.reachfree.powerballandmega.utils.AppUtils
import com.reachfree.powerballandmega.utils.NetworkUtils
import com.reachfree.powerballandmega.utils.runDelayed
import com.reachfree.powerballandmega.utils.setOnSingleClickListener
import com.reachfree.powerballandmega.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity<HomeActivityBinding>({ HomeActivityBinding.inflate(it) }), InAppReviewView {

    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private lateinit var updateListener: InstallStateUpdatedListener

    private var doubleBackToExit = false

    private val homeViewModel: HomeViewModel by viewModels()

    private var arrayListOfMega: ArrayList<MegaBallResponse>? = null
    private var arrayListOfPower: ArrayList<PowerBallResponse>? = null

    private var powerBallJob: Job? = null
    private var megaBallJob: Job? = null
    private var adviceJob: Job? = null

    @Inject
    lateinit var inAppReviewManager: InAppReviewManager

    override fun onStart() {
        super.onStart()
        binding.layoutContainer.visibility = View.GONE
        binding.pbHome.visibility = View.VISIBLE

        powerBallJob = lifecycleScope.launchWhenStarted {
            homeViewModel.powerBall.collect { event ->
                when (event) {
                    is HomeViewModel.PowerBallEvent.Success -> {
                        val result = event.resultList
                        setupPowerNumber(result.first())
                        arrayListOfPower = ArrayList()
                        arrayListOfPower?.clear()
                        arrayListOfPower?.addAll(event.resultList)
                    }
                    is HomeViewModel.PowerBallEvent.Failure -> {

                    }
                    is HomeViewModel.PowerBallEvent.Loading -> {

                    }
                    else -> Unit
                }
            }
        }

        megaBallJob = lifecycleScope.launchWhenStarted {
            homeViewModel.megaBall.collect { event ->
                when (event) {
                    is HomeViewModel.MegaBallEvent.Success -> {
                        val result = event.resultList
                        setupMegaNumber(result.first())
                        arrayListOfMega = ArrayList()
                        arrayListOfMega?.clear()
                        arrayListOfMega?.addAll(event.resultList)

                        binding.layoutContainer.visibility = View.VISIBLE
                        binding.pbHome.visibility = View.GONE
                        binding.layoutTimeout.visibility = View.GONE
                    }
                    is HomeViewModel.MegaBallEvent.Failure -> {
                        binding.layoutContainer.visibility = View.VISIBLE
                        binding.pbHome.visibility = View.GONE

                        if (event.errorText.contains("TimeoutCancellationException")) {
                            binding.layoutContainer.visibility = View.GONE
                            binding.pbHome.visibility = View.GONE
                            binding.layoutTimeout.visibility = View.VISIBLE
                        }

                    }
                    is HomeViewModel.MegaBallEvent.Loading -> {

                    }
                    else -> Unit
                }
            }
        }

        adviceJob = lifecycleScope.launchWhenStarted {
            homeViewModel.advice.collect { event ->
                when (event) {
                    is HomeViewModel.AdviceEvent.Success -> {
                        binding.includeDrawer.txtAdvice.text = event.result.slip.advice
                    }
                    is HomeViewModel.AdviceEvent.Failure -> {

                    }
                    is HomeViewModel.AdviceEvent.Loading -> {

                    }
                    else -> {

                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    AppUpdateType.IMMEDIATE,
                    this,
                    REQUEST_CODE_UPDATE
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupNetworkCheck()
        setupUpdateCheck()
        setupToolbar()

        homeViewModel.setInAppReviewView(this)
        homeViewModel.openInAppReview()

        binding.cardviewMegaBall.setOnSingleClickListener {
            arrayListOfMega?.let {
                MegaListActivity.start(this, it)
            }
        }

        binding.cardviewPowerBall.setOnSingleClickListener {
            arrayListOfPower?.let {
                PowerListActivity.start(this, it)
            }
        }
    }

    private fun setupNetworkCheck() {
        NetworkUtils.getNetworkLiveData(applicationContext).observe(this) { isConnected ->
            if (isConnected) {
                binding.layoutConnected.visibility = View.VISIBLE
                binding.layoutDisconnected.visibility = View.GONE
                binding.layoutContainer.visibility = View.GONE
                binding.pbHome.visibility = View.VISIBLE
                homeViewModel.getRecentWinningNumbers()
                homeViewModel.getAdvice()
            } else {
                binding.layoutConnected.visibility = View.GONE
                binding.layoutDisconnected.visibility = View.VISIBLE
            }
        }
    }

    private fun setupUpdateCheck() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { handleUpdate(appUpdateManager, appUpdateInfoTask) }
    }

    private fun handleUpdate(manager: AppUpdateManager, info: Task<AppUpdateInfo>) {
        if (APP_UPDATE_TYPE_SUPPORTED == AppUpdateType.IMMEDIATE) {
            handleImmediateUpdate(info)
        } else if (APP_UPDATE_TYPE_SUPPORTED == AppUpdateType.FLEXIBLE) {
            handleFlexibleUpdate(info)
        }
    }

    private fun handleImmediateUpdate(info: Task<AppUpdateInfo>) {
        if(info.result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
            info.result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
            appUpdateManager.startUpdateFlowForResult(info.result, AppUpdateType.IMMEDIATE, this, REQUEST_CODE_UPDATE)
        }
    }


    private fun handleFlexibleUpdate(info: Task<AppUpdateInfo>) {
        if ((info.result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE ||
                    info.result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) &&
            info.result.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
            setUpdateAction(info)
        }
    }

    private fun setUpdateAction(info: Task<AppUpdateInfo>) {
        updateListener = InstallStateUpdatedListener {
            when (it.installStatus()) {
                InstallStatus.DOWNLOADED -> {
                    launchRestartDialog(appUpdateManager)
                }
            }
        }
    }

    private fun launchRestartDialog(manager: AppUpdateManager) {
        val builder = AlertDialog.Builder(this,
            R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Background)

        builder.setTitle(getString(R.string.dialog_update_title))
        builder.setMessage(getString(R.string.dialog_update_message))

        builder.setPositiveButton(getString(R.string.dialog_update_positive)) { dialog, _ ->
            manager.completeUpdate()
            dialog.dismiss()
        }

        val alertDialog = builder.create()

        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setBackgroundResource(android.R.color.transparent)
        }

        alertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_UPDATE && resultCode == Activity.RESULT_CANCELED) {
            finish()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar).apply { title = null }

        binding.toolbarTitle.text = "Power & Mega"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            binding.drawLayout.openDrawer(GravityCompat.START)
        }

        binding.includeDrawer.layoutPowerSimulation.setOnSingleClickListener {
            runAfterDrawerClose {
                arrayListOfPower?.let {
                    PowerSimulationActivity.start(this, it)
                }
            }
        }
        binding.includeDrawer.layoutMegaSimulation.setOnSingleClickListener {
            runAfterDrawerClose {
                arrayListOfMega?.let {
                    MegaSimulationActivity.start(this, it)
                }
            }
        }
        binding.includeDrawer.layoutGenerator.setOnSingleClickListener {
            runAfterDrawerClose { GeneratorActivity.start(this) }
        }
        binding.includeDrawer.layoutRoulette.setOnSingleClickListener {
            runAfterDrawerClose { RouletteActivity.start(this) }
        }
        binding.includeDrawer.layoutScratch.setOnSingleClickListener {
            runAfterDrawerClose { ScratchActivity.start(this) }
        }
        binding.includeDrawer.layoutSlot.setOnSingleClickListener {
            runAfterDrawerClose { SlotActivity.start(this) }
        }
        binding.includeDrawer.layoutSavedNumber.setOnSingleClickListener {
            runAfterDrawerClose { SavedNumberActivity.start(this) }
        }
    }

    override fun onStop() {
        super.onStop()
        powerBallJob?.cancel()
        megaBallJob?.cancel()
        adviceJob?.cancel()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        loadAds()
    }

    private fun loadAds() {
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        val adView = AdView(this)
        adView.adSize = AdSize.SMART_BANNER
        adView.adUnitId = BuildConfig.ADMOB_BANNER_ID

        val heightDP = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            AppUtils.getAdViewHeightDP(this).toFloat(),
            resources.displayMetrics
        ).toInt()

        binding.adContainer.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            heightDP
        )

        binding.adContainer.addView(adView)

        adView.loadAd(adRequest)
    }


    override fun onBackPressed() {
        if (binding.drawLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawLayout.closeDrawer(GravityCompat.START)
            return
        }

        if (doubleBackToExit) {
            finishAffinity()
        } else {
            Toast.makeText(this, getString(R.string.toast_exit),
                Toast.LENGTH_SHORT).show()
            doubleBackToExit = true
            runDelayed(TIME_EXIT_DELAY) {
                doubleBackToExit = false
            }
        }
    }

    private fun runAfterDrawerClose(action: () -> Unit) {
        binding.drawLayout.closeDrawer(GravityCompat.START)
        runDelayed(DRAWER_CLOSE_DELAY) { action() }
    }

    private fun setupPowerNumber(powerBallResponse: PowerBallResponse) {
        val arrayOfBall = arrayOf(
            binding.txtPowerNumber1, binding.txtPowerNumber2, binding.txtPowerNumber3,
            binding.txtPowerNumber4, binding.txtPowerNumber5, binding.txtPowerNumber6
        )
        val numbers = powerBallResponse.winning_numbers!!.split(" ").toTypedArray()
        for (i in numbers.indices) {
            arrayOfBall[i].text = numbers[i]
        }
        powerBallResponse.multiplier?.let {
            binding.txtPowerMultiplier.text = AppUtils.convertMultiplier(it, false)
        }
        binding.txtPowerWinningNumbersTitle.text =
            "Winning Numbers: ${AppUtils.convertDrawDate(powerBallResponse.draw_date!!)}"
    }

    private fun setupMegaNumber(megaBallResponse: MegaBallResponse) {
        val arrayOfBall = arrayOf(
            binding.txtMegaNumber1, binding.txtMegaNumber2, binding.txtMegaNumber3,
            binding.txtMegaNumber4, binding.txtMegaNumber5
        )
        val megaNumber = megaBallResponse.mega_ball
        val numbers = megaBallResponse.winning_numbers!!.split(" ").toTypedArray()
        for (i in numbers.indices) {
            arrayOfBall[i].text = numbers[i]
        }
        binding.txtMegaNumber6.text = megaNumber
        binding.txtMegaNumber6.setBackgroundResource(R.drawable.bg_mega_ball)
        binding.txtPowerNumber6.setTextColor(ContextCompat.getColor(this, R.color.white))

        megaBallResponse.multiplier?.let {
            binding.txtMegaMultiplier.text =
                AppUtils.convertMultiplier(megaBallResponse.multiplier, true)
        }
        binding.txtMegaWinningNumbersTitle.text =
            "Winning Numbers: ${AppUtils.convertDrawDate(megaBallResponse.draw_date!!)}"
    }

    companion object {
        const val DRAWER_CLOSE_DELAY = 300L
        const val TIME_EXIT_DELAY = 1500L

        private const val APP_UPDATE_TYPE_SUPPORTED = AppUpdateType.IMMEDIATE
        private const val REQUEST_CODE_UPDATE = 1212

        fun start(context: Context) {
            val intent = Intent(context, HomeActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun showReviewFlow() {
        if (inAppReviewManager.isEligibleForReview()) {
            val dialog = InAppReviewPromptDialog()

            dialog.show(supportFragmentManager, null)
        }
    }
}