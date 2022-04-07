package com.example.ocoor.Utils

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true) var id: Int, //
    @ColumnInfo(name = "status") var status: String = "False",
    @ColumnInfo(name = "itemText") val itemText: String = "",
    @ColumnInfo(name = "unit") var unit: String = "",
    @ColumnInfo(name = "amount") var amount: Float = 1f,
    @ColumnInfo(name = "good") var good: String = ""
)

@Dao
interface ItemDao {
    @Query("SELECT * FROM item")
    fun getAll(): List<Item>

    @Query("SELECT * FROM item WHERE id IN (:itemIds)")
    fun loadAllByIds(itemIds: IntArray): List<Item>

    @Insert(onConflict = OnConflictStrategy.REPLACE) //REPLACE //IGNORE
    fun addItem(item: Item)

    @Query("SELECT * FROM item ORDER BY id DESC")
    fun readAllData(): LiveData<List<Item>>

    //@Update
    //suspend fun updateItem()

    @Delete
    fun rmItem(item: Item)

    @Query("DELETE FROM item")
    suspend fun deleteAllItems()

    @Query("SELECT COUNT(id) FROM item")
    fun getCount(): Int
}

class ItemRepository(private val itemDao: ItemDao){

    val readAllData: LiveData<List<Item>> = itemDao.readAllData()

    suspend fun addItem(item:Item){
        itemDao.addItem(item)
    }

    suspend fun rmItem(item:Item){
        itemDao.rmItem(item)
    }

}