package com.reachfree.powerballandmega.ui.slot

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.powerballandmega.R
import com.reachfree.powerballandmega.databinding.ItemGeneratorResultBinding
import com.reachfree.powerballandmega.ui.generator.GeneratedNumber
import com.reachfree.powerballandmega.utils.toastShort

class SlotAdapter(
    private val type: String
) : RecyclerView.Adapter<SlotAdapter.MyViewHolder>() {

    private var generatedNumberList: ArrayList<GeneratedNumber> = ArrayList()
    private val checkBoxList = arrayListOf<CheckBox>()

    inner class MyViewHolder(
        private val binding: ItemGeneratorResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(slotGame: List<Int>, isSelected: Boolean) {

            with(binding) {
                txtNumber1.text = slotGame[0].toString()
                txtNumber2.text = slotGame[1].toString()
                txtNumber3.text = slotGame[2].toString()
                txtNumber4.text = slotGame[3].toString()
                txtNumber5.text = slotGame[4].toString()
                txtNumber6.text = slotGame[5].toString()

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
                chkSelect.setOnClickListener { v ->
                    try {
                        val cb = v as CheckBox
                        val list = v.getTag() as GeneratedNumber
                        list.isSelected = cb.isChecked
                        generatedNumberList[adapterPosition].isSelected = cb.isChecked
                        onItemClickListener?.let { it(true) }
                    } catch (e: Exception) {
                        root.context.toastShort("Unknown error occurred.")
                    }

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MyViewHolder(ItemGeneratorResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false))

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        generatedNumberList.let { data ->
            val numberList = data[position].numbers
            val isSelected = data[position].isSelected

            holder.bind(numberList, isSelected)
        }
    }

    override fun getItemCount() = generatedNumberList.size

    fun clearData() {
        generatedNumberList.clear()
        notifyDataSetChanged()
    }

    fun setData(generatedNumber: GeneratedNumber, index: Int) {
        generatedNumberList.add(index, generatedNumber)
        notifyItemInserted(generatedNumberList.size)
    }

    fun getCheckBoxList() = checkBoxList

    fun getNumberList() = generatedNumberList

    private var onItemClickListener: ((Boolean) -> Unit)? = null

    fun setOnClickListener(listener: (Boolean) -> Unit) {
        onItemClickListener = listener
    }

    companion object {
        const val TYPE_POWER = "power"
        const val TYPE_MEGA = "mega"
    }
}