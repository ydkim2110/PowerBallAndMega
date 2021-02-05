package com.reachfree.powerballandmega.ui.simulation.power

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.powerballandmega.R
import com.reachfree.powerballandmega.databinding.ItemSimulationResultBinding
import com.reachfree.powerballandmega.utils.AppUtils
import java.lang.Exception

class SimulationPowerAdapter: ListAdapter<PowerModel, SimulationPowerAdapter.MyViewHolder>(
    DiffCallback()
) {

    inner class MyViewHolder(
        private val binding: ItemSimulationResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(powerModel: PowerModel) {
            with(binding) {
                val arrayOfBall = arrayOf(
                    txtNumber1, txtNumber2, txtNumber3, txtNumber4, txtNumber5, txtNumber6)
                val numbers = powerModel.powerBall.winning_numbers!!.split(" ").toTypedArray()
                for (i in numbers.indices) {
                    arrayOfBall[i].text = numbers[i]
                }

                txtNumber6.setBackgroundResource(R.drawable.bg_power_ball)
                txtNumber6.setTextColor(ContextCompat.getColor(root.context, R.color.white))

                txtMultiplier.text = AppUtils.convertMultiplier(powerModel.powerBall.multiplier!!, false)
                txtDrawDate.text = "Winning Numbers: ${AppUtils.convertDrawDate(powerModel.powerBall.draw_date!!)}"

                if (powerModel.isPowerPlay) {
                    try {
                        txtResult.text = "Your Result: ${powerModel.rank.description} ${powerModel.powerBall.multiplier.toInt()}X"
                    } catch (e: Exception) {
                        txtResult.text = "Your Result: ${powerModel.rank.description}"
                    }
                } else {
                    txtResult.text = "Your Result: ${powerModel.rank.description}"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MyViewHolder(ItemSimulationResultBinding.inflate(LayoutInflater.from(parent.context),
            parent, false))

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffCallback() : DiffUtil.ItemCallback<PowerModel>() {
        override fun areItemsTheSame(
            oldItem: PowerModel,
            newItem: PowerModel
        ): Boolean {
            return  oldItem.powerBall.draw_date == newItem.powerBall.draw_date
        }

        override fun areContentsTheSame(
            oldItem: PowerModel,
            newItem: PowerModel
        ): Boolean {
            return  oldItem.powerBall == newItem.powerBall
        }

    }

}