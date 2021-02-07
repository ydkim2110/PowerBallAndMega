package com.reachfree.powerballandmega.ui.scratch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.ads.InterstitialAd
import com.reachfree.powerballandmega.R
import com.reachfree.powerballandmega.databinding.ScratchActivityBinding
import com.reachfree.powerballandmega.ui.common.GamePagerAdapter
import com.reachfree.powerballandmega.ui.base.BaseActivity
import com.reachfree.powerballandmega.utils.loadAds
import com.reachfree.powerballandmega.utils.showADMOB
import com.reachfree.powerballandmega.utils.showWaitingDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScratchActivity : BaseActivity<ScratchActivityBinding>({ ScratchActivityBinding.inflate(it) }) {

    override var animationKind = ANIMATION_SLIDE_FROM_RIGHT

    private var gamePagerAdapter: GamePagerAdapter? = null

    private var waitingDialog: AlertDialog? = null
    private lateinit var interstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()
        setupGamePager()
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

        binding.toolbarTitle.text = "Scratch"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupGamePager() {
        gamePagerAdapter = GamePagerAdapter(supportFragmentManager).apply {
            addFragment(ScratchPowerFragment.newInstance(), getString(R.string.power_ball))
            addFragment(ScratchMegaFragment.newInstance(), getString(R.string.mega_millions))
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
            val intent = Intent(context, ScratchActivity::class.java)
            context.startActivity(intent)
        }
    }
}