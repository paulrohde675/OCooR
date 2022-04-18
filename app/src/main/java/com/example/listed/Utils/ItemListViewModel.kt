package com.example.ocoor.Utils

import android.app.Application
import androidx.lifecycle.*
import com.example.ocoor.Units.BaseUnit
import com.google.android.gms.common.internal.FallbackServiceBroker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.utf8Size

class ItemListViewModel(application: Application): AndroidViewModel(application) {

    var readAllData: LiveData<List<ItemList>>
    private val repository: ItemListRepository

    init {
        val itemListDao = AppDatabase.getDatabase(application).itemListDao()
        repository = ItemListRepository(itemListDao)
        readAllData = repository.readAllData
    }

    fun addItemList(itemList: ItemList){
        viewModelScope.launch (Dispatchers.IO){
            repository.addItem(itemList)
        }
    }

    fun rmItemList(itemList: ItemList){
        viewModelScope.launch (Dispatchers.IO){
            repository.rmItem(itemList)
        }
    }

    fun addItemLists(itemLists : List<ItemList>){
        viewModelScope.launch (Dispatchers.IO){
            for (itemList in itemLists){
                repository.addItem(itemList)
            }
        }
    }

}

