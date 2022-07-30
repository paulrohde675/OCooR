package com.example.ocoor.Utils

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "setting_data")
data class SettingData(
    @PrimaryKey(autoGenerate = false) var id: Int = 1,
    @ColumnInfo(name = "ocr_type") var ocr_type: String = "complex",
    @ColumnInfo(name = "selected_list_id") var selected_list_id: Int = 1,
    @ColumnInfo(name = "loggedin") var loggedin: Int = 1)

@Dao
interface SettingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSettings(settingData:SettingData)

    @Query("SELECT * FROM setting_data WHERE id = 1")
    fun readSetting(): LiveData<SettingData>

    @Query("SELECT * FROM setting_data")
    fun hasSettings(): SettingData?

    //@Update
    //fun updateSettiongs(settingData: SettingData)

    @Query("SELECT COUNT(id) FROM setting_data")
    fun getCount(): Int
}

class SettingRepository(private val settingDao: SettingDao){

    val readSetting: LiveData<SettingData> = settingDao.readSetting()

    //suspend fun initSetting(settingData:SettingData){
    //    settingDao.addItem(settingData)
    //}

    suspend fun updateSettings(settingData:SettingData){
        settingDao.updateSettings(settingData)
    }

    fun hasSettings(): Boolean {
        return settingDao.hasSettings() != null
    }
}