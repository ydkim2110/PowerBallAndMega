package com.reachfree.powerballandmega.ui.slot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.reachfree.powerballandmega.databinding.SlotPowerFragmentBinding
import com.reachfree.powerballandmega.ui.generator.GeneratedNumber
import com.reachfree.powerballandmega.ui.generator.NumberModel
import com.reachfree.powerballandmega.utils.Constants
import com.reachfree.powerballandmega.utils.Constants.ROUND_POWER
import com.reachfree.powerballandmega.utils.Constants.ROUND_POWER_PLAY
import com.reachfree.powerballandmega.utils.activateButton
import com.reachfree.powerballandmega.utils.inActivateButton
import com.reachfree.powerballandmega.utils.setOnSingleClickListener
import kotlin.random.Random

class SlotPowerFragment : Fragment(), ISlotEventEnd {

    private var _binding: SlotPowerFragmentBinding? = null
    private val binding get() = _binding!!

    private val slotAdapter by lazy { SlotAdapter(SlotAdapter.TYPE_POWER) }

    private lateinit var generatedNumber: GeneratedNumber
//    private var isAnimated = false
    private var index = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SlotPowerFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        var isAnimated = false
        fun newInstance() = SlotPowerFragment()
    }


}