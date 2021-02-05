package com.reachfree.powerballandmega.ui.savednumber

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.powerballandmega.R
import com.reachfree.powerballandmega.data.local.LottoEntity
import com.reachfree.powerballandmega.databinding.ItemSavedNumberBinding
import com.reachfree.powerballandmega.ui.common.GameCategory
import com.reachfree.powerballandmega.utils.Constants

class SavedNumberAdapter(
    private val gameCategoryList: ArrayList<GameCategory>,
    private val savedNumberList: List<LottoEntity>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<SavedNumberAdapter.MyViewHolder>() {


    interface OnItemClickListener {
        fun onItemClickListener(lottoEntity: LottoEntity)
    }

    inner class MyViewHolder(
        private val binding: ItemSavedNumberBinding,
        private var clickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(gameCategory: GameCategory, savedNumberList: List<LottoEntity>) {
            with(binding) {
                txtGameTitle.text = gameCategory.title!!
                imgGame.setImageResource(gameCategory.image!!)

                val filteredGameList = filteredGameCategoryList(gameCategory.title!!, savedNumberList)

                if (filteredGameList.isNullOrEmpty()) {
                    txtEmptyMessage.visibility = View.VISIBLE
                } else {
                    txtEmptyMessage.visibility = View.GONE
                }

                rvSubResult.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(this.context)
                    adapter = SavedNumberSubAdapter(filteredGameList, clickListener)
                }
            }

        }

        private fun filteredGameCategoryList(
            gameTitle: String,
            savedNumberList: List<LottoEntity>
        ): List<LottoEntity> {
            val result = ArrayList<LottoEntity>()
            when (gameTitle) {
                Constants.GAME_GENERATOR -> {
                    val list = savedNumberList.filter { it.category == Constants.CATEGORY_GENERATOR }
                    result.addAll(list)
                }
                Constants.GAME_ROULETTE -> {
                    val list = savedNumberList.filter { it.category == Constants.CATEGORY_ROULETTE }
                    result.addAll(list)
                }
                Constants.GAME_SCRATCH -> {
                    val list = savedNumberList.filter { it.category == Constants.CATEGORY_SCRATCH }
                    result.addAll(list)
                }
                Constants.GAME_SLOT -> {
                    val list = savedNumberList.filter { it.category == Constants.CATEGORY_SLOT }
                    result.addAll(list)
                }
                else -> {}
            }
            return result
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MyViewHolder(ItemSavedNumberBinding.inflate(LayoutInflater.from(parent.context),
            parent, false), listener)

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(gameCategoryList[position], savedNumberList)
    }

    override fun getItemCount() = gameCategoryList.size

}