package com.reachfree.powerballandmega.ui.generator

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.reachfree.powerballandmega.data.local.LottoEntity
import com.reachfree.powerballandmega.databinding.GeneratorMegaFragmentBinding
import com.reachfree.powerballandmega.ui.bottomsheet.GeneratorResultBottomSheetDialog
import com.reachfree.powerballandmega.utils.*
import com.reachfree.powerballandmega.viewmodels.LocalViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GeneratorMegaFragment : Fragment(), NumberSelectListener {

    private var _binding: GeneratorMegaFragmentBinding? = null
    private val binding get() = _binding!!

    private val localViewModel: LocalViewModel by viewModels()

    private lateinit var generatorAdapter: GeneratorAdapter
    private lateinit var generatorPowerPlayAdapter: GeneratorAdapter
    private var numberModels = mutableListOf<NumberModel>()
    private var numberPowerPlayModels = mutableListOf<NumberModel>()

    private var generatedNumberList: ArrayList<GeneratedNumber>? = null
    private var generatedNumber: GeneratedNumber? = null

    private lateinit var analyzingDialog: AlertDialog

    private val generatorResultDialog by lazy {
        GeneratorResultBottomSheetDialog(generatedNumberList!!, GeneratorResultBottomSheetDialog.TYPE_MEGA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        generateBaseNumberList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GeneratorMegaFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (generatedNumberList == null)  {
            generatedNumberList = ArrayList()
        }
        generatedNumberList?.clear()

        setupTextView()
        setupRecyclerView()
        setupButton()
        setupGeneratorResultDialog()
    }

    private fun setupTextView() {
        binding.txtCount.text = "0 selected"
        binding.txtCountPowerPlay.text = "0 selected"
    }
    private fun setupRecyclerView() {
        binding.rvNumber.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity, GRID_SPAN_COUNT)
            generatorAdapter = GeneratorAdapter(numberModels, this@GeneratorMegaFragment)
            adapter = generatorAdapter
        }

        binding.rvNumberPowerPlay.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity, GRID_SPAN_COUNT)
            generatorPowerPlayAdapter = GeneratorAdapter(numberPowerPlayModels, this@GeneratorMegaFragment)
            adapter = generatorPowerPlayAdapter
        }
    }

    private fun setupButton() {
        binding.btnClear.inActivateButton()
        binding.btnGenerate.inActivateButton()

        binding.btnClear.setOnSingleClickListener {
            generatorAdapter.clearAllSelectedNumbers()
            generatorPowerPlayAdapter.clearAllSelectedNumbers()

            binding.txtCount.text = "0 selected(Min 12)"
            binding.txtCountPowerPlay.text = "0 selected(Min 2)"

            binding.btnClear.inActivateButton()
            binding.btnGenerate.inActivateButton()
        }

        binding.btnGenerate.setOnSingleClickListener {
            analyzingDialog = requireActivity().showAnalyzingDialog()
            analyzingDialog.show()
            runDelayed(SAVE_TIME_DELAY) {
                analyzingDialog.dismiss()
                generateMegaBallNumber()
                showResultDialog()
            }
        }
    }

    private fun setupGeneratorResultDialog() {
        generatorResultDialog.setGeneratorResultDialog(object : GeneratorResultBottomSheetDialog.OnGeneratorResultDialog {
            override fun onFinish(type: String, generatedNumberList: ArrayList<GeneratedNumber>?) {
                when (type) {
                    Constants.TYPE_SAVE -> {
                        if (generatedNumberList.isNullOrEmpty()) return

                        saveGeneratedNumber(generatedNumberList)
                    }
                    Constants.TYPE_CANCEL -> {
                        generatorResultDialog.dismiss()
                    }
                }
            }
        })
    }

    private fun saveGeneratedNumber(generatedNumberList: List<GeneratedNumber>) {

        val selectedNumberList = ArrayList<LottoEntity>()

        generatedNumberList.filter { it.isSelected }.map {
            val lottoEntity = LottoEntity().apply {
                type = GeneratorResultBottomSheetDialog.TYPE_MEGA
                category = Constants.CATEGORY_GENERATOR
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
            generatorResultDialog.dismiss()
            clearRecyclerView()
        }
    }

    private fun clearRecyclerView() {
        generatorAdapter.clearAllSelectedNumbers()
        generatorPowerPlayAdapter.clearAllSelectedNumbers()
        binding.layoutScroll.post { binding.layoutScroll.fullScroll(ScrollView.FOCUS_UP) }
    }

    private fun generateBaseNumberList() {
        numberModels.clear()
        for (i in 1..70) {
            val number = NumberModel(number = i)
            numberModels.add(number)
        }

        numberPowerPlayModels.clear()
        for (i in 1..25) {
            val number = NumberModel(number = i)
            numberPowerPlayModels.add(number)
        }
    }

    private fun generateMegaBallNumber() {
        generatedNumberList?.clear()

        val selectedNumbers = generatorAdapter.getSelectedNumbers()
        val selectedPowerPlayNumbers = generatorPowerPlayAdapter.getSelectedNumbers()
        val addedNumbers = ArrayList<Int>()
        val addedPowerPlayNumbers = ArrayList<Int>()

        for (number in selectedNumbers) {
            addedNumbers.add(number.number)
        }

        for (number in selectedPowerPlayNumbers) {
            addedPowerPlayNumbers.add(number.number)
        }

        if (addedNumbers.size <= 5) return
        if (addedPowerPlayNumbers.size <= 1) return

        for (c in 1..5) {
            generatedNumber = GeneratedNumber(ArrayList())

            addedNumbers.shuffle()
            addedPowerPlayNumbers.shuffle()

            for (i in 0..4) {
                generatedNumber!!.numbers.add(i, addedNumbers[i])
            }

            generatedNumber!!.numbers.sort()

            generatedNumber!!.numbers.add(5, addedPowerPlayNumbers[0])

            generatedNumberList?.add((c-1), generatedNumber!!)
        }

        generatedNumberList?.distinct()
    }

    private fun showResultDialog() {
        generatorResultDialog.isCancelable = false
        generatorResultDialog.show(requireActivity().supportFragmentManager, GeneratorResultBottomSheetDialog.TAG)
    }

    override fun onSelected(isSelected: Boolean) {
        val selectedNumbers = generatorAdapter.getSelectedNumbers()
        val selectedPowerPlayNumbers = generatorPowerPlayAdapter.getSelectedNumbers()

        if (selectedNumbers.size >= 12 && selectedPowerPlayNumbers.size >= 2) {
            binding.btnGenerate.activateButton()
        }

        if (isSelected) {
            if (selectedNumbers.size > 0) {
                binding.txtCount.text = "${selectedNumbers.count()} selected(Min 12)"
            }

            if (selectedPowerPlayNumbers.size > 0) {
                binding.txtCountPowerPlay.text = "${selectedPowerPlayNumbers.count()} selected(Min 2)"
            }
            binding.btnClear.activateButton()
        } else {
            if (selectedNumbers.isEmpty()) {
                binding.txtCount.text = "0 selected(Min 12)"
                binding.btnClear.inActivateButton()
            } else {
                binding.txtCount.text = "${selectedNumbers.count()} selected(Min 12)"
                binding.btnClear.activateButton()
            }

            if (selectedPowerPlayNumbers.isEmpty()) {
                binding.txtCountPowerPlay.text = "0 selected(Min 2)"
                binding.btnClear.inActivateButton()
            } else {
                binding.txtCountPowerPlay.text = "${selectedPowerPlayNumbers.count()} selected(Min 2)"
                binding.btnClear.activateButton()
            }
        }
    }

    companion object {
        private const val SAVE_TIME_DELAY = 3000L
        private const val GRID_SPAN_COUNT = 7
        fun newInstance() = GeneratorMegaFragment()
    }


}