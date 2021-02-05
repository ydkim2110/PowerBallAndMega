package com.reachfree.powerballandmega.ui.megalist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.powerballandmega.data.remote.response.MegaBallResponse
import com.reachfree.powerballandmega.databinding.ItemMegaBinding
import com.reachfree.powerballandmega.utils.AppUtils

class MegaListAdapter : ListAdapter<MegaBallResponse, MegaListAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(
        private val binding: ItemMegaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(megaBallResponse: MegaBallResponse) {
            with(binding) {
                txtDrawDate.text = AppUtils.convertDrawDate(megaBallResponse.draw_date!!)
                txtMultiplier.text = AppUtils.convertMultiplier(megaBallResponse.multiplier!!, true)

                val arrayOfBall = arrayOf(txtNumber1, txtNumber2, txtNumber3, txtNumber4, txtNumber5)
                val numbers = megaBallResponse.winning_numbers!!.split(" ").toTypedArray()
                for (i in numbers.indices) {
                    arrayOfBall[i].text = numbers[i]
                }
                txtNumber6.text = megaBallResponse.mega_ball
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMegaBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    val differ = AsyncListDiffer(this, DiffCallback())

    private class DiffCallback: DiffUtil.ItemCallback<MegaBallResponse>() {
        override fun areItemsTheSame(
            oldItem: MegaBallResponse,
            newItem: MegaBallResponse
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: MegaBallResponse,
            newItem: MegaBallResponse
        ): Boolean {
            return oldItem == newItem
        }
    }



}