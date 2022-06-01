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

    private val mItemViewModel = mainActivity.mItemViewModel
    private val mItemListViewModel = mainActivity.mItemListViewModel
    private val fireBaseUtil = mainActivity.fireBaseUtil

    fun addItem(item: Item){
        mItemViewModel.addItem(item)
        fireBaseUtil.uploadItem(item)
    }

    fun addItems(items : List<Item>){
        mItemViewModel.addItemList (items)
        fireBaseUtil.uploadItems(items)
    }

    fun deleteItem(item: Item){
        fireBaseUtil.deleteItem(item)
        mItemViewModel.rmItem(item)
    }

    fun deleteList(itemList: ItemList){

        // delete data from firestore
        fireBaseUtil.rmList(itemList)
        // delete items
        mItemViewModel.rmItems(itemList)
        // delete itemList
        mItemListViewModel.rmItemList(itemList)
    }

}