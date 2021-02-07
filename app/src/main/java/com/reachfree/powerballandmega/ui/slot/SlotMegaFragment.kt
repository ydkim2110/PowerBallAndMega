package com.reachfree.powerballandmega.ui.slot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.reachfree.powerballandmega.data.local.LottoEntity
import com.reachfree.powerballandmega.databinding.SlotMegaFragmentBinding
import com.reachfree.powerballandmega.ui.base.BaseFragment
import com.reachfree.powerballandmega.ui.bottomsheet.GeneratorResultBottomSheetDialog
import com.reachfree.powerballandmega.ui.generator.GeneratedNumber
import com.reachfree.powerballandmega.utils.*
import com.reachfree.powerballandmega.utils.Constants.TYPE_MEGA
import com.reachfree.powerballandmega.viewmodels.LocalViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class SlotMegaFragment : BaseFragment<SlotMegaFragmentBinding>(), ISlotEventEnd {

    private val localViewModel: LocalViewModel by viewModels()
    private val slotAdapter by lazy { SlotAdapter(SlotAdapter.TYPE_MEGA) }

    private lateinit var generatedNumber: GeneratedNumber
    private var index = 0

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): SlotMegaFragmentBinding {
        return SlotMegaFragmentBinding.inflate(inflater, container, false)
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
                type = TYPE_MEGA
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
            image1.setSlotEventEnd(this@SlotMegaFragment)
            image2.setSlotEventEnd(this@SlotMegaFragment)
            image3.setSlotEventEnd(this@SlotMegaFragment)
            image4.setSlotEventEnd(this@SlotMegaFragment)
            image5.setSlotEventEnd(this@SlotMegaFragment)
            image6.setSlotEventEnd(this@SlotMegaFragment)

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
                    for (i in 1..70) {
                        ranNumber.add(i)
                    }
                    ranNumber.shuffle()

                    val ranNumberPowerPlay = ArrayList<Int>()
                    ranNumberPowerPlay.clear()
                    for (i in 1..25) {
                        ranNumberPowerPlay.add(i)
                    }
                    ranNumberPowerPlay.shuffle()

                    image1.setValueOrdered(Random.nextInt(6), 5, ranNumber[0], SlotAdapter.TYPE_MEGA)
                    image2.setValueOrdered(Random.nextInt(6), 10, ranNumber[1], SlotAdapter.TYPE_MEGA)
                    image3.setValueOrdered(Random.nextInt(6), 15, ranNumber[2], SlotAdapter.TYPE_MEGA)
                    image4.setValueOrdered(Random.nextInt(6), 20, ranNumber[3], SlotAdapter.TYPE_MEGA)
                    image5.setValueOrdered(Random.nextInt(6), 25, ranNumber[4], SlotAdapter.TYPE_MEGA)
                    image6.setValueOrdered(Random.nextInt(6), 30, ranNumberPowerPlay[0], SlotAdapter.TYPE_MEGA)
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
        fun newInstance() = SlotMegaFragment()
    }

}