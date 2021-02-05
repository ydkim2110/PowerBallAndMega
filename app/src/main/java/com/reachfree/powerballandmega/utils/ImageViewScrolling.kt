package com.reachfree.powerballandmega.utils

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.reachfree.powerballandmega.R
import com.reachfree.powerballandmega.databinding.ImageViewScrollingBinding
import com.reachfree.powerballandmega.ui.slot.IEventEnd
import com.reachfree.powerballandmega.ui.slot.ISlotEventEnd
import com.reachfree.powerballandmega.ui.slot.SlotAdapter

class ImageViewScrolling : FrameLayout {

    internal lateinit var eventEnd: IEventEnd
    internal lateinit var slotEventEnd: ISlotEventEnd
    internal var lastResult = 0
    internal var oldValue = 0

    private var _binding: ImageViewScrollingBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ANIMATION_DURATION = 150
    }

    val value: Int
        get() = Integer.parseInt(binding.imgNext.tag.toString())

    fun setEventEnd(eventEnd: IEventEnd) {
        this.eventEnd = eventEnd
    }

    fun setSlotEventEnd(slotEventEnd: ISlotEventEnd) {
        this.slotEventEnd = slotEventEnd
    }

    constructor(context: Context): super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        _binding = ImageViewScrollingBinding.inflate(LayoutInflater.from(context), this, true)
        binding.imgNext.translationY = height.toFloat()
    }

    fun setValueOrdered(image: Int, numRotate: Int, number: Int, type: String) {
        binding.textNo.visibility = View.GONE

        binding.imgCurrent.animate()
            .translationY((-height).toFloat())
            .setDuration(ANIMATION_DURATION.toLong())
            .start()

        binding.imgNext.translationY = binding.imgNext.height.toFloat()

        binding.imgNext.animate()
            .translationY(0f)
            .setDuration(ANIMATION_DURATION.toLong())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    setImage(binding.imgCurrent, oldValue % 6)

                    binding.imgCurrent.translationY = 0f
                    if (oldValue != numRotate) {
                        setValueOrdered(image, numRotate, number, type)
                        oldValue++
                    } else {
                        lastResult = 0
                        oldValue = 0
                        setLottoNumberImage(binding.imgNext, image, number, numRotate, type)
                        slotEventEnd.slotEventEnd(image%6, numRotate, number)
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}

            })
            .start()
    }

    private fun setLottoNumberImage(
        img: ImageView?,
        value: Int,
        number: Int,
        numRotate: Int,
        type: String
    ) {
        img!!.setImageResource(R.drawable.bg_slot_ball)
        binding.textNo.visibility = View.VISIBLE
        binding.textNo.setBackgroundResource(R.drawable.bg_ball_outline)
        binding.textNo.text = number.toString()

        if (numRotate == 30) {
            if (type == SlotAdapter.TYPE_POWER) {
                binding.textNo.setBackgroundResource(R.drawable.bg_power_ball)
                binding.textNo.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
            else if (type == SlotAdapter.TYPE_MEGA) {
                binding.textNo.setBackgroundResource(R.drawable.bg_mega_ball)
                binding.textNo.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
        }

        img.tag = value
        lastResult = value
    }

    private fun setImage(img: ImageView?, value: Int) {
        when (value) {
            Constants.BOMB -> {
                img!!.setImageResource(R.drawable.slot_bomb_done)
            }
            Constants.RAINBOW -> {
                img!!.setImageResource(R.drawable.slot_rainbow_done)
            }
            Constants.BEER -> {
                img!!.setImageResource(R.drawable.slot_beer_done)
            }
            Constants.CLOVER -> {
                img!!.setImageResource(R.drawable.slot_clover_done)
            }
            Constants.TRIPLE_CLOVER -> {
                img!!.setImageResource(R.drawable.slot_triple_clover_done)
            }
            Constants.RAT -> {
                img!!.setImageResource(R.drawable.slot_rat_done)
            }
        }

        img!!.tag = value
        lastResult = value
    }

}