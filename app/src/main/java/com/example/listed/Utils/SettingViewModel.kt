package com.example.ocoor.Utils

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<SettingData>
    private var repository: SettingRepository

    init {
        val userDao = AppDatabase.getDatabase(application).settingDao()
        repository = SettingRepository(userDao)
        readAllData = repository.readSetting

        viewModelScope.launch (Dispatchers.IO) {
            if(!repository.hasSettings()){
                println("init new settings")
                repository.updateSettings(SettingData())
            }else{
                println("No new settings")
            }
        }
    }

    fun getSettings() : SettingData {
        return readAllData.value!!
    }

    fun userLoggedIn() : Boolean{
        return readAllData.value?.loggedin == 1
    }

    fun updateSettings(settingData : SettingData){
        viewModelScope.launch (Dispatchers.IO){
            repository.updateSettings(settingData)
        }
    }
}

