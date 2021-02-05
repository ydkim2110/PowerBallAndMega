package com.reachfree.powerballandmega.ui.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.reachfree.powerballandmega.databinding.SplashActivityBinding
import com.reachfree.powerballandmega.ui.home.HomeActivity
import kotlinx.coroutines.delay

class SplashActivity : AppCompatActivity() {

    private var _binding: SplashActivityBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = SplashActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launchWhenCreated {
            delay(500)
            binding.motionLayout.transitionToEnd()
            delay(700)
            HomeActivity.start(this@SplashActivity)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}