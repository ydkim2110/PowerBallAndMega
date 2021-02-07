package com.reachfree.powerballandmega.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.reachfree.powerballandmega.R

abstract class BaseActivity<B: ViewBinding>(
    val bindingFactory: (LayoutInflater) -> B
) : AppCompatActivity() {

    private var _binding: B? = null
    val binding get() = _binding!!

    protected open var animationKind = ANIMATION_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingFactory(layoutInflater)
        setContentView(binding.root)

        overridePendingEnterTransition()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun overridePendingEnterTransition() {
        when (animationKind) {
            ANIMATION_SLIDE_FROM_RIGHT ->
                overridePendingTransition(R.anim.slide_from_right, R.anim.fade_scale_out)
            ANIMATION_SLIDE_FROM_BOTTOM ->
                overridePendingTransition(R.anim.slide_from_bottom, R.anim.fade_scale_out)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingExitTransition()
    }

    private fun overridePendingExitTransition() {
        when (animationKind) {
            ANIMATION_SLIDE_FROM_RIGHT ->
                overridePendingTransition(R.anim.fade_scale_in, R.anim.slide_to_right)
            ANIMATION_SLIDE_FROM_BOTTOM ->
                overridePendingTransition(R.anim.fade_scale_in, R.anim.slide_to_bottom)
        }
    }

    companion object {
        const val ANIMATION_DEFAULT = 0
        const val ANIMATION_SLIDE_FROM_RIGHT = 1
        const val ANIMATION_SLIDE_FROM_BOTTOM = 2
    }
}