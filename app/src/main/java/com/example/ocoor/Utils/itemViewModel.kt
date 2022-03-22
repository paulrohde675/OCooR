package com.example.ocoor.Utils

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<Item>>
    private val repository: ItemRepository

    init {
        println("Test")
        val userDao = AppDatabase.getDatabase(application).itemDao()
        repository = ItemRepository(userDao)
        readAllData = repository.readAllData
    }

    fun addItem(item: Item){
        viewModelScope.launch (Dispatchers.IO){
            repository.addItem(item)
        }
    }

    fun addItemList(items : List<Item>){
        viewModelScope.launch (Dispatchers.IO){
            for (item in items){
                repository.addItem(item)
            }
        }
    }

    fun rmItem(user: Item){
        viewModelScope.launch (Dispatchers.IO){
            repository.rmItem(user)
        }
    }

}

