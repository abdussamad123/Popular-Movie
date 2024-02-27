package com.popularmovie.appcomponent.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.popularmovie.R
import com.popularmovie.databinding.BottomSheetSortFilterLayoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SortFilterBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetSortFilterLayoutBinding

    companion object{
        val TAG : String = SortFilterBottomSheet::class.java.simpleName
        private var title = ""
        private var listItems =  ArrayList<SortAndFilterItem>()
        private var openingForFilter = false
        private var callbackListener : SortFilterBottomSheetCallbackListener?= null

        fun getInstance(title:String, listItems :  ArrayList<SortAndFilterItem>, openingForFilter : Boolean,
                        callbackListener : SortFilterBottomSheetCallbackListener )
        = SortFilterBottomSheet().also {
            this.title = title
            this.listItems = listItems
            this.openingForFilter = openingForFilter
            this.callbackListener = callbackListener
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetSortFilterLayoutBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (openingForFilter){
            binding.tvTitle.text = getString(R.string.filter_by)
        }else binding.tvTitle.text = getString(R.string.sort_by)

        setUpItemsRV()
    }

    private fun setUpItemsRV(){
      val sortFilterAdaptor = SortFilterAdaptor(listItems, object  : SortFilterAdaptor.OnItemClickListener{
          override fun onItemClick(itemData: SortAndFilterItem) {
              callbackListener?.onClickItem(itemData)
              dismiss()
          }
      })
        binding.rvItem.adapter = sortFilterAdaptor
    }

   data class SortAndFilterItem(val itemName:String, val itemId:Int)
}

interface SortFilterBottomSheetCallbackListener {
   fun onClickItem(sortAndFilterItem: SortFilterBottomSheet.SortAndFilterItem)
}
