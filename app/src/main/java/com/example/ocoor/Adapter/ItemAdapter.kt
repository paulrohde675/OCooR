package com.example.ocoor.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ocoor.Model.ItemModel
import com.example.ocoor.Utils.User
import com.example.ocoor.databinding.ItemLayoutBinding


class ItemAdapter(var itemList: List<User>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ItemLayoutBinding)
        :RecyclerView.ViewHolder(binding.root)

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
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun  Int.toBoolean() = this == 1

    //fun addItem(item: User){
    //    itemList.add(item)
    //    notifyDataSetChanged()
    //}

    fun setData(items: List<User>){
        itemList = items.toMutableList()
        notifyDataSetChanged()
    }
}