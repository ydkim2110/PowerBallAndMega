package com.reachfree.powerballandmega.ui.savednumber

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.reachfree.powerballandmega.R
import com.reachfree.powerballandmega.databinding.SavedNumberActivityBinding
import com.reachfree.powerballandmega.ui.common.GamePagerAdapter
import com.reachfree.powerballandmega.ui.base.BaseActivity
import com.reachfree.powerballandmega.ui.savednumber.mega.SavedNumberMegaFragment
import com.reachfree.powerballandmega.ui.savednumber.power.SavedNumberPowerFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedNumberActivity : BaseActivity<SavedNumberActivityBinding>({ SavedNumberActivityBinding.inflate(it) }) {

    override var animationKind = ANIMATION_SLIDE_FROM_RIGHT

    private var gamePagerAdapter: GamePagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()
        setupGamePager()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar).apply {
            title = null
        }

        binding.toolbarTitle.text = "Saved Number"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupGamePager() {
        gamePagerAdapter = GamePagerAdapter(supportFragmentManager).apply {
            addFragment(SavedNumberPowerFragment.newInstance(), getString(R.string.power_ball))
            addFragment(SavedNumberMegaFragment.newInstance(), getString(R.string.mega_millions))
        }

        binding.vpContent.adapter = gamePagerAdapter
        binding.tab.setupWithViewPager(binding.vpContent)
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
            val intent = Intent(context, SavedNumberActivity::class.java)
            context.startActivity(intent)
        }
    }
}