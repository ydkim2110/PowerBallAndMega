package com.reachfree.powerballandmega.ui.scratch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.powerballandmega.R
import com.reachfree.powerballandmega.databinding.ItemScratchNumberBinding

class ScratchAdapter(
    private val generatedNumberList: ArrayList<ArrayList<Int>>,
    private val type: String
) : RecyclerView.Adapter<ScratchAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemScratchNumberBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = generatedNumberList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(generatedNumberList[position])
    }

    inner class MyViewHolder(
        private val binding: ItemScratchNumberBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(numberList: ArrayList<Int>) {
            with(binding) {
                val arrayBall = arrayOf(txtNumber1, txtNumber2, txtNumber3, txtNumber4, txtNumber5, txtNumber6)
                for (i in arrayBall.indices) {
                    arrayBall[i].text = numberList[i].toString()
                }

                if (type == TYPE_POWER) {
                    txtNumber6.setBackgroundResource(R.drawable.bg_power_ball)
                    txtNumber6.setTextColor(ContextCompat.getColor(root.context, R.color.white))
                }
                else if (type == TYPE_MEGA) {
                    txtNumber6.setBackgroundResource(R.drawable.bg_mega_ball)
                    txtNumber6.setTextColor(ContextCompat.getColor(root.context, R.color.black))
                }
            }
        }
    }

    companion object {
        const val TYPE_POWER = "power"
        const val TYPE_MEGA = "mega"
    }
}