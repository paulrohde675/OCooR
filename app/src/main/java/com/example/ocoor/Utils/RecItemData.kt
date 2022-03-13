package com.example.ocoor.Utils

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RecItemDao {
    @Query("SELECT * FROM item")
    fun getAll(): List<Item>

    @Query("SELECT * FROM item WHERE id IN (:itemIds)")
    fun loadAllByIds(itemIds: IntArray): List<Item>

    @Query("SELECT * FROM item WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): Item

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addItem(item: Item)

    @Query("SELECT * FROM item ORDER BY id ASC")
    fun readAllData(): LiveData<List<Item>>

    @Delete
    fun rmItem(item: Item)

    @Query("DELETE FROM item")
    suspend fun deleteAllItems()

}

class RecItemItemRepository(private val recItemDao: RecItemDao){

    val readAllData: LiveData<List<Item>> = recItemDao.readAllData()

    suspend fun addItem(item:Item){
        recItemDao.addItem(item)
    }

    suspend fun rmItem(item:Item){
        recItemDao.rmItem(item)
    }

}