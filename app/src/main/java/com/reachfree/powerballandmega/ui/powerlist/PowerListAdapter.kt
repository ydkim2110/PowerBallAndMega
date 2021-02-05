package com.reachfree.powerballandmega.ui.powerlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.powerballandmega.R
import com.reachfree.powerballandmega.data.remote.response.PowerBallResponse
import com.reachfree.powerballandmega.databinding.ItemPowerBinding
import com.reachfree.powerballandmega.utils.AppUtils

class PowerListAdapter : ListAdapter<PowerBallResponse, PowerListAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(
        private val binding: ItemPowerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(powerBallResponse: PowerBallResponse) {
            with(binding) {
                txtDrawDate.text = AppUtils.convertDrawDate(powerBallResponse.draw_date!!)
                //TODO: 예외처리
                txtMultiplier.text = AppUtils.convertMultiplier(powerBallResponse.multiplier!!, false)

                val arrayOfBall = arrayOf(txtNumber1, txtNumber2, txtNumber3, txtNumber4, txtNumber5, txtNumber6)
                val numbers = powerBallResponse.winning_numbers!!.split(" ").toTypedArray()
                for (i in numbers.indices) {
                    arrayOfBall[i].text = numbers[i]
                }

                txtNumber6.setTextColor(ContextCompat.getColor(root.context, R.color.white))
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemPowerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffCallback: DiffUtil.ItemCallback<PowerBallResponse>() {
        override fun areItemsTheSame(
            oldItem: PowerBallResponse,
            newItem: PowerBallResponse
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: PowerBallResponse,
            newItem: PowerBallResponse
        ): Boolean {
            return oldItem == newItem
        }
    }



}