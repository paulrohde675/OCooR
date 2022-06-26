package com.example.ocoor.Adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.example.listed.Utils.ImageDataBase
import com.example.ocoor.Fragments.AddItemFragment
import com.example.ocoor.MainActivity
import com.example.ocoor.R
import com.example.ocoor.Utils.Item
import com.example.ocoor.Utils.ItemViewModel
import com.example.ocoor.databinding.ItemLayoutBinding
import java.text.DecimalFormat
import java.util.*


class ItemAdapter(
    var itemList: List<Item>,
    val mItemViewModel: ItemViewModel,
    val mainActivity: MainActivity
) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    // variables
    private val dbif = mainActivity.dbif
    var mRecyclerView: RecyclerView? = null
    var selectedItem: com.google.android.material.card.MaterialCardView? = null
    val imageDB = ImageDataBase(mainActivity).imageNames

    inner class ItemViewHolder(val binding: ItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //fun bindView(item: Item) {
        //    binding.apply {
        //        itemTextView.text = item.itemText
        //    }
        //}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ItemViewHolder(binding)
    }

    // get the recylcer view the adapter is attached to
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView

        // activate drag&drop + swiping
        ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(mRecyclerView)
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val dec = DecimalFormat("#,###.##")

        val currentItem = itemList[position]
        holder.binding.itemTvUnit.text = currentItem.unit
        holder.binding.itemTvAmount.text = dec.format(currentItem.amount).toString()
        holder.binding.itemTvGood.text = currentItem.good
        holder.binding.itemCheckbox.isChecked = currentItem.status.toBoolean()

        // update item when button is checked
        holder.binding.itemCheckbox.setOnClickListener() {

            // if add item view is open, close it!
            if (mainActivity.getSupportFragmentManager()
                    .findFragmentByTag(mainActivity.ADD_ITEM_TAG) is AddItemFragment
            ) {
                if(selectedItem == holder.binding.cvItem){
                    mainActivity.onBackPressed();
                }
            }

            itemList[position].status = holder.binding.itemCheckbox.isChecked.toString()
            val newItem = itemList[position]
            newItem.status = "True"
            dbif.addItem(newItem)
        }

        //


        // handle klick on item in RV
        holder.binding.clItem.setOnClickListener {

            // open AddItemFragment if not already open
            if (mainActivity.getSupportFragmentManager()
                    .findFragmentByTag(mainActivity.ADD_ITEM_TAG) !is AddItemFragment
            ) {
                // open AddItemFragment with info from item (text and id to override)
                mainActivity.addItemFragment.initText = itemList[position].itemText
                mainActivity.addItemFragment.itemID = itemList[position].id

                // blend in AddItemFragment
                mainActivity.supportFragmentManager.beginTransaction().apply {
                    replace(
                        R.id.fl_add_itemd,
                        mainActivity.addItemFragment,
                        mainActivity.ADD_ITEM_TAG
                    )
                    addToBackStack(null)
                    commit()
                }
            } else { // if AddItem already open: update text in editTexField
                mainActivity.addItemFragment.edit_text.setText(itemList[position].itemText)
                mainActivity.addItemFragment.itemID = itemList[position].id

            }

            // remove frame if new item is selected
            //----------------------------------------------
            deactivateItemFrame()

            // add frame to selected item
            //----------------------------------------------
            activateItemFrame(holder.binding.cvItem)
        }

        // search image in db
        var imageName = imageDB[currentItem.good.lowercase().trim()]
        if(imageName == null){
            imageName = imageDB[currentItem.good.lowercase().trim().dropLast(1)]
        }
        if(imageName == null){
            imageName = "bu_image"
        }

        // add item image
        val id = mainActivity.resources.getIdentifier(imageName, "drawable",
            mainActivity.packageName
        )
        holder.binding.imageView.setImageResource(id)
        holder.binding.imageView.visibility = View.VISIBLE

    }

    fun activateItemFrame(itemCardView: com.google.android.material.card.MaterialCardView?) {
        itemCardView?.strokeWidth = 8
        itemCardView?.invalidate()
        selectedItem = itemCardView
    }

    fun deactivateItemFrame() {
        println("Deselect item: ${selectedItem}")
        if (selectedItem != null) {
            selectedItem!!.strokeWidth = 0
            selectedItem!!.invalidate()
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun Int.toBoolean() = this == 1

    fun setData(items: List<Item>) {
        itemList = items.toMutableList()
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
            var swaped: Boolean = false

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val adapter = recyclerView.adapter
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition

                val dummy_item_id = itemList[from].id
                itemList[from].id = itemList[to].id
                itemList[to].id = dummy_item_id
                Collections.swap(itemList, from, to)

                swaped = true
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

                // do not swipe selected view
                if(selectedItem == viewHolder.itemView){
                    mainActivity.onBackPressed();
                }

                val pos = viewHolder.adapterPosition
                val newItem = itemList[pos]
                newItem.status = "True"
                dbif.addItem(newItem)
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder);
                // [7] Do something when the interaction with an item
                // Update Room Database
                if(swaped){
                    dbif.addItems(itemList.slice(start_swap..end_swap))
                }
            }
        }
}