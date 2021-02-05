package com.reachfree.powerballandmega.ui.savednumber

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.reachfree.powerballandmega.R
import com.reachfree.powerballandmega.data.local.LottoEntity
import com.reachfree.powerballandmega.databinding.ItemSavedNumberListBinding
import com.reachfree.powerballandmega.utils.Constants
import com.reachfree.powerballandmega.utils.setOnSingleClickListener

class SavedNumberSubAdapter(
    private val savedNumberList: List<LottoEntity>,
    private val clickListener: SavedNumberAdapter.OnItemClickListener
) : RecyclerView.Adapter<SavedNumberSubAdapter.MyViewHolder>() {

    inner class MyViewHolder(
        private val binding: ItemSavedNumberListBinding,
        private val clickListener: SavedNumberAdapter.OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(lottoEntity: LottoEntity) {
            with(binding) {
                txtNumber1.text = lottoEntity.number1.toString()
                txtNumber2.text = lottoEntity.number2.toString()
                txtNumber3.text = lottoEntity.number3.toString()
                txtNumber4.text = lottoEntity.number4.toString()
                txtNumber5.text = lottoEntity.number5.toString()
                txtNumber6.text = lottoEntity.number6.toString()

                if (lottoEntity.type.equals(Constants.TYPE_POWER)) {
                    txtNumber6.setBackgroundResource(R.drawable.bg_power_ball)
                    txtNumber6.setTextColor(ContextCompat.getColor(root.context, R.color.white))
                }
                else if (lottoEntity.type.equals(Constants.TYPE_MEGA)) {
                    txtNumber6.setBackgroundResource(R.drawable.bg_mega_ball)
                }

                btnDelete.setOnSingleClickListener {
                    clickListener.onItemClickListener(lottoEntity)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MyViewHolder(ItemSavedNumberListBinding.inflate(LayoutInflater.from(parent.context),
            parent, false), clickListener)

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(savedNumberList[position])
    }

    override fun getItemCount() = savedNumberList.size


}