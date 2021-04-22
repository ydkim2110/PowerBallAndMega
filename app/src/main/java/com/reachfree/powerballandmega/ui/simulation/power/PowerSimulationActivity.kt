package com.reachfree.powerballandmega.ui.simulation.power

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.reachfree.powerballandmega.BuildConfig
import com.reachfree.powerballandmega.data.remote.response.PowerBallResponse
import com.reachfree.powerballandmega.databinding.PowerSimulationActivityBinding
import com.reachfree.powerballandmega.ui.base.BaseActivity
import com.reachfree.powerballandmega.ui.powerlist.PowerListActivity
import com.reachfree.powerballandmega.utils.*
import com.reachfree.powerballandmega.utils.Constants.ROUND_POWER
import com.reachfree.powerballandmega.utils.Constants.ROUND_POWER_PLAY

class PowerSimulationActivity : BaseActivity<PowerSimulationActivityBinding>({ PowerSimulationActivityBinding.inflate(it) }) {

    override var animationKind = ANIMATION_DEFAULT

    private lateinit var simulationAdapter: SimulationPowerAdapter
    private var passedData: ArrayList<PowerBallResponse>? = null

    private lateinit var analyzingDialog: AlertDialog

    private lateinit var simulationResultDialog: SimulationPowerResultDialog

    private var waitingDialog: AlertDialog? = null
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        passedData = intent.getParcelableArrayListExtra(EXTRA_POWER_BALL_LIST)

        loadAds()
        setupToolbar()
        setupRecyclerView()
        setupEditText()
        setupButton()
    }

    override fun onDestroy() {
        super.onDestroy()
        waitingDialog?.let {
            if (it.isShowing) it.dismiss()
        }
    }

    private fun loadAds() {
        binding.adView.loadAd(AdRequest.Builder().build())
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar).apply {
            title = null
        }

        binding.toolbarTitle.text = "Power Simulation"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupRecyclerView() {
        binding.rvResult.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@PowerSimulationActivity)
            addItemDecoration(DividerItemDecoration(this@PowerSimulationActivity, LinearLayoutManager.VERTICAL))
        }
    }

    private fun setupEditText() {
        binding.edtNumber1.requestFocus()

        binding.edtNumber1.addTextChangedListener(powerTextWatcher)
        binding.edtNumber2.addTextChangedListener(powerTextWatcher)
        binding.edtNumber3.addTextChangedListener(powerTextWatcher)
        binding.edtNumber4.addTextChangedListener(powerTextWatcher)
        binding.edtNumber5.addTextChangedListener(powerTextWatcher)
        binding.edtNumber6.addTextChangedListener(powerTextWatcher)
    }

    private fun setupButton() {
        binding.btnSimulate.inActivateButton()
        binding.btnSimulate.setOnSingleClickListener {
            if (checkDuplicateNumber()) {
                toastShort("Please check duplicated Number.")
                return@setOnSingleClickListener
            }

            analyzingDialog = showSimulatingDialog()
            analyzingDialog.show()
            runDelayed(SIMULATION_TIME_DELAY) {
                analyzingDialog.dismiss()

                val myNumber = arrayOf(
                    binding.edtNumber1.text.toString().trim(),
                    binding.edtNumber2.text.toString().trim(),
                    binding.edtNumber3.text.toString().trim(),
                    binding.edtNumber4.text.toString().trim(),
                    binding.edtNumber5.text.toString().trim(),
                    binding.edtNumber6.text.toString().trim()
                )

                val isPowerPlay = binding.switchPowerPlay.isChecked
                val resultList = simulatePowerBallResult(myNumber, isPowerPlay)
                simulationAdapter = SimulationPowerAdapter()
                binding.rvResult.adapter = simulationAdapter
                simulationAdapter.submitList(resultList)

                simulationResultDialog = SimulationPowerResultDialog(resultList)
                simulationResultDialog.show(supportFragmentManager, SimulationPowerResultDialog.DIALOG_TAG)
            }
        }
    }

    @Suppress("NAME_SHADOWING")
    private fun simulatePowerBallResult(myNumber: Array<String>, isPowerPlay: Boolean): List<PowerModel> {
        val resultPowerModelList = arrayListOf<PowerModel>()

        for (i in passedData!!.indices) {
            val win = passedData!![i].winning_numbers?.split(" ")?.toTypedArray()!!

            var powerBallHit = false
            if (win[win.size - 1].toInt() == myNumber[myNumber.size - 1].toInt()) {
                powerBallHit = true
            }

            var count = 0
            for (i in 0 until win.size - 1) {
                for (j in 0 until win.size - 1) {
                    if (win[i].toInt() == myNumber[j].toInt()) {
                        count++
                    }
                }
            }

            if (count == 5 && powerBallHit) {
                val powerModel = PowerModel(passedData!![i], RANK.RANK_POWER_FIRST, powerBallHit, isPowerPlay)
                resultPowerModelList.add(powerModel)
                continue
            }
            if (count == 5) {
                val powerModel = PowerModel(passedData!![i], RANK.RANK_POWER_SECOND, powerBallHit, isPowerPlay)
                resultPowerModelList.add(powerModel)
                continue
            }
            if (count == 4 && powerBallHit) {
                val powerModel = PowerModel(passedData!![i], RANK.RANK_POWER_THIRD, powerBallHit, isPowerPlay)
                resultPowerModelList.add(powerModel)
                continue
            }
            if (count == 4) {
                val powerModel = PowerModel(passedData!![i], RANK.RANK_POWER_FIRST, powerBallHit, isPowerPlay)
                resultPowerModelList.add(powerModel)
                continue
            }
            if (count == 3 && powerBallHit) {
                val powerModel = PowerModel(passedData!![i], RANK.RANK_POWER_FIFTH, powerBallHit, isPowerPlay)
                resultPowerModelList.add(powerModel)
                continue
            }
            if (count == 3) {
                val powerModel = PowerModel(passedData!![i], RANK.RANK_POWER_SIXTH, powerBallHit, isPowerPlay)
                resultPowerModelList.add(powerModel)
                continue
            }
            if (count == 2 && powerBallHit) {
                val powerModel = PowerModel(passedData!![i], RANK.RANK_POWER_SEVENTH, powerBallHit, isPowerPlay)
                resultPowerModelList.add(powerModel)
                continue
            }
            if (count == 1 && powerBallHit) {
                val powerModel = PowerModel(passedData!![i], RANK.RANK_POWER_EIGHTH, powerBallHit, isPowerPlay)
                resultPowerModelList.add(powerModel)
                continue
            }
            if (powerBallHit) {
                val powerModel = PowerModel(passedData!![i], RANK.RANK_POWER_NINTH, powerBallHit, isPowerPlay)
                resultPowerModelList.add(powerModel)
                continue
            }
        }

        return resultPowerModelList
    }


    private val powerTextWatcher = object: TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            var number = 0
            try {
                number = s.toString().toInt()
            } catch (e: NumberFormatException) {
                Log.d("DEBUG", "ERROR: ${e.message}")
            }

            if (s.toString() == binding.edtNumber1.text.toString()) {
                if (checkPowerNumber(number)) {
                    binding.edtNumber1.setText("")
                }
            }
            else if (s.toString() == binding.edtNumber2.text.toString()) {
                if (checkPowerNumber(number)) {
                    binding.edtNumber2.setText("")
                }
            }
            else if (s.toString() == binding.edtNumber3.text.toString()) {
                if (checkPowerNumber(number)) {
                    binding.edtNumber3.setText("")
                }
            }
            else if (s.toString() == binding.edtNumber4.text.toString()) {
                if (checkPowerNumber(number)) {
                    binding.edtNumber4.setText("")
                }
            }
            else if (s.toString() == binding.edtNumber5.text.toString()) {
                if (checkPowerNumber(number)) {
                    binding.edtNumber5.setText("")
                }
            }
            else if (s.toString() == binding.edtNumber6.text.toString()) {
                if (checkPowerPlayNumber(number)) {
                    binding.edtNumber6.setText("")
                }
            }

            val isValidate = AppUtils.validated(
                binding.edtNumber1,
                binding.edtNumber2,
                binding.edtNumber3,
                binding.edtNumber4,
                binding.edtNumber5,
                binding.edtNumber6
            )

            if (isValidate) {
                binding.btnSimulate.activateButton()
            } else {
                binding.btnSimulate.inActivateButton()
            }
        }
    }

    private fun checkDuplicateNumber(): Boolean {
        var result = false
        val edits = arrayOf(
            binding.edtNumber1.text.toString().trim(),
            binding.edtNumber2.text.toString().trim(),
            binding.edtNumber3.text.toString().trim(),
            binding.edtNumber4.text.toString().trim(),
            binding.edtNumber5.text.toString().trim()
        )

        val afterDistinct = edits.distinct()

        if (afterDistinct.size < edits.size) {
            result = true
        }

        return result
    }

    private fun checkPowerNumber(number: Int): Boolean {
        return number > ROUND_POWER
    }

    private fun checkPowerPlayNumber(number: Int): Boolean {
        return number > ROUND_POWER_PLAY
    }

    override fun onBackPressed() {
        if (showADMOB()) {
            waitingDialog = showWaitingDialog()
            waitingDialog?.show()

            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                this,
                BuildConfig.ADMOB_INTERSTITIALAD_ID,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        waitingDialog?.dismiss()
                        mInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        mInterstitialAd = interstitialAd
                        mInterstitialAd?.show(this@PowerSimulationActivity)

                        mInterstitialAd?.fullScreenContentCallback =
                            object : FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {
                                    runDelayed(100) {
                                        waitingDialog?.dismiss()
                                        finish()
                                    }
                                }

                                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                                    super.onAdFailedToShowFullScreenContent(adError)
                                }

                                override fun onAdShowedFullScreenContent() {
                                    mInterstitialAd = null
                                }
                            }

                    }
                })
        } else {
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val SIMULATION_TIME_DELAY = 3000L
        const val EXTRA_POWER_BALL_LIST = "extra_power_ball_list"
        fun start(context: Context, powerList: ArrayList<PowerBallResponse>) {
            val intent = Intent(context, PowerSimulationActivity::class.java).apply {
                putParcelableArrayListExtra(EXTRA_POWER_BALL_LIST, powerList)
            }
            context.startActivity(intent)
        }
    }
}