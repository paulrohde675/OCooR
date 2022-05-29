package com.example.listed.Utils

import Authentication.LoginGoogle
import android.content.Context
import com.example.ocoor.MainActivity
import com.example.ocoor.Utils.Item
import com.example.ocoor.Utils.ItemList
import com.example.ocoor.Utils.ItemViewModel
import com.example.ocoor.Utils.SingletonHolder

class DataBaseInterface(val context: Context) {
    companion object : SingletonHolder<DataBaseInterface, Context>(::DataBaseInterface)
    val mainActivity = context as MainActivity

    val mItemViewModel = mainActivity.mItemViewModel
    val mItemListViewModel = mainActivity.mItemListViewModel
    val fireBaseUtil = mainActivity.fireBaseUtil

    fun addItem(item: Item){
        mItemViewModel.addItem(item)
        fireBaseUtil.uploadItem(item)
    }

    fun deleteList(itemList: ItemList){
        //val itemListID = itemList.id
        //mItemViewModel.readAllData.value?.filter { it.id == itemListID }
        mItemViewModel.rmItems(itemList)


    }

}