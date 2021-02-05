package com.reachfree.powerballandmega.ui.megalist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.powerballandmega.data.remote.response.MegaBallResponse
import com.reachfree.powerballandmega.databinding.MegaListActivityBinding
import com.reachfree.powerballandmega.ui.base.BaseActivity

class MegaListActivity : BaseActivity() {

    override var animationKind = ANIMATION_SLIDE_FROM_BOTTOM

    private var _binding: MegaListActivityBinding? = null
    private val binding get() = _binding!!

    private val megaAdapter: MegaListAdapter by lazy { MegaListAdapter() }
    private var passedData: ArrayList<MegaBallResponse>? = null
    private val data: ArrayList<MegaBallResponse> by lazy { ArrayList() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = MegaListActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        passedData = ArrayList()
        passedData?.clear()
        passedData = intent.getParcelableArrayListExtra(EXTRA_MEGA_BALL_LIST)

        passedData?.subList(0, 20)?.let { data.addAll(it) }

        with(binding) {
            rvMegaBallList.layoutManager = LinearLayoutManager(this@MegaListActivity)
            megaAdapter.submitList(passedData!!)
            rvMegaBallList.adapter = megaAdapter
            rvMegaBallList.addOnScrollListener(scrollListener)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar).apply {
            title = null
        }

        binding.toolbarTitle.text = "Mega Millions"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = binding.rvMegaBallList.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= 20
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                passedData?.subList(0, 40)?.let { data.addAll(it) }
                megaAdapter.submitList(data)
                isScrolling = false
            } else {
                binding.rvMegaBallList.setPadding(0, 0, 0, 0)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun showProgressBar() {
        binding.progressMegaList.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideProgressBar() {
        binding.progressMegaList.visibility = View.INVISIBLE
        isLoading = false
    }

    companion object {
        const val EXTRA_MEGA_BALL_LIST = "extra_mega_ball_list"
        fun start(context: Context, megaList: ArrayList<MegaBallResponse>) {
            val intent = Intent(context, MegaListActivity::class.java).apply {
                putParcelableArrayListExtra(EXTRA_MEGA_BALL_LIST, megaList)
            }
            context.startActivity(intent)
        }
    }
}