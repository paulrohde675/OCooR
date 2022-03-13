package com.example.ocoor.Utils

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int, //
    @ColumnInfo(name = "first_name") var status: String?,
    @ColumnInfo(name = "last_name") val itemText: String?
)

@Dao
interface ItemDao {
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

    @Update
    suspend fun updateItem()

    @Delete
    fun rmItem(item: Item)

    @Query("DELETE FROM item")
    suspend fun deleteAllItems()

}

class ItemRepository(private val itemDao: ItemDao){

    val readAllData: LiveData<List<Item>> = itemDao.readAllData()

    suspend fun addItem(item:Item){
        itemDao.addItem(item)
    }

    suspend fun rmItem(item:Item){
        itemDao.rmItem(item)
    }

    suspend fun updateItem(item: Item){
        itemDao.updateItem()
    }

}