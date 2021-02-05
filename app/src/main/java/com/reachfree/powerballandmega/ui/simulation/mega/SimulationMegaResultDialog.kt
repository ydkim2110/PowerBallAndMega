package com.reachfree.powerballandmega.ui.simulation.mega

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.reachfree.powerballandmega.databinding.DialogSimulationResultMegaBinding
import com.reachfree.powerballandmega.utils.RANK

class SimulationMegaResultDialog(
    private val resultList: List<MegaModel>
) : DialogFragment() {

    private var _binding: DialogSimulationResultMegaBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSimulationResultMegaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTextView()
    }

    private fun setupTextView() {
        binding.txtResult.text = "Your Result: ${resultList.count()}"

        binding.txtPowerJackpot.text = resultList.filter { it.rank == RANK.RANK_MEGA_FIRST }.count().toString()
        binding.txtPowerBall5.text = resultList.filter { it.rank == RANK.RANK_MEGA_SECOND }.count().toString()
        binding.txtPowerBall4Hit.text = resultList.filter { it.rank == RANK.RANK_MEGA_THIRD }.count().toString()
        binding.txtPowerBall4.text = resultList.filter { it.rank == RANK.RANK_MEGA_FOURTH }.count().toString()
        binding.txtPowerBall3Hit.text = resultList.filter { it.rank == RANK.RANK_MEGA_FIFTH }.count().toString()
        binding.txtPowerBall3.text = resultList.filter { it.rank == RANK.RANK_MEGA_SIXTH }.count().toString()
        binding.txtPowerBall2Hit.text = resultList.filter { it.rank == RANK.RANK_MEGA_SEVENTH }.count().toString()
        binding.txtPowerBall1Hit.text = resultList.filter { it.rank == RANK.RANK_MEGA_EIGHTH }.count().toString()
        binding.txtPowerHit.text = resultList.filter { it.rank == RANK.RANK_MEGA_NINTH }.count().toString()
    }

    companion object {
        const val DIALOG_TAG = "SimulationMegaResultDialog"
    }
}