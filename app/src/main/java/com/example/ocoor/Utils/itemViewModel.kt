package com.example.ocoor.Utils

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<User>>
    private val repository: ItemRepository

    init {
        println("Test")
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = ItemRepository(userDao)
        readAllData = repository.readAllData
    }

    fun addUser(user: User){
        viewModelScope.launch (Dispatchers.IO){
            repository.addUser(user)
        }
    }

    fun rmItem(user: User){
        viewModelScope.launch (Dispatchers.IO){
            repository.rmItem(user)
        }
    }

}