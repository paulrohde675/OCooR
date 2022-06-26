package com.example.ocoor.Utils

import androidx.lifecycle.LiveData
import androidx.room.*


@Entity(tableName = "item_list_data")
data class ItemList(
    @PrimaryKey(autoGenerate = true) var id: Int, //
    @ColumnInfo(name = "name") var name: String = "My List",
    @ColumnInfo(name = "cloud") var cloud: Int = 0,
    @ColumnInfo(name = "fid") var fid: String = "",
    @ColumnInfo(name = "userID") var userID: String = "",
    @ColumnInfo(name = "collab") var collab: ArrayList<String> = ArrayList(),
) {
    companion object {
        fun from(map: Map<String, Any>) = object {
            val id = 0
            val name = map["name"] as String
            val fid = map["fid"] as String
            val cloud = 1
            val userID = map["userID"] as String
            val data = ItemList(id = id, name = name, fid = fid, cloud = cloud, userID = userID)
        }.data
    }
}

@Dao
interface ItemListDao {
    @Query("SELECT * FROM item_list_data")
    fun getAll(): List<ItemList>

    @Query("SELECT * FROM item_list_data WHERE id IN (:itemIds)")
    fun loadAllByIds(itemIds: IntArray): List<ItemList>

    @Query("SELECT * FROM item_list_data WHERE id = (:getId)")
    fun getItemById(getId: Int): ItemList?

    @Insert(onConflict = OnConflictStrategy.REPLACE) //REPLACE //IGNORE
    fun addItem(item: ItemList): Long

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

    fun addItem(itemList:ItemList): Long{
        return itemListDao.addItem(itemList)
    }

    fun getItem(getId:Int) : ItemList?{
        return itemListDao.getItemById(getId)
    }

    fun rmItem(itemList:ItemList){
        itemListDao.rmItem(itemList)
    }

}