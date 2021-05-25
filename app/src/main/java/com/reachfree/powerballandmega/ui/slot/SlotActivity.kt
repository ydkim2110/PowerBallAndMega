package com.reachfree.powerballandmega.ui.slot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.reachfree.powerballandmega.BuildConfig
import com.reachfree.powerballandmega.R
import com.reachfree.powerballandmega.databinding.SlotActivityBinding
import com.reachfree.powerballandmega.ui.common.GamePagerAdapter
import com.reachfree.powerballandmega.ui.base.BaseActivity
import com.reachfree.powerballandmega.utils.runDelayed
import com.reachfree.powerballandmega.utils.showADMOB
import com.reachfree.powerballandmega.utils.showWaitingDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SlotActivity : BaseActivity<SlotActivityBinding>({ SlotActivityBinding.inflate(it) }) {

    override var animationKind = ANIMATION_SLIDE_FROM_RIGHT

    private var gamePagerAdapter: GamePagerAdapter? = null

    private var waitingDialog: AlertDialog? = null
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadAds()
        setupToolbar()
        setupGamePager()
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

        binding.toolbarTitle.text = "Slot"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupGamePager() {
        gamePagerAdapter = GamePagerAdapter(supportFragmentManager).apply {
            addFragment(SlotPowerFragment.newInstance(), getString(R.string.power_ball))
            addFragment(SlotMegaFragment.newInstance(), getString(R.string.mega_millions))
        }

        binding.vpContent.adapter = gamePagerAdapter
        binding.tab.setupWithViewPager(binding.vpContent)
    }

    override fun onBackPressed() {
        if (SlotPowerFragment.isAnimated || SlotMegaFragment.isAnimated) return

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
                        mInterstitialAd?.show(this@SlotActivity)

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
        fun start(context: Context) {
            val intent = Intent(context, SlotActivity::class.java)
            context.startActivity(intent)
        }
    }
}