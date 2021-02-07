package com.reachfree.powerballandmega.ui.scratch

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.reachfree.powerballandmega.data.local.LottoEntity
import com.reachfree.powerballandmega.databinding.ScratchPowerFragmentBinding
import com.reachfree.powerballandmega.ui.base.BaseFragment
import com.reachfree.powerballandmega.ui.bottomsheet.GeneratorResultBottomSheetDialog
import com.reachfree.powerballandmega.ui.generator.GeneratedNumber
import com.reachfree.powerballandmega.utils.*
import com.reachfree.powerballandmega.utils.Constants.CATEGORY_SCRATCH
import com.reachfree.powerballandmega.utils.Constants.TYPE_POWER
import com.reachfree.powerballandmega.viewmodels.LocalViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScratchPowerFragment : BaseFragment<ScratchPowerFragmentBinding>() {

    private val localViewModel: LocalViewModel by viewModels()
    private lateinit var scratchAdapter: ScratchAdapter

    private var width: Int = 0
    private var height: Int = 0

    private var generatedNumberListAdapter: ArrayList<ArrayList<Int>>? = null
    private var generatedNumberList: ArrayList<GeneratedNumber>? = null
    private var generatedNumber: GeneratedNumber? = null

    private val scratchResultDialog by lazy {
        GeneratorResultBottomSheetDialog(generatedNumberList!!, TYPE_POWER)
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): ScratchPowerFragmentBinding {
        return ScratchPowerFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (generatedNumberList == null)  {
            generatedNumberList = ArrayList()
        }
        generatedNumberList?.clear()

        setupView()
        setupRecyclerView()
        setupGeneratorResultDialog()
    }

    private fun setupView() {
        binding.scratchCard.setOnScratchListener(object : ScratchCard.OnScratchListener {
            override fun onScratch(scratchCard: ScratchCard?, visiblePercent: Float) {
                if (visiblePercent >= 0.6) {
                    binding.scratchCard.visibility = View.GONE
                    runDelayed(BOTTOM_TIME_DELAY) {
                        showBottomDialog()
                    }
                }
            }
        })

        binding.rvScratch.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                width = binding.rvScratch.measuredWidth
                height = binding.rvScratch.measuredHeight

                val params = binding.scratchCard.layoutParams
                params.width = width
                params.height = height
                binding.scratchCard.layoutParams = params

                binding.rvScratch.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun showBottomDialog() {
        scratchResultDialog.isCancelable = false
        scratchResultDialog.show(requireActivity().supportFragmentManager, GeneratorResultBottomSheetDialog.TAG)
    }

    private fun setupRecyclerView() {
        binding.rvScratch.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())

            generatedNumberList?.clear()

            if (generatedNumberListAdapter == null) {
                generatedNumberListAdapter = ArrayList()
            }
            generatedNumberListAdapter?.clear()


            for (c in 1..3) {
                generatedNumber = GeneratedNumber(ArrayList())
                val row = arrayListOf<Int>()

                val fiveNumber = AppUtils.generatePowerFiveNumber()
                row.addAll(fiveNumber)
                val powerPlayNumber = AppUtils.generatePowerPlayNumber()
                row.addAll(powerPlayNumber)

                generatedNumber!!.numbers.addAll(row)

                generatedNumberListAdapter?.add(row)
                generatedNumberList?.add((c-1), generatedNumber!!)
            }

            scratchAdapter = ScratchAdapter(generatedNumberListAdapter!!, ScratchAdapter.TYPE_POWER)
            adapter = scratchAdapter
        }
    }

    private fun setupGeneratorResultDialog() {
        scratchResultDialog.setGeneratorResultDialog(object : GeneratorResultBottomSheetDialog.OnGeneratorResultDialog {
            override fun onFinish(type: String, generatedNumberList: ArrayList<GeneratedNumber>?) {
                when (type) {
                    Constants.TYPE_SAVE -> {
                        if (generatedNumberList.isNullOrEmpty()) return

                        saveGeneratedNumber(generatedNumberList)
                    }
                    Constants.TYPE_CANCEL -> {
                        scratchResultDialog.dismiss()
                    }
                }
            }
        })
    }

    private fun saveGeneratedNumber(generatedNumberList: List<GeneratedNumber>) {

        val selectedNumberList = ArrayList<LottoEntity>()

        generatedNumberList.filter { it.isSelected }.map {
            val lottoEntity = LottoEntity().apply {
                type = TYPE_POWER
                category = CATEGORY_SCRATCH
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
            scratchResultDialog.dismiss()
        }
    }


    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        super.onMultiWindowModeChanged(isInMultiWindowMode)
        val height = binding.rvScratch.height
        binding.scratchCard.layoutParams.height = height
    }

    companion object {
        private const val BOTTOM_TIME_DELAY = 500L
        fun newInstance() = ScratchPowerFragment()
    }

}