package com.example.ocoor.Utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true) var id: Int, //
    @ColumnInfo(name = "status") var status: String = "False",
    @ColumnInfo(name = "fid") var fid: String = "",
    @ColumnInfo(name = "list_fid") var list_fid: String = "",
    @ColumnInfo(name = "list_id") var list_id: Int,
    @ColumnInfo(name = "itemText") val itemText: String = "",
    @ColumnInfo(name = "unit") var unit: String = "",
    @ColumnInfo(name = "amount") var amount: Float = 1f,
    @ColumnInfo(name = "good") var good: String = ""
) {
    companion object {
        fun from(map: Map<String, Any>) = object {
            val id = 0
            val status = map["status"] as String
            val fid = map["fid"] as String
            val list_fid = map["list_fid"] as String
            val list_id = map["list_id"] as Long
            val itemText = map["itemText"] as String
            val unit = map["unit"] as String
            val amount = map["amount"] as Double
            val good = map["good"] as String
            val data = Item(
                    id = id,
                    status = status,
                    fid = fid,
                    list_fid = list_fid,
                    list_id = list_id.toInt(),
                    itemText = itemText,
                    unit = unit,
                    amount = amount.toFloat(),
                    good = good,
                )
        }.data
    }
}

@Dao
interface ItemDao {
    @Query("SELECT * FROM item")
    fun getAll(): List<Item>

    @Query("SELECT * FROM item WHERE id IN (:itemIds)")
    fun loadAllByIds(itemIds: IntArray): List<Item>

    @Query("SELECT * FROM item WHERE id = (:getId)")
    fun getItemById(getId: Int): Item?

    @Insert(onConflict = OnConflictStrategy.REPLACE) //REPLACE //IGNORE
    fun addItem(item: Item) : Long

    @Query("SELECT * FROM item ORDER BY id DESC")
    fun readAllData(): LiveData<List<Item>>

    //@Update
    //suspend fun updateItem()

    @Query("delete from item where id in (:idList)")
    fun rmItems(idList: List<Int>)

    @Delete
    fun rmItem(item: Item)

    @Query("DELETE FROM item")
    suspend fun deleteAllItems()

    @Query("SELECT COUNT(id) FROM item")
    fun getCount(): Int
}

class ItemRepository(private val itemDao: ItemDao){

    val readAllData: LiveData<List<Item>> = itemDao.readAllData()

    suspend fun addItem(item:Item): Int{
        return itemDao.addItem(item).toInt()
    }

    suspend fun getItem(getId:Int) : Item?{
        return itemDao.getItemById(getId)
    }

    suspend fun rmItem(item:Item){
        itemDao.rmItem(item)
    }

    suspend fun rmItems(idList: List<Int>){
        itemDao.rmItems(idList)
    }

}