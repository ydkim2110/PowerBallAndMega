package com.reachfree.powerballandmega.ui.roulette

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.reachfree.powerballandmega.R
import com.reachfree.powerballandmega.data.local.LottoEntity
import com.reachfree.powerballandmega.databinding.RoulettePowerFragmentBinding
import com.reachfree.powerballandmega.ui.bottomsheet.GeneratorResultBottomSheetDialog
import com.reachfree.powerballandmega.utils.*
import com.reachfree.powerballandmega.viewmodels.LocalViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoulettePowerFragment : Fragment() {

    private var _binding: RoulettePowerFragmentBinding? = null
    private val binding get() = _binding!!

    private val localViewModel: LocalViewModel by viewModels()

    private val resultNumberList = ArrayList<Int>()
    private val randomNumberList = ArrayList<Int>()
    private val randomNumberPowerPlayList = ArrayList<Int>()
    private val selectedNumberList = ArrayList<Int>()
    private var sectors = Array(69) { i -> (i + 1).toString() }
    private var sectorsPowerPlay = Array(26) { i -> (i + 1).toString() }

    private var degree = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sectors.reverse()
        sectorsPowerPlay.reverse()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RoulettePowerFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupButton()
    }

    private fun setupButton() {
        binding.btnSpin.setOnSingleClickListener {
            if (binding.btnSpin.text == SAVE) {
                saveGeneratedNumber(resultNumberList)
            } else {
                if (resultNumberList.size < 5) {
                    spinWheel()
                }
                else if (resultNumberList.size == 5) {
                    spinPowerPlayWheel()
                }
                else {
                    requireActivity().toastShort("Press Restart Button.")
                }
            }
        }

        binding.txtReStart.setOnSingleClickListener {
            if (binding.btnSpin.text == SAVE) {
                binding.btnSpin.text = SPIN
            }
            clearAll()
        }
    }

    private fun saveGeneratedNumber(resultNumberList: ArrayList<Int>) {
        val lottoEntity = LottoEntity().apply {
            type = GeneratorResultBottomSheetDialog.TYPE_POWER
            category = Constants.CATEGORY_ROULETTE
            number1 = resultNumberList[0]
            number2 = resultNumberList[1]
            number3 = resultNumberList[2]
            number4 = resultNumberList[3]
            number5 = resultNumberList[4]
            number6 = resultNumberList[5]
        }

        requireContext().toastShort("Saving...")

        runDelayed(500L) {
            localViewModel.insertLotto(lottoEntity)
            binding.btnSpin.text = SPIN
            clearAll()
        }
    }

    private fun clearAll() {
        binding.txtIntro.text = "Pick 5 Number"
        binding.txtResult.text = "?"
        binding.wheel.setImageResource(R.drawable.ic_roulette_pick5)

        resultNumberList.clear()
        if (resultNumberList.size < 6) {
            binding.lottieFireworks.visibility = View.GONE
        }
        binding.layoutResultContainer.removeAllViewsInLayout()
    }

    private fun spinWheel()  {
        generateRandomNumber()
        removeSelectedNumberList()
        setDegreeAndDataToSelectedNumberList()
        rotateRoulette()
    }

    private fun spinPowerPlayWheel() {
        randomNumberPowerPlayList.clear()
        for (i in 1..26) {
            randomNumberPowerPlayList.add(i)
        }
        randomNumberPowerPlayList.shuffle()

        try {
            degree = DEGREE_BY_26 * randomNumberPowerPlayList[0]
        } catch (e: IndexOutOfBoundsException) {
            requireContext().toastShort("Unknown error occurred. Please try again.")
        } catch (e: Exception) {
            requireContext().toastShort("Unknown error occurred. Please try again.")
        }

        val rotateAnim = RotateAnimation(
            START_DEGREE,
            (degree + END_DEGREE),
            RotateAnimation.RELATIVE_TO_SELF,
            0.5f,
            RotateAnimation.RELATIVE_TO_SELF,
            0.5f
        )

        with(rotateAnim) {
            duration = ROTATE_DURATION
            fillAfter = true
            interpolator = DecelerateInterpolator()
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    calculatePowerPlayPoint(degree)

                    binding.btnSpin.activateButton()
                    binding.txtReStart.isClickable = true
                    binding.layoutResultContainer.removeAllViewsInLayout()
                    inflateNumber(resultNumberList, binding.layoutResultContainer)
                }

                override fun onAnimationStart(animation: Animation?) {
                    binding.txtResult.text = "?"
                    binding.btnSpin.inActivateButton()
                    binding.txtReStart.isClickable = false
                    binding.txtResult.setBackgroundResource(R.drawable.bg_ball_gray_roulette)
                    binding.txtResult.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                }
            })
        }

        binding.wheel.startAnimation(rotateAnim)
    }

    private fun calculatePowerPlayPoint(degree: Float) {
        var initialPoint = 0f
        var endPoint = DEGREE_BY_26

        var i = 0
        var res: String? = null

        do {
            if (degree in (initialPoint + 1f)..(endPoint + 1f)) {
                res = sectorsPowerPlay[i]
            }
            initialPoint += DEGREE_BY_26
            endPoint += DEGREE_BY_26
            i++
        } while (res == null && i < sectorsPowerPlay.size)

        binding.txtResult.text = res!!.toString()
        resultNumberList.add(res.toInt())

        if (resultNumberList.size == 5) {
            binding.txtIntro.text = "Pick 1 Number"
            binding.wheel.setImageResource(R.drawable.ic_roulette_pick1)
        }

        if (resultNumberList.size == 6) {
            binding.lottieFireworks.visibility = View.VISIBLE
            binding.btnSpin.text = SAVE
        }
    }

    private fun generateRandomNumber() {
        randomNumberList.clear()
        for (i in 1..69) {
            randomNumberList.add(i)
        }
        randomNumberList.shuffle()
    }

    private fun removeSelectedNumberList() {
        for (i in selectedNumberList.indices) {
            randomNumberList.remove(selectedNumberList[i])
        }
    }

    private fun setDegreeAndDataToSelectedNumberList() {
        try {
            degree = DEGREE_BY_69 * randomNumberList[0]
            val selectedNumber = randomNumberList[0]
            selectedNumberList.add(selectedNumber)
        } catch (e: IndexOutOfBoundsException) {
            requireContext().toastShort("Unknown error occurred. Please try again.")
        } catch (e: Exception) {
            requireContext().toastShort("Unknown error occurred. Please try again.")
        }
    }

    private fun rotateRoulette() {
        val rotateAnim = RotateAnimation(
            START_DEGREE,
            (degree + END_DEGREE),
            RotateAnimation.RELATIVE_TO_SELF,
            0.5f,
            RotateAnimation.RELATIVE_TO_SELF,
            0.5f
        )

        with(rotateAnim) {
            duration = ROTATE_DURATION
            fillAfter = true
            interpolator = DecelerateInterpolator()
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    calculatePoint(degree)

                    binding.btnSpin.activateButton()
                    binding.txtReStart.isClickable = true
                    binding.layoutResultContainer.removeAllViewsInLayout()
                    inflateNumber(resultNumberList, binding.layoutResultContainer)
                }

                override fun onAnimationStart(animation: Animation?) {
                    binding.txtResult.text = "?"
                    binding.btnSpin.inActivateButton()
                    binding.txtReStart.isClickable = false
                    binding.txtResult.setBackgroundResource(R.drawable.bg_ball_gray_roulette)
                    binding.txtResult.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                }
            })
        }

        binding.wheel.startAnimation(rotateAnim)
    }

    private fun calculatePoint(degree: Float) {
        var initialPoint = 0f
        var endPoint = DEGREE_BY_69

        var i = 0
        var res: String? = null

        do {
            if (degree in (initialPoint + 1f)..(endPoint + 1f)) {
                res = sectors[i]
            }
            initialPoint += DEGREE_BY_69
            endPoint += DEGREE_BY_69
            i++
        } while (res == null && i < sectors.size)

        binding.txtResult.text = res!!.toString()
        resultNumberList.add(res.toInt())

        if (resultNumberList.size == 5) {
            binding.txtIntro.text = "Pick 1 Number"
            binding.wheel.setImageResource(R.drawable.ic_roulette_pick1)
        }

        if (resultNumberList.size == 6) {
            binding.lottieFireworks.visibility = View.VISIBLE
        }
    }

    @Suppress("NAME_SHADOWING")
    private fun inflateNumber(numberList: ArrayList<Int>, layoutContainer: LinearLayout) {
        val view = View.inflate(requireActivity(), R.layout.num_select_list, null)
        val numberContainer = view.findViewById<LinearLayout>(R.id.selectedNumberContainer)
        numberContainer.gravity = Gravity.CENTER

        val lottoNum = TextView(requireActivity())

        for (i in numberList.indices) {
            val lottoNum = TextView(requireActivity())

            lottoNum.run {
                id = numberList[i]
                text = numberList[i].toString()
                textSize = 20F
                setTypeface(this.typeface, Typeface.BOLD)
                setTextColor(Color.BLACK)
                gravity = Gravity.CENTER
                paintFlags = 0
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                val size = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    45f,
                    resources.displayMetrics
                )
                params.width = size.toInt()
                params.height = size.toInt()
                params.setMargins(0, 0, 16, 0)
                layoutParams = params
            }
            lottoNum.setBackgroundResource(R.drawable.bg_ball_outline)
            if (i == 5) {
                lottoNum.setBackgroundResource(R.drawable.bg_power_ball)
                lottoNum.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            numberContainer.addView(lottoNum)
        }

        binding.txtResult.setTextColor(ContextCompat.getColor(requireActivity(), android.R.color.white))

        numberContainer.addView(lottoNum)

        layoutContainer.addView(numberContainer)

        val loadAnimation = AnimationUtils.loadAnimation(requireActivity(), R.anim.anim_text)
        for (i in 0 until numberContainer.childCount - 1) {
            if (i == numberContainer.childCount - 2) {
                numberContainer.getChildAt(i).startAnimation(loadAnimation)
            }
        }

    }

    companion object {
        fun newInstance() = RoulettePowerFragment()
        private const val START_DEGREE = 0f
        private const val END_DEGREE = 720f
        private const val DEGREE_BY_69 = 360f/69f
        private const val DEGREE_BY_26 = 360f/26f
        private const val ROTATE_DURATION = 2600L
        private const val SAVE = "Save"
        private const val SPIN = "Spin"
    }
}