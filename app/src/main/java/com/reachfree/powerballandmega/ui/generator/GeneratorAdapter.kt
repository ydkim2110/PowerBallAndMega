package com.reachfree.powerballandmega.ui.generator

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.powerballandmega.R
import com.reachfree.powerballandmega.databinding.ItemNumberBinding

class GeneratorAdapter(
    private val numberModels: List<NumberModel>,
    private val numberSelectListener: NumberSelectListener
) : RecyclerView.Adapter<GeneratorAdapter.MyViewHolder>() {

    inner class MyViewHolder(
        private val binding: ItemNumberBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(numberModel: NumberModel) {
            with(binding) {
                layoutNumber.setBackgroundResource(R.drawable.bg_btn_outline_gray)
                txtNumber.setBackgroundResource(R.drawable.bg_ball)
                txtNumber.setTextColor(ContextCompat.getColor(txtNumber.context, R.color.colorDarker))
                txtNumber.text = numberModel.number.toString()

                layoutNumber.setOnClickListener {
                    if (numberModel.isSelected) {
                        layoutNumber.setBackgroundResource(R.drawable.bg_btn_outline_gray)
                        txtNumber.setBackgroundResource(R.drawable.bg_ball)
                        txtNumber.setTextColor(ContextCompat.getColor(txtNumber.context, R.color.colorDarker))
                        numberModel.isSelected = false
                        numberSelectListener.onSelected(false)
                    } else {
                        layoutNumber.setBackgroundResource(R.drawable.bg_btn_outline_orange)
                        txtNumber.setTextColor(ContextCompat.getColor(txtNumber.context, R.color.colorDarker))
                        numberModel.isSelected = true
                        numberSelectListener.onSelected(true)
                    }
                }
            }
        }

    }

    fun clearAllSelectedNumbers() {
        numberModels.forEach { number -> number.isSelected = false }
        notifyDataSetChanged()
    }

    fun getSelectedNumbers(): List<NumberModel> {
        return numberModels.filter { it.isSelected }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MyViewHolder(ItemNumberBinding.inflate(LayoutInflater.from(parent.context),
            parent, false))


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(numberModels[position])
    }

    override fun getItemCount() = numberModels.size

}