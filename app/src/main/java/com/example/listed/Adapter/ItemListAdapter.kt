package com.example.ocoor.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.example.ocoor.Fragments.AddItemFragment
import com.example.ocoor.MainActivity
import com.example.ocoor.R
import com.example.ocoor.Utils.Item
import com.example.ocoor.Utils.ItemList
import com.example.ocoor.Utils.ItemListViewModel
import com.example.ocoor.Utils.ItemViewModel
import com.example.ocoor.databinding.ItemLayoutBinding
import com.example.ocoor.databinding.ListLayoutBinding
import java.text.DecimalFormat
import java.util.*


class ItemListAdapter(
    var itemListOfLists: List<ItemList>,
    val mItemListViewModel: ItemListViewModel,
    val mainActivity: MainActivity
) :
    RecyclerView.Adapter<ItemListAdapter.ItemListViewHolder>() {

    // variables
    var mRecyclerView: RecyclerView? = null
    var selectedList: com.google.android.material.card.MaterialCardView? = null

    inner class ItemListViewHolder(val binding: ListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //fun bindView(item: Item) {
        //    binding.apply {
        //        itemTextView.text = item.itemText
        //    }
        //}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
        val binding = ListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ItemListViewHolder(binding)
    }

    // get the recylcer view the adapter is attached to
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView

        // activate drag&drop + swiping
        ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(mRecyclerView)
    }


    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        val dec = DecimalFormat("#,###.##")

        val currentItemList = itemListOfLists[position]
        holder.binding.itemTvGood.text = currentItemList.name


        // handle klick on item in RV
        holder.binding.clItem.setOnClickListener {

            //mainActivity.selectedListId = currentItemList.id

            // select list
            val settings =  mainActivity.settingViewModel.readAllData.value
            if(settings != null){

                settings.selected_list_id = currentItemList.id
                mainActivity.settingViewModel.updateSettings(settings)

                // remove frame if new item is selected
                //----------------------------------------------
                deactivateItemFrame()

                // add frame to selected item
                //----------------------------------------------
                activateItemFrame(holder.binding.cvItem)

                // go back to main activity
                mainActivity.onBackPressed();
            }
        }

        // get cloud button
        val btnCloud = holder.binding.btnCloud
        val itemList = itemListOfLists[position]

        // Set cloud button Icon
        if(itemList.cloud == 0) {
            btnCloud.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_baseline_cloud_outline_24,
                0,
                0,
                0
            )
        } else {
            btnCloud.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_baseline_cloud_full_24,
                0,
                0,
                0
            )
        }

        // Set cloud button
        btnCloud.setOnClickListener {
            // change cloud flag
            if(itemList.cloud == 0){
                itemList.cloud = 1
                mItemListViewModel.addItemList(itemList)
                btnCloud.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_cloud_full_24, 0, 0, 0);

                // Upload List
                mainActivity.fireBaseUtil.uploadList(currentItemList.id)
            }
            else {
                itemList.cloud = 0
                mItemListViewModel.addItemList(itemList)
                btnCloud.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_cloud_outline_24, 0, 0, 0);

                // Delete List from cloud
                // Todo
            }

        }

        //println("List: ${currentItemList.id} | ${mainActivity.settingViewModel.readAllData.value!!.selected_list_id}")
        if(currentItemList.id == mainActivity.settingViewModel.readAllData.value!!.selected_list_id){
            // remove frame if new item is selected
            //----------------------------------------------
            deactivateItemFrame()

            // add frame to selected item
            //----------------------------------------------
            activateItemFrame(holder.binding.cvItem)
        }
    }

    fun activateItemFrame(itemCardView: com.google.android.material.card.MaterialCardView?) {
        itemCardView?.strokeWidth = 8
        itemCardView?.invalidate()
        selectedList = itemCardView
    }

    fun deactivateItemFrame() {
        println("Deselect list: ${selectedList}")
        if (selectedList != null) {
            selectedList!!.strokeWidth = 0
            selectedList!!.invalidate()
        }
    }

    override fun getItemCount(): Int {
        return itemListOfLists.size
    }

    fun Int.toBoolean() = this == 1

    fun setData(itemLists: List<ItemList>) {
        itemListOfLists = itemLists.toMutableList()
        notifyDataSetChanged()
    }


    // the logic behind swiping and dragging
    private val simpleItemTouchCallback =
        object : ItemTouchHelper.SimpleCallback(
            // [1] The allowed directions for moving (drag-and-drop) items
            //UP or DOWN or START or END,
            // [2] The allowed directions for swiping items
            //2
            UP or DOWN, LEFT or RIGHT
        ) {

            var start_swap: Int = 9999
            var end_swap: Int = 0

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val adapter = recyclerView.adapter
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition

                val dummy_item_id = itemListOfLists[from].id
                itemListOfLists[from].id = itemListOfLists[to].id
                itemListOfLists[to].id = dummy_item_id
                Collections.swap(itemListOfLists, from, to)

                if (from < start_swap) start_swap = from
                if (to < start_swap) start_swap = to
                if (from > end_swap) start_swap = from
                if (to > end_swap) start_swap = to

                // [5] Tell the adapter to switch the 2 items
                adapter?.notifyItemMoved(from, to)

                return true
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                val pos = viewHolder.adapterPosition
                val swipedList = itemListOfLists[pos]
                mItemListViewModel.rmItemList(swipedList)
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder);
                // [7] Do something when the interaction with an item
                // Update Room Database
                mItemListViewModel.addItemLists(itemListOfLists.slice(start_swap..end_swap))
            }
        }
}