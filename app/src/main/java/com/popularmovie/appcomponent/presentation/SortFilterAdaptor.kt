package com.popularmovie.appcomponent.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.popularmovie.databinding.ItemSortFilterBinding

class SortFilterAdaptor(private val listItems:ArrayList<SortFilterBottomSheet.SortAndFilterItem>, private val onItemClickListener : OnItemClickListener)
    : RecyclerView.Adapter<SortFilterAdaptor.SortFilterAdaptorVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SortFilterAdaptorVH {

        return SortFilterAdaptorVH(ItemSortFilterBinding.inflate(LayoutInflater.from(parent.context), parent,false))
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    override fun onBindViewHolder(holder: SortFilterAdaptorVH, position: Int) {
        holder.bindItem(listItems[position])
    }

  inner class SortFilterAdaptorVH(private val listItemView: ItemSortFilterBinding, ) : RecyclerView.ViewHolder(listItemView.root) {

        fun bindItem(itemData: SortFilterBottomSheet.SortAndFilterItem) {

            listItemView.tvItem.text = itemData.itemName
            listItemView.container.setOnClickListener {
                onItemClickListener.onItemClick(itemData)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(itemData: SortFilterBottomSheet.SortAndFilterItem)
    }

}
