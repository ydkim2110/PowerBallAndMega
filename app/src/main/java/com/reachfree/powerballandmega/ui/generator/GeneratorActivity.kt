package com.reachfree.powerballandmega.ui.generator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.ads.InterstitialAd
import com.reachfree.powerballandmega.R
import com.reachfree.powerballandmega.databinding.GeneratorActivityBinding
import com.reachfree.powerballandmega.ui.base.BaseActivity
import com.reachfree.powerballandmega.ui.common.GamePagerAdapter
import com.reachfree.powerballandmega.utils.loadAds
import com.reachfree.powerballandmega.utils.showADMOB
import com.reachfree.powerballandmega.utils.showWaitingDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GeneratorActivity : BaseActivity() {

    private var _binding: GeneratorActivityBinding? = null
    private val binding get() = _binding!!

    override var animationKind = ANIMATION_SLIDE_FROM_RIGHT

    private var gamePagerAdapter: GamePagerAdapter? = null
    private var waitingDialog: AlertDialog? = null

    private lateinit var interstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = GeneratorActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupGamePager()
    }

    override fun onDestroy() {
        super.onDestroy()
        waitingDialog?.let {
            if (it.isShowing) it.dismiss()
        }
        _binding = null
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar).apply {
            title = null
        }

        binding.toolbarTitle.text = "Number Generator"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupGamePager() {
        gamePagerAdapter = GamePagerAdapter(supportFragmentManager).apply {
            addFragment(GeneratorPowerFragment.newInstance(), getString(R.string.power_ball))
            addFragment(GeneratorMegaFragment.newInstance(), getString(R.string.mega_millions))
        }

        binding.vpContent.adapter = gamePagerAdapter
        binding.tab.setupWithViewPager(binding.vpContent)
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
        fun start(context: Context) {
            val intent = Intent(context, GeneratorActivity::class.java)
            context.startActivity(intent)
        }
    }

}