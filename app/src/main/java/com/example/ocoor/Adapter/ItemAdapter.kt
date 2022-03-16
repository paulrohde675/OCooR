package com.example.ocoor.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.ocoor.MainActivity
import com.example.ocoor.Model.ItemModel
import com.example.ocoor.Utils.Item
import com.example.ocoor.Utils.ItemViewModel
import com.example.ocoor.databinding.ItemLayoutBinding


class ItemAdapter(var itemList: List<Item>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ItemLayoutBinding)
        :RecyclerView.ViewHolder(binding.root){}

    // data_base
    private lateinit var mItemViewModel: ItemViewModel
    //val itemList:List<ItemModel> = emptyList()
    //val activity:MainActivity =

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)


        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.binding.itemCheckbox.text = currentItem.itemText
        holder.binding.itemCheckbox.isChecked = currentItem.status.toBoolean()

        holder.binding.itemCheckbox.setOnClickListener(){
            itemList[position].status = holder.binding.itemCheckbox.isChecked.toString()
        }

    }


    override fun getItemCount(): Int {
        return itemList.size
    }

    fun  Int.toBoolean() = this == 1

    //fun addItem(item: User){
    //    itemList.add(item)
    //    notifyDataSetChanged()
    //}

    fun setData(items: List<Item>){
        itemList = items.toMutableList()
        notifyDataSetChanged()
    }
}