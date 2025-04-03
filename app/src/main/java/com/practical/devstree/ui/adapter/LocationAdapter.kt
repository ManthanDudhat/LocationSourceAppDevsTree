package com.practical.devstree.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practical.devstree.db.entity.LocationInfo
import com.practical.devstree.databinding.RowLocationBinding
import com.practical.devstree.utils.formatToOneDecimalPlaces
import com.practical.devstree.utils.gone
import com.practical.devstree.utils.visible

class LocationAdapter(
    var onEventListener: (view: View, adapterPos: Int) -> Unit,
) : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    var mList = ArrayList<LocationInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RowLocationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    inner class ViewHolder(private var binding: RowLocationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LocationInfo) = with(binding) {
            tvCity.text = "${item.primaryAddress}"
            tvAddresses.text = "${item.city}"
            tvDistance.text = formatToOneDecimalPlaces(item.distance ?: "").let {
                if (it == "0.00") "" else "Distance : $it KM"
            }

            if (item.isPrimary == true) {
                tvPrimary.visible()
            } else {
                tvPrimary.gone()
            }

            ivDelete.setOnClickListener {
                onEventListener.invoke(it, adapterPosition)
            }
            ivEdit.setOnClickListener {
                onEventListener.invoke(it, adapterPosition)
            }
        }
    }
}