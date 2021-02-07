package com.reachfree.powerballandmega.ui.simulation.mega

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
import com.google.android.gms.ads.InterstitialAd
import com.reachfree.powerballandmega.data.remote.response.MegaBallResponse
import com.reachfree.powerballandmega.data.remote.response.PowerBallResponse
import com.reachfree.powerballandmega.databinding.MegaSimulationActivityBinding
import com.reachfree.powerballandmega.ui.base.BaseActivity
import com.reachfree.powerballandmega.ui.megalist.MegaListActivity
import com.reachfree.powerballandmega.utils.*
import com.reachfree.powerballandmega.utils.Constants.ROUND_MEGA
import com.reachfree.powerballandmega.utils.Constants.ROUND_MEGA_PLAY

class MegaSimulationActivity : BaseActivity<MegaSimulationActivityBinding>({ MegaSimulationActivityBinding.inflate(it) }) {

    override var animationKind = ANIMATION_DEFAULT

    private lateinit var simulationMegaAdapter: SimulationMegaAdapter
    private var passedData: ArrayList<MegaBallResponse>? = null

    private lateinit var analyzingDialog: AlertDialog

    private lateinit var simulationResultDialog: SimulationMegaResultDialog

    private var waitingDialog: AlertDialog? = null
    private lateinit var interstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        passedData = intent.getParcelableArrayListExtra(EXTRA_MEGA_BALL_LIST)

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

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar).apply {
            title = null
        }

        binding.toolbarTitle.text = "Mega Simulation"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }


    private fun setupRecyclerView() {
        binding.rvResult.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MegaSimulationActivity)
            addItemDecoration(DividerItemDecoration(this@MegaSimulationActivity, LinearLayoutManager.VERTICAL))
        }
    }

    private fun setupEditText() {
        binding.edtNumber1.addTextChangedListener(megaTextWatcher)
        binding.edtNumber2.addTextChangedListener(megaTextWatcher)
        binding.edtNumber3.addTextChangedListener(megaTextWatcher)
        binding.edtNumber4.addTextChangedListener(megaTextWatcher)
        binding.edtNumber5.addTextChangedListener(megaTextWatcher)
        binding.edtNumber6.addTextChangedListener(megaTextWatcher)
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

                val isMegaPlay = binding.switchPowerPlay.isChecked
                val resultList = simulateMegaBallResult(myNumber, isMegaPlay)
                simulationMegaAdapter = SimulationMegaAdapter()
                binding.rvResult.adapter = simulationMegaAdapter
                simulationMegaAdapter.submitList(resultList)

                simulationResultDialog = SimulationMegaResultDialog(resultList)
                simulationResultDialog.show(supportFragmentManager, SimulationMegaResultDialog.DIALOG_TAG)
            }
        }
    }

    fun <T> append(arr: Array<T>, element: T): Array<T?> {
        val array = arr.copyOf(arr.size + 1)
        array[arr.size] = element
        return array
    }

    @Suppress("NAME_SHADOWING")
    private fun simulateMegaBallResult(myNumber: Array<String>, isMegaPlay: Boolean): List<MegaModel> {
        val resultMegaModelList = arrayListOf<MegaModel>()

        for (i in passedData!!.indices) {
            val win = passedData!![i].winning_numbers?.split(" ")?.toTypedArray()!!
            val megaBall = passedData!![i].mega_ball?.toInt() ?: 0

            var megaBallHit = false
            if (megaBall == myNumber[myNumber.size - 1].toInt()) {
                megaBallHit = true
            }

            var count = 0
            if (megaBallHit) {
                count++
            }
            for (i in 0 until win.size - 1) {
                for (j in 0 until win.size - 1) {
                    if (win[i].toInt() == myNumber[j].toInt()) {
                        count++
                    }
                }
            }

            if (count == 5 && megaBallHit) {
                val megaModel = MegaModel(passedData!![i], RANK.RANK_MEGA_FIRST, megaBallHit, isMegaPlay)
                resultMegaModelList.add(megaModel)
                continue
            }
            if (count == 5) {
                val megaModel = MegaModel(passedData!![i], RANK.RANK_MEGA_SECOND, megaBallHit, isMegaPlay)
                resultMegaModelList.add(megaModel)
                continue
            }
            if (count == 4 && megaBallHit) {
                val megaModel = MegaModel(passedData!![i], RANK.RANK_MEGA_THIRD, megaBallHit, isMegaPlay)
                resultMegaModelList.add(megaModel)
                continue
            }
            if (count == 4) {
                val megaModel = MegaModel(passedData!![i], RANK.RANK_MEGA_FOURTH, megaBallHit, isMegaPlay)
                resultMegaModelList.add(megaModel)
                continue
            }
            if (count == 3 && megaBallHit) {
                val megaModel = MegaModel(passedData!![i], RANK.RANK_MEGA_FIFTH, megaBallHit, isMegaPlay)
                resultMegaModelList.add(megaModel)
                continue
            }
            if (count == 3) {
                val megaModel = MegaModel(passedData!![i], RANK.RANK_MEGA_SIXTH, megaBallHit, isMegaPlay)
                resultMegaModelList.add(megaModel)
                continue
            }
            if (count == 2 && megaBallHit) {
                val megaModel = MegaModel(passedData!![i], RANK.RANK_MEGA_SEVENTH, megaBallHit, isMegaPlay)
                resultMegaModelList.add(megaModel)
                continue
            }
            if (count == 1 && megaBallHit) {
                val megaModel = MegaModel(passedData!![i], RANK.RANK_MEGA_EIGHTH, megaBallHit, isMegaPlay)
                resultMegaModelList.add(megaModel)
                continue
            }
            if (megaBallHit) {
                val megaModel = MegaModel(passedData!![i], RANK.RANK_MEGA_NINTH, megaBallHit, isMegaPlay)
                resultMegaModelList.add(megaModel)
                continue
            }
        }

        return resultMegaModelList
    }

    private val megaTextWatcher = object: TextWatcher {
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
                if (checkMegaNumber(number)) {
                    binding.edtNumber1.setText("")
                }
            }
            else if (s.toString() == binding.edtNumber2.text.toString()) {
                if (checkMegaNumber(number)) {
                    binding.edtNumber2.setText("")
                }
            }
            else if (s.toString() == binding.edtNumber3.text.toString()) {
                if (checkMegaNumber(number)) {
                    binding.edtNumber3.setText("")
                }
            }
            else if (s.toString() == binding.edtNumber4.text.toString()) {
                if (checkMegaNumber(number)) {
                    binding.edtNumber4.setText("")
                }
            }
            else if (s.toString() == binding.edtNumber5.text.toString()) {
                if (checkMegaNumber(number)) {
                    binding.edtNumber5.setText("")
                }
            }
            else if (s.toString() == binding.edtNumber6.text.toString()) {
                if (checkMegaPlayNumber(number)) {
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

    private fun checkMegaNumber(number: Int): Boolean {
        return number > ROUND_MEGA
    }

    private fun checkMegaPlayNumber(number: Int): Boolean {
        return number > ROUND_MEGA_PLAY
    }

    override fun onBackPressed() {
        if (showADMOB()) {
            waitingDialog = showWaitingDialog()
            waitingDialog?.show()
            interstitialAd = loadAds(this, waitingDialog)

            if (interstitialAd.isLoaded) {
                interstitialAd.show()
                return
            }

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
        const val EXTRA_MEGA_BALL_LIST = "extra_mega_ball_list"
        fun start(context: Context, megaList: ArrayList<MegaBallResponse>) {
            val intent = Intent(context, MegaSimulationActivity::class.java).apply {
                putExtra(EXTRA_MEGA_BALL_LIST, megaList)
            }
            context.startActivity(intent)
        }
    }
}