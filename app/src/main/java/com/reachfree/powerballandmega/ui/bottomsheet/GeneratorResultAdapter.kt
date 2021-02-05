package com.reachfree.powerballandmega.ui.bottomsheet

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.powerballandmega.R
import com.reachfree.powerballandmega.databinding.ItemGeneratorResultBinding
import com.reachfree.powerballandmega.ui.bottomsheet.GeneratorResultBottomSheetDialog.Companion.TYPE_MEGA
import com.reachfree.powerballandmega.ui.bottomsheet.GeneratorResultBottomSheetDialog.Companion.TYPE_POWER
import com.reachfree.powerballandmega.ui.generator.GeneratedNumber
import com.reachfree.powerballandmega.utils.toastShort

class GeneratorResultAdapter(
    private val generatedNumberList: ArrayList<GeneratedNumber>,
    private val type: String
) : RecyclerView.Adapter<GeneratorResultAdapter.MyViewHolder>() {

    private val checkBoxList = arrayListOf<CheckBox>()

    inner class MyViewHolder(
        private val binding: ItemGeneratorResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(numbers: ArrayList<Int>, isSelected: Boolean) {
            with(binding) {
                val arrayOfBall = arrayOf(txtNumber1, txtNumber2, txtNumber3, txtNumber4, txtNumber5, txtNumber6)
                for (i in numbers.indices) {
                    arrayOfBall[i].text = numbers[i].toString()
                }

                if (type == TYPE_POWER) {
                    txtNumber6.setBackgroundResource(R.drawable.bg_power_ball)
                    txtNumber6.setTextColor(ContextCompat.getColor(root.context, R.color.white))
                }
                else if (type == TYPE_MEGA) {
                    txtNumber6.setBackgroundResource(R.drawable.bg_mega_ball)
                    txtNumber6.setTextColor(ContextCompat.getColor(root.context, R.color.black))
                }

                checkBoxList.add(chkSelect)

                chkSelect.isChecked = isSelected
                chkSelect.tag = generatedNumberList[adapterPosition]
                chkSelect.setOnClickListener { view ->
                    try {
                        val cb = view as CheckBox
                        val list = view.getTag() as GeneratedNumber
                        list.isSelected = cb.isChecked
                        generatedNumberList[adapterPosition].isSelected = cb.isChecked
                    } catch (e: Exception) {
                        root.context.toastShort("Unknown error occurred.")
                    }
                }
            }
        }

    }

    fun getCheckBoxList() = checkBoxList

    fun getNumberList() = generatedNumberList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MyViewHolder(ItemGeneratorResultBinding.inflate(LayoutInflater.from(parent.context),
            parent, false))

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val numberList = generatedNumberList[position].numbers
        val isSelected = generatedNumberList[position].isSelected
        holder.bind(numberList, isSelected)
    }

    override fun getItemCount() = generatedNumberList.size

}