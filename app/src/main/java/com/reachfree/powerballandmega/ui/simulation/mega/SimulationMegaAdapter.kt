package com.reachfree.powerballandmega.ui.simulation.mega

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.powerballandmega.R
import com.reachfree.powerballandmega.databinding.ItemSimulationResultBinding
import com.reachfree.powerballandmega.utils.AppUtils
import java.lang.Exception

class SimulationMegaAdapter: ListAdapter<MegaModel, SimulationMegaAdapter.MyViewHolder>(
    DiffCallback()
) {

    inner class MyViewHolder(
        private val binding: ItemSimulationResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(megaModel: MegaModel) {
            with(binding) {
                val arrayOfBall = arrayOf(
                    txtNumber1, txtNumber2, txtNumber3, txtNumber4, txtNumber5)
                val numbers = megaModel.megaBall.winning_numbers!!.split(" ").toTypedArray()
                for (i in numbers.indices) {
                    arrayOfBall[i].text = numbers[i]
                }

                txtNumber6.text = megaModel.megaBall.mega_ball
                txtNumber6.setBackgroundResource(R.drawable.bg_mega_ball)

                txtMultiplier.text = AppUtils.convertMultiplier(megaModel.megaBall.multiplier!!, false)
                txtDrawDate.text = "Winning Numbers: ${AppUtils.convertDrawDate(megaModel.megaBall.draw_date!!)}"
                if (megaModel.isMegaPlay) {
                    try {
                        txtResult.text = "Your Result: ${megaModel.rank.description} ${megaModel.megaBall.multiplier.toInt()}X"
                    } catch (e: Exception) {
                        txtResult.text = "Your Result: ${megaModel.rank.description}"
                    }
                } else {
                    txtResult.text = "Your Result: ${megaModel.rank.description}"
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

    private class DiffCallback() : DiffUtil.ItemCallback<MegaModel>() {
        override fun areItemsTheSame(
            oldItem: MegaModel,
            newItem: MegaModel
        ): Boolean {
            return  oldItem.megaBall.draw_date == newItem.megaBall.draw_date
        }

        override fun areContentsTheSame(
            oldItem: MegaModel,
            newItem: MegaModel
        ): Boolean {
            return  oldItem.megaBall == newItem.megaBall
        }

    }

}