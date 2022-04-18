package com.example.ocoor.Utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Entity(tableName = "item_list_data")
data class ItemList(
    @PrimaryKey(autoGenerate = true) var id: Int, //
    @ColumnInfo(name = "name") var name: String = "My List",
)

@Dao
interface ItemListDao {
    @Query("SELECT * FROM item_list_data")
    fun getAll(): List<ItemList>

    @Query("SELECT * FROM item_list_data WHERE id IN (:itemIds)")
    fun loadAllByIds(itemIds: IntArray): List<ItemList>

    @Query("SELECT * FROM item_list_data WHERE id = (:getId)")
    fun getItemById(getId: Int): ItemList?

    @Insert(onConflict = OnConflictStrategy.REPLACE) //REPLACE //IGNORE
    fun addItem(item: ItemList)

    @Query("SELECT * FROM item_list_data ORDER BY id DESC")
    fun readAllData(): LiveData<List<ItemList>>

    //@Update
    //suspend fun updateItem()

    @Delete
    fun rmItem(item: ItemList)

    @Query("DELETE FROM item_list_data")
    suspend fun deleteAllItems()

    @Query("SELECT COUNT(id) FROM item_list_data")
    fun getCount(): Int
}

class ItemListRepository(private val itemListDao: ItemListDao){

    val readAllData: LiveData<List<ItemList>> = itemListDao.readAllData()

    suspend fun addItem(itemList:ItemList){
        itemListDao.addItem(itemList)
    }

    suspend fun getItem(getId:Int) : ItemList?{
        return itemListDao.getItemById(getId)
    }

    suspend fun rmItem(itemList:ItemList){
        itemListDao.rmItem(itemList)
    }

}