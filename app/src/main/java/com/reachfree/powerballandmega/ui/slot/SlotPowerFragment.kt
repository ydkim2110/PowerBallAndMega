package com.reachfree.powerballandmega.ui.slot

import android.os.Bundle
import android.view.*
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.reachfree.powerballandmega.data.local.LottoEntity
import com.reachfree.powerballandmega.databinding.SlotPowerFragmentBinding
import com.reachfree.powerballandmega.ui.base.BaseFragment
import com.reachfree.powerballandmega.ui.bottomsheet.GeneratorResultBottomSheetDialog
import com.reachfree.powerballandmega.ui.generator.GeneratedNumber
import com.reachfree.powerballandmega.ui.generator.NumberModel
import com.reachfree.powerballandmega.utils.*
import com.reachfree.powerballandmega.utils.Constants.ROUND_POWER
import com.reachfree.powerballandmega.utils.Constants.ROUND_POWER_PLAY
import com.reachfree.powerballandmega.utils.Constants.TYPE_POWER
import com.reachfree.powerballandmega.viewmodels.LocalViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class SlotPowerFragment : BaseFragment<SlotPowerFragmentBinding>(), ISlotEventEnd {

    private val localViewModel: LocalViewModel by viewModels()
    private val slotAdapter by lazy { SlotAdapter(SlotAdapter.TYPE_POWER) }

    private lateinit var generatedNumber: GeneratedNumber
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): SlotPowerFragmentBinding {
        return SlotPowerFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSlot()
    }

    private fun setupRecyclerView() {
        binding.rvSlotResult.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = slotAdapter
        }

        slotAdapter.setOnClickListener { isClick ->
            if (isClick) {
                val count = slotAdapter.getCheckBoxList().count { it.isChecked }
                if (count < 1) {
                    binding.btnStart.text = START
                    return@setOnClickListener
                }

                binding.btnStart.text = SAVE
            }
        }
    }

    private fun saveGeneratedNumber(generatedNumberList: ArrayList<GeneratedNumber>) {
        val selectedNumberList = ArrayList<LottoEntity>()

        generatedNumberList.filter { it.isSelected }.map {
            val lottoEntity = LottoEntity().apply {
                type = TYPE_POWER
                category = Constants.CATEGORY_SLOT
                number1 = it.numbers[0]
                number2 = it.numbers[1]
                number3 = it.numbers[2]
                number4 = it.numbers[3]
                number5 = it.numbers[4]
                number6 = it.numbers[5]
            }
            selectedNumberList.add(lottoEntity)
        }

        if (selectedNumberList.isEmpty()) return

        requireContext().toastShort("Saving...")

        runDelayed(500L) {
            localViewModel.insertLottoList(selectedNumberList)
            slotAdapter.clearData()
            index = 0
            binding.btnStart.text = START
        }
    }


    private fun setupSlot() {
        with(binding) {
            image1.setSlotEventEnd(this@SlotPowerFragment)
            image2.setSlotEventEnd(this@SlotPowerFragment)
            image3.setSlotEventEnd(this@SlotPowerFragment)
            image4.setSlotEventEnd(this@SlotPowerFragment)
            image5.setSlotEventEnd(this@SlotPowerFragment)
            image6.setSlotEventEnd(this@SlotPowerFragment)

            btnStart.setOnSingleClickListener {
                if (btnStart.text == SAVE) {
                    saveGeneratedNumber(slotAdapter.getNumberList())

                } else {
                    // TODO: 게임 제한하기
                    isAnimated = true
                    generatedNumber = GeneratedNumber(ArrayList())
                    btnStart.inActivateButton()

                    val ranNumber = ArrayList<Int>()
                    ranNumber.clear()
                    for (i in 1..ROUND_POWER) {
                        ranNumber.add(i)
                    }
                    ranNumber.shuffle()

                    val ranNumberPowerPlay = ArrayList<Int>()
                    ranNumberPowerPlay.clear()
                    for (i in 1..ROUND_POWER_PLAY) {
                        ranNumberPowerPlay.add(i)
                    }
                    ranNumberPowerPlay.shuffle()

                    image1.setValueOrdered(Random.nextInt(6), 5, ranNumber[0], SlotAdapter.TYPE_POWER)
                    image2.setValueOrdered(Random.nextInt(6), 10, ranNumber[1], SlotAdapter.TYPE_POWER)
                    image3.setValueOrdered(Random.nextInt(6), 15, ranNumber[2], SlotAdapter.TYPE_POWER)
                    image4.setValueOrdered(Random.nextInt(6), 20, ranNumber[3], SlotAdapter.TYPE_POWER)
                    image5.setValueOrdered(Random.nextInt(6), 25, ranNumber[4], SlotAdapter.TYPE_POWER)
                    image6.setValueOrdered(Random.nextInt(6), 30, ranNumberPowerPlay[0], SlotAdapter.TYPE_POWER)
                }
            }
        }
    }

    override fun slotEventEnd(result: Int, numRotate: Int, number: Int) {
        generatedNumber.numbers.add(number)
        if (numRotate == 25) {
            generatedNumber.numbers.sort()
        }

        if (numRotate == 30) {
            isAnimated = false
            binding.btnStart.activateButton()
            slotAdapter.setData(generatedNumber, index)
            index++
        }
    }


    companion object {
        private const val SAVE = "Save"
        private const val START = "Start"
        var isAnimated = false
        fun newInstance() = SlotPowerFragment()
    }

}