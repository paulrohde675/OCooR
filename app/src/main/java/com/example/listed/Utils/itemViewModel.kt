package com.example.ocoor.Utils

import android.app.Application
import androidx.lifecycle.*
import com.example.ocoor.Units.BaseUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import okio.utf8Size
import java.util.stream.Collectors

class ItemViewModel(application: Application): AndroidViewModel(application) {

    var readAllData: LiveData<List<Item>>
    private val repository: ItemRepository

    init {
        val userDao = AppDatabase.getDatabase(application).itemDao()
        repository = ItemRepository(userDao)
        readAllData = repository.readAllData
    }

    fun addItem(item: Item): Int?{
        var id : Int? = null
        viewModelScope.launch (Dispatchers.IO){
            id = repository.addItem(item)
        }
        return id
    }

    fun addItemList(items : List<Item>){
        viewModelScope.launch (Dispatchers.IO){
            for (item in items){
                repository.addItem(item)
            }
        }
    }

    fun rmItems(itemList: ItemList){
        // get all items from itemList
        val items = readAllData.value?.filter { it.list_id == itemList.id }
        if(items != null){
            // get id from items
            val idList: List<Int> = items.stream().map(Item::id).collect(Collectors.toList()) as List<Int>

            // remove items
            viewModelScope.launch (Dispatchers.IO){
                repository.rmItems(idList)
            }
        }
    }

    fun rmItem(item: Item){
        viewModelScope.launch (Dispatchers.IO){
            repository.rmItem(item)
        }
    }

    fun getItemByFid(getId:String) : Item?{
        var item:Item? = null
        item = readAllData.value?.findLast {  it.fid == getId }
        return item
    }

    fun mergeItemWithList(newItem: Item):Boolean{

        val itemList = readAllData.value!!
        var matchItem:Item? = null
        var newGoodName:String? = null
        var multMerges = false
        var convertable = false

        // search for item in list that matches
        for (itemInList in itemList){

            // check if item is active
            if(itemInList.status == "True"){
                continue
            }
            // only merge wit items in same list
            if(itemInList.list_id != newItem.list_id){
                continue
            }


            // first look if the good similar
            // Todo: if two or more merges are possible

            if(newGoodName == null){
                newGoodName = compareGoods(newItem.good, itemInList.good)
                if(newGoodName != null){
                    matchItem = itemInList
                }
            } else {
                // if multiple merges are possile dont merge
                if(compareGoods(newItem.good, itemInList.good) != null){
                    multMerges = true
                    break
                }
            }
        }

        if ((matchItem != null) && !multMerges){
            // check if unit is the same
            // todo: if unit is not the same, change it!
            // merge new item with old item

            var conversionFactor = 1f

            // try to convert units
            if(newItem.unit == matchItem.unit) {
                convertable = true
            } else {
                if ((newItem.unit in BaseUnit.massUnit) && (matchItem.unit in BaseUnit.massUnit)){
                    val num = BaseUnit.massUnit.getValue(newItem.unit)
                    val denom = BaseUnit.massUnit.getValue(matchItem.unit)
                    conversionFactor = num/denom
                    println("conversionFactor: $conversionFactor")
                    convertable = true
                }
                else if ((newItem.unit in BaseUnit.volumeUnit) && (matchItem.unit in BaseUnit.volumeUnit)){
                    val num = BaseUnit.volumeUnit.getValue(newItem.unit)
                    val denom = BaseUnit.volumeUnit.getValue(matchItem.unit)
                    conversionFactor = num/denom
                    println("conversionFactor: $conversionFactor")
                    convertable = true
                }
                else if ((newItem.unit in BaseUnit.countableUnit) && (matchItem.unit in BaseUnit.countableUnit)){
                    val num = BaseUnit.countableUnit.getValue(newItem.unit)
                    val denom = BaseUnit.countableUnit.getValue(matchItem.unit)
                    conversionFactor = num/denom
                    println("conversionFactor: $conversionFactor")
                    convertable = true
                }
            }

            if(convertable){
                viewModelScope.launch (Dispatchers.IO) {
                    matchItem.amount += newItem.amount*conversionFactor
                    repository.addItem(matchItem)
                }
                return true
            }
        }
        return false
    }

    private fun compareGoods(good1 : String, good2 : String):String?{

        val goodName1 = good1.lowercase().trim()
        val goodName2 = good2.lowercase().trim()

        // check number of words
        // only accept if same number
        val nWords1 = goodName1.split("\\s+".toRegex()).size
        val nWords2 = goodName2.split("\\s+".toRegex()).size
        if(nWords1 != nWords2){
            println("Not same number of words: $nWords1 $nWords2")
            return null
        }

        //compare words
        if(good1.lowercase().contains(goodName2) or goodName2.contains(goodName1)){

            // return the longer one (assuming the longer probaly is the plural)
            if(good1.utf8Size() > good2.utf8Size())
                return(good1)
            else {
                return(good2)
            }
        }
        else if(goodName2.contains(goodName1)){
            return(good2)
        }
        return null
    }

    fun forceRefresh(){
        readAllData = readAllData
    }
}

