package com.example.ocoor.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.example.ocoor.MainActivity
import com.example.ocoor.R
import com.example.ocoor.Utils.ItemList
import com.example.ocoor.Utils.ItemListViewModel
import com.example.ocoor.databinding.ListLayoutBinding
import kotlinx.android.synthetic.main.list_layout.view.*
import java.text.DecimalFormat
import java.util.*


class ItemListAdapter(
    var itemListOfLists: List<ItemList>,
    val mItemListViewModel: ItemListViewModel,
    val mainActivity: MainActivity
) :
    RecyclerView.Adapter<ItemListAdapter.ItemListViewHolder>() {

    // variables
    private val dbif = mainActivity.dbif
    var mRecyclerView: RecyclerView? = null
    var selectedListView: com.google.android.material.card.MaterialCardView? = null
    var selectedList: ItemList? = null

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
        holder.binding.tvListName.text = currentItemList.name


        // handle click on item in RV
        holder.binding.clItem.setOnClickListener {

            //mainActivity.selectedListId = currentItemList.id

            // select list
            val settings =  mainActivity.settingViewModel.getSettings()

            settings.selected_list_id = currentItemList.id
            mainActivity.settingViewModel.updateSettings(settings)
            selectedList = currentItemList

            // remove frame if new item is selected
            //----------------------------------------------
            deactivateItemFrame()

            // add frame to selected item
            //----------------------------------------------
            activateItemFrame(holder.binding.cvItem)

            // go back to main activity
            //mainActivity.onBackPressed();
        }

        // get cloud button
        val btnCloud = holder.binding.btnCloud

        // Set cloud button Icon
        if(currentItemList.cloud == 0) {
            btnCloud.setIconResource(R.drawable.ic_baseline_cloud_outline_24)
            btnCloud.invalidate()
        } else {
            btnCloud.setIconResource(R.drawable.ic_baseline_cloud_full_24)
            btnCloud.invalidate()
        }

        // Set delete button
        //------------------------------------------------------------------------------------------
        holder.binding.btnDelete.setOnClickListener {
            mainActivity.dbif.deleteList(currentItemList)
        }

        // Set cloud button
        //------------------------------------------------------------------------------------------
        btnCloud.setOnClickListener {

            // if user is logged in: upload list to cloud
            if(mainActivity.userID != null){
                // change cloud flag
                if(currentItemList.cloud == 0){
                    btnCloud.setIconResource(R.drawable.ic_baseline_cloud_full_24)
                    btnCloud.invalidate()

                    // Upload List
                    mainActivity.fireBaseUtil?.uploadList(currentItemList)
                }
            }
            else{ // start login
                mainActivity.loginGoogle.signIn()
            }
        }

        // add collab button to add users to share the itemList with
        val btnCollab = holder.binding.btnShareWithUser
        val etCollab = holder.binding.etEnterUser
        btnCollab.setOnClickListener{
            val newUserId = etCollab.text.toString().trim().lowercase()
            if (newUserId.isBlank()) {
                Toast.makeText(mainActivity, "You did not enter an User", Toast.LENGTH_SHORT).show()
            } // else: add item
            else {
                val collaborators = currentItemList.collab
                if (!collaborators.contains(newUserId)) {
                    collaborators.add(newUserId)
                }
                currentItemList.collab = collaborators
                dbif.modifyItemList(currentItemList)
                etCollab.text?.clear()
            }
        }

        // activate frame when itemList is added
        if(currentItemList.id == mainActivity.settingViewModel.readAllData.value!!.selected_list_id){
            // remove frame if new item is selected
            //----------------------------------------------
            deactivateItemFrame()

            // add frame to selected item
            //----------------------------------------------
            activateItemFrame(holder.binding.cvItem)

            // expand list
            //----------------------------------------------
        }

    }

    fun activateItemFrame(itemCardView: com.google.android.material.card.MaterialCardView?) {
        selectedListView = itemCardView
        println("cloud ${selectedList?.cloud}")
        if(selectedList?.cloud == 1) {
            itemCardView?.et_enter_user?.visibility = View.VISIBLE
            itemCardView?.btn_share_with_user?.visibility = View.VISIBLE
        }
        itemCardView?.strokeWidth = 8
        itemCardView?.invalidate()
    }

    fun deactivateItemFrame() {
        println("Deselect list: ${selectedList}")
        if (selectedListView != null) {

            selectedListView!!.et_enter_user.visibility = View.GONE
            selectedListView!!.btn_share_with_user.visibility = View.GONE

            selectedListView!!.strokeWidth = 0
            selectedListView!!.invalidate()
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
                dbif.deleteList(swipedList)
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