package com.reachfree.powerballandmega.ui.powerlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.reachfree.powerballandmega.data.remote.response.PowerBallResponse
import com.reachfree.powerballandmega.databinding.PowerListActivityBinding
import com.reachfree.powerballandmega.ui.base.BaseActivity

class PowerListActivity : BaseActivity<PowerListActivityBinding>({ PowerListActivityBinding.inflate(it) }) {

    override var animationKind = ANIMATION_SLIDE_FROM_BOTTOM

    private val powerAdapter: PowerListAdapter by lazy { PowerListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()

        val data = intent.getParcelableArrayListExtra<PowerBallResponse>(EXTRA_POWER_BALL_LIST)
        try {
            for (i in data!!.indices) {
                if (data[i].multiplier == null) {
                    Log.d("DEBUG", "NULL: ${data[i].draw_date}")
                }
            }
        } catch (e: Exception) {
            Log.d("DEBUG", "ERROR: ${e.message}")
        }

        with(binding) {
            rvPowerBallList.layoutManager = LinearLayoutManager(this@PowerListActivity)
            powerAdapter.submitList(data!!)
            rvPowerBallList.adapter = powerAdapter
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar).apply {
            title = null
        }

        binding.toolbarTitle.text = "Power Ball"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
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
        const val EXTRA_POWER_BALL_LIST = "extra_power_ball_list"
        fun start(context: Context, powerList: ArrayList<PowerBallResponse>) {
            val intent = Intent(context, PowerListActivity::class.java).apply {
                putParcelableArrayListExtra(EXTRA_POWER_BALL_LIST, powerList)
            }
            context.startActivity(intent)
        }
    }
}