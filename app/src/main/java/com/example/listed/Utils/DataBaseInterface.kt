package com.example.listed.Utils

import Authentication.LoginGoogle
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.example.ocoor.MainActivity
import com.example.ocoor.Utils.Item
import com.example.ocoor.Utils.ItemList
import com.example.ocoor.Utils.ItemViewModel
import com.example.ocoor.Utils.SingletonHolder
import com.google.firebase.firestore.proto.TargetGlobal

class DataBaseInterface(val context: Context) {
    companion object : SingletonHolder<DataBaseInterface, Context>(::DataBaseInterface)
    val mainActivity = context as MainActivity

    private val mItemViewModel = mainActivity.mItemViewModel
    private val mItemListViewModel = mainActivity.mItemListViewModel

    fun addItem(item: Item){
        val itemList = mItemListViewModel.getItemListById(item.list_id)
        if(mainActivity.fireBaseUtil != null && itemList?.cloud == 1){
            mainActivity.fireBaseUtil?.uploadItem(item)
        }else{
            mItemViewModel.addItem(item)
        }
    }

    fun addItems(items : List<Item>){

        if (items.isEmpty()){
            return
        }

        val itemList = mItemListViewModel.getItemListById(items[0].list_id)
        if(mainActivity.fireBaseUtil != null && itemList?.cloud == 1){
            mainActivity.fireBaseUtil?.uploadItems(items)
        }else{
            mItemViewModel.addItemList (items)
        }
    }

    fun deleteItem(item: Item){
        val itemList = mItemListViewModel.getItemListById(item.list_id)
        if(mainActivity.fireBaseUtil != null && itemList?.cloud == 1){
            mainActivity.fireBaseUtil?.deleteItem(item)
        }else{
            mItemViewModel.rmItem(item)
        }
    }

    fun addItemList(itemList: ItemList){
        Log.d(TAG, "Add List 1")

        if(mainActivity.fireBaseUtil != null && itemList.cloud == 1){
            Log.d(TAG, "Add List 2")
            mainActivity.fireBaseUtil?.uploadList(itemList)
        }else{
            Log.d(TAG, "Add List 3")
            mItemListViewModel.addItemList(itemList)
        }
    }

    fun deleteList(itemList: ItemList){
        if(mainActivity.fireBaseUtil != null && itemList.cloud == 1){
            // delete data from firestore
            mainActivity.fireBaseUtil?.rmList(itemList)
        }else {
            // delete items
            mItemViewModel.rmItems(itemList)
            // delete itemList
            mItemListViewModel.rmItemList(itemList)
        }
    }

    fun modifyItemList(itemList: ItemList){
        if(mainActivity.fireBaseUtil != null && itemList.cloud == 1){
            // save changes in firebase
            mainActivity.fireBaseUtil?.modifyList(itemList)
        }else {
            // save changes locally
            mItemListViewModel.addItemList(itemList)
        }
    }
}