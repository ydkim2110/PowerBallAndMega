package com.reachfree.powerballandmega.ui.bottomsheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.reachfree.powerballandmega.R
import com.reachfree.powerballandmega.databinding.GeneratorResultBottomSheetDialogBinding
import com.reachfree.powerballandmega.ui.generator.GeneratedNumber
import com.reachfree.powerballandmega.utils.Constants.TYPE_CANCEL
import com.reachfree.powerballandmega.utils.Constants.TYPE_SAVE
import com.reachfree.powerballandmega.utils.setOnSingleClickListener
import com.reachfree.powerballandmega.viewmodels.LocalViewModel

class GeneratorResultBottomSheetDialog(
    private val generatedNumberList: ArrayList<GeneratedNumber>,
    private val type: String
) : BottomSheetDialogFragment() {

    private var _binding: GeneratorResultBottomSheetDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var generatorResultAdapter: GeneratorResultAdapter

    private lateinit var onGeneratorResultDialog: OnGeneratorResultDialog
    interface OnGeneratorResultDialog {
        fun onFinish(type: String, generatedNumberList: ArrayList<GeneratedNumber>? = null)
    }

    fun setGeneratorResultDialog(onGeneratorResultDialog: OnGeneratorResultDialog) {
        this.onGeneratorResultDialog = onGeneratorResultDialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.GeneratorResultBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GeneratorResultBottomSheetDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupRecyclerView()
        setupButton()
    }

    @SuppressLint("ResourceType")
    private fun setupView() {
        if (type == TYPE_POWER) {
            binding.cardview.background.setTint(ContextCompat.getColor(requireContext(), R.color.colorPowerBackground))
            binding.imgLogo.setImageResource(R.drawable.logo_powerball)
        }
        else if (type == TYPE_MEGA) {
            binding.cardview.background.setTint(ContextCompat.getColor(requireContext(), R.color.colorMegaBackground))
            binding.imgLogo.setImageResource(R.drawable.logo_megamillions)
        }
    }

    private fun setupRecyclerView() {
        binding.rvResult.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            generatorResultAdapter = GeneratorResultAdapter(generatedNumberList, type)
            adapter = generatorResultAdapter
        }
    }

    private fun setupButton() {
        binding.btnSave.setOnSingleClickListener {
            val count = generatorResultAdapter.getCheckBoxList().count { it.isChecked }
            if (count < 1) {
                return@setOnSingleClickListener
            }

            onGeneratorResultDialog.onFinish(TYPE_SAVE, generatorResultAdapter.getNumberList())
        }

        binding.btnCancel.setOnSingleClickListener {
            onGeneratorResultDialog.onFinish(TYPE_CANCEL, null)
        }
    }

    companion object {
        const val TAG = "GeneratorResultBottomSheetDialog"
        const val TYPE_POWER = "power"
        const val TYPE_MEGA = "mega"
    }
}