package com.practical.devstree.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.practical.devstree.databinding.RowSearchAddressBinding

class SearchAddressAdapter(
    var itemClickCallback: (placeId: String?, primaryAddress: String?, mainAddress: String?) -> Unit
) : RecyclerView.Adapter<SearchAddressAdapter.ViewHolder>() {

     var setPredictionsList: List<AutocompletePrediction> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RowSearchAddressBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false)
        )
    }

    override fun getItemCount(): Int {
        return setPredictionsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(setPredictionsList[position])
    }

    fun setPredictions(predictions: List<AutocompletePrediction>) {
        this.setPredictionsList = predictions
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: RowSearchAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AutocompletePrediction) = with(binding) {
            tvAddressType.text = item.getPrimaryText(null).toString()
            tvAddress.text = item.getFullText(null).toString()

            root.setOnClickListener {
                itemClickCallback.invoke(
                    item.placeId,
                    item.getPrimaryText(null).toString(),
                    item.getFullText(null).toString()
                                        )
            }
        }
    }
}