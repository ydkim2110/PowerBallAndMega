package com.reachfree.powerballandmega.ui.savednumber.power

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.reachfree.powerballandmega.R
import com.reachfree.powerballandmega.data.local.LottoEntity
import com.reachfree.powerballandmega.databinding.SavedNumberPowerFragmentBinding
import com.reachfree.powerballandmega.ui.common.GameCategory
import com.reachfree.powerballandmega.ui.savednumber.SavedNumberAdapter
import com.reachfree.powerballandmega.utils.Constants
import com.reachfree.powerballandmega.utils.toastShort
import com.reachfree.powerballandmega.viewmodels.LocalViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SavedNumberPowerFragment : Fragment(), SavedNumberAdapter.OnItemClickListener {

    private var _binding: SavedNumberPowerFragmentBinding? = null
    private val binding get() = _binding!!

    private val localViewModel: LocalViewModel by viewModels()
    private lateinit var savedNumberAdapter: SavedNumberAdapter

    private lateinit var gameCategoryList: ArrayList<GameCategory>

    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameCategoryList = ArrayList()

        val titleArray = resources.getStringArray(R.array.game_tile)
        val imageArray = resources.obtainTypedArray(R.array.game_image)

        for (i in titleArray.indices) {
            val gameCategory = GameCategory().apply {
                title = titleArray[i]
                image = imageArray.getResourceId(i, -1)
            }
            gameCategoryList.add(gameCategory)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SavedNumberPowerFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showProgressBar()
        setupRecyclerView()
        setupResponseObserver()

        localViewModel.getLottoByType(Constants.TYPE_POWER)
    }

    private fun showProgressBar() {
        binding.rvPowerResult.visibility = View.GONE
        binding.progressSavedPower.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.rvPowerResult.visibility = View.VISIBLE
        binding.progressSavedPower.visibility = View.GONE
    }

    private fun setupRecyclerView() {
        binding.rvPowerResult.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupResponseObserver() {
        lifecycleScope.launchWhenStarted {
            localViewModel.lottoResponse.collect { event ->
                when (event) {
                    is LocalViewModel.LocalDatabaseEvent.Loading -> {
                        Log.d("DEBUG", "Loading: ")
                    }
                    is LocalViewModel.LocalDatabaseEvent.Success -> {
                        savedNumberAdapter = SavedNumberAdapter(gameCategoryList, event.resultList, this@SavedNumberPowerFragment)
                        binding.rvPowerResult.adapter = savedNumberAdapter
                        hideProgressBar()
                    }
                    is LocalViewModel.LocalDatabaseEvent.Failure -> {
                        Log.d("DEBUG", "Failure: ")
                        hideProgressBar()
                    }
                    else -> {
                        Log.d("DEBUG", "else: ")
                        hideProgressBar()
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance() = SavedNumberPowerFragment()
    }

    override fun onItemClickListener(lottoEntity: LottoEntity) {
        localViewModel.deleteLotto(lottoEntity)
        localViewModel.getLottoByType(Constants.TYPE_POWER)
        requireActivity().toastShort("Deleted...")
    }
}