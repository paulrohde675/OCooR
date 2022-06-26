package FireBase

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.example.ocoor.MainActivity
import com.example.ocoor.Utils.Item
import com.example.ocoor.Utils.ItemList
import com.example.ocoor.Utils.SingletonHolder
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class FireBaseUtil(val context: Context) {
    companion object : SingletonHolder<FireBaseUtil, Context>(::FireBaseUtil)
    //val fireStoreDatabase = FirebaseFirestore.getInstance()
    var db : FirebaseFirestore = FirebaseFirestore.getInstance()
    val mainActivity = context as MainActivity
    val mItemViewModel = mainActivity.mItemViewModel
    val mItemListViewModel = mainActivity.mItemListViewModel
    var fids = arrayListOf<String>()

    init {
        Log.d("firebase", "Init Firebase")
        Log.d("firebase", "userID: ${mainActivity.userID}")
        addLiveDataListener()
    }

    private fun addLiveDataListener(){

        // add itemList listener
        db.collection("lists")
            .whereArrayContains("collab", mainActivity.userID!!)
            .addSnapshotListener(mainActivity) { snapshots, e ->

                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            // get new itemList
                            val itemList = ItemList.from(dc.document.data)
                            itemList.fid = dc.document.id

                            // if the itemlist is new: set listener to its items
                            if (!fids.contains(itemList.fid)){
                                fids.add(itemList.fid)
                                addListenerForFid(itemList.fid)
                                refreshItems(itemList.fid)
                            }

                            // save new itemList
                            downloadItemList(itemList)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            // get new itemList
                            val itemList = ItemList.from(dc.document.data)
                            itemList.fid = dc.document.id

                            // if the itemlist is new: set listener to its items
                            if (!fids.contains(itemList.fid)){
                                fids.add(itemList.fid)
                                addListenerForFid(itemList.fid)
                                refreshItems(itemList.fid)
                            }

                            // save new itemList
                            downloadItemList(itemList)
                        }
                        DocumentChange.Type.REMOVED -> {

                            Log.d(TAG, "Delete ItemList")

                            // get item to delete
                            val itemList = ItemList.from(dc.document.data)
                            itemList.fid = dc.document.id

                            // delete itemList
                            deleteItemListInc(itemList)
                        }
                    }
                }
            }

        // find all cloud lists
        getFids()

        // add listener to corresponding items
        for (fid in fids){
            addListenerForFid(fid)
            refreshItems(fid)
        }

        Log.d("firebase", "${db.collection("lists")
            .whereEqualTo("userID", mainActivity.userID!!)}")
    }

    fun refreshLists(){
        db.collection("lists")
            .whereArrayContains("collab", mainActivity.userID!!).get()
    }

    fun refreshItems(fid : String){
        db.collection("items").whereEqualTo("list_fid", fid)
    }

    // add listener to items corresponding to a single fid
    fun addListenerForFid(fid : String){
        db.collection("items").whereEqualTo("list_fid", fid)
            .addSnapshotListener(mainActivity) { snapshots, e ->

                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {

                            Log.d(TAG, "Listen to Item")

                            // get new item
                            val item = Item.from(dc.document.data)
                            item.fid = dc.document.id

                            // save new item
                            downloadItem(item)
                        }
                        DocumentChange.Type.MODIFIED -> {

                            Log.d(TAG, "Modify Item")

                            // get new item
                            val item = Item.from(dc.document.data)
                            item.fid = dc.document.id

                            // save new item
                            downloadItem(item)
                        }
                        DocumentChange.Type.REMOVED -> {

                            Log.d(TAG, "Delete Item")

                            // get item to delete
                            val item = Item.from(dc.document.data)
                            item.fid = dc.document.id

                            // delete item
                            deleteItemInc(item)
                        }
                    }
                }
            }
    }

    fun getFids(){
        fids = arrayListOf()
        val itemLists = mItemListViewModel.readAllData.value

        if (itemLists != null){
            for (itemList in itemLists){
                if (!fids.contains(itemList.fid)){
                    fids.add(itemList.fid)
                }
            }
        }
    }

    fun deleteItemInc(deleteItem: Item){
        // check if item exists
        val oldItem = mItemViewModel.readAllData.value?.filter {  it.fid == deleteItem.fid }
        if(oldItem != null && oldItem.isNotEmpty()){
            deleteItem.id = oldItem[0].id
            mItemViewModel.rmItem(deleteItem)
        }
    }

    fun downloadItem(newItem: Item){

        // check if item already exists
        val oldItem = mItemViewModel.getItemByFid(newItem.fid)
        if(oldItem != null){
            newItem.id = oldItem.id
            newItem.list_id = oldItem.list_id

            Log.d(TAG, "Replace DL Item")
        } else {
            // otherwise find correspnding list
            val listId = mItemListViewModel.getItemListByFid(newItem.list_fid)?.id
            if (listId != null){
                newItem.list_id = listId
            } else {
                Log.d(TAG, "No list found for list_fid!")
            }
        }




        // save downloaded item
        mItemViewModel.addItem(newItem)
    }

    fun deleteItemListInc(deleteItemList: ItemList){
        // check if itemList already exists
        val oldItemList = mItemListViewModel.getItemListByFid(deleteItemList.fid)
        if(oldItemList != null){
            deleteItemList.id = oldItemList.id
            mItemListViewModel.rmItemList(deleteItemList)
        }
    }

    fun downloadItemList(newItemList: ItemList){

        // check if itemList already exists
        val oldItemList = mItemListViewModel.getItemListByFid(newItemList.fid)
        if(oldItemList != null){
            newItemList.id = oldItemList.id
        }

        // save downloaded itemList
        mItemListViewModel.addItemList(newItemList)
    }

    fun uploadItem(item: Item){

        Log.d("firestore", "Upload Item")

        // Add single item to firebase
        val itemList = mItemListViewModel.getItemListById(item.list_id)
        if(itemList != null){

            // check if itemList is synced
            if(itemList.fid == ""){
                Log.d(TAG, "Stop uplading, list has no fid")
                return
            }

            // save firestore list_id
            item.list_fid = itemList.fid

            // upload item
            if(item.fid != ""){
                val updateItem = db.collection("items").document(item.fid)
                updateItem.set(item)
            } else {
                val updateItem = db.collection("items").document()
                item.fid = updateItem.id
                updateItem.set(item)
            }
        }
    }

    fun deleteItem(item: Item){
        // Delete single item to firebase
        val itemList = mItemListViewModel.readAllData.value?.filter { it.id == item.list_id }?.get(0)
        if(itemList != null){

            // check if itemList is synced
            if(itemList.fid == ""){
                return
            }

            db.collection("lists").document(itemList.fid).collection("items").document(item.id.toString()).delete()
        }
    }

    fun uploadItems(items : List<Item>){
        // Add single item to firebase

        val itemList = mItemListViewModel.readAllData.value?.filter { it.id == items[0].list_id }?.get(0)
        if(itemList != null){

            // check if itemList is synced
            if(itemList.fid == ""){
                return
            }
            for (item in items){
                db.collection("items").document(item.id.toString()).set(item)
            }
        }
    }

    fun uploadList(itemList: ItemList){
        val itemListID = itemList.id

        // Check if already in cloud
        if(itemList.cloud == 1){
            return
        }

        if (mainActivity.userID == null){
            println("No UserID found!")
            return
        }

        // update itemList
        itemList.cloud = 1
        itemList.userID = mainActivity.userID!!
        itemList.collab.add(mainActivity.userID!!)

        // init new list on firestore
        //------------------------------------------------
        // add list to firestore
        val newItemList = db.collection("lists").document()
        itemList.fid = newItemList.id
        newItemList.set(itemList)

        // add firebase id to itemList
        //mItemListViewModel.addItemList(itemList) // this write statement could probably be omitted

        // get list items
        val items = mItemViewModel.readAllData.value!!.filter {item -> item.list_id == itemListID}

        // add items to new list in firestore
        for (item in items) {

            // save firestore list_id
            item.list_fid = itemList.fid

            // upload item
            if (item.fid != "") {
                val updateItem = db.collection("items").document(item.fid)
                updateItem.set(item)
            } else {
                val updateItem = db.collection("items").document()
                item.fid = updateItem.id
                updateItem.set(item)
            }
        }
    }

    fun modifyList(itemList: ItemList){
        // Check if already in cloud
        if (itemList.cloud == 0) {
            return
        }

        // get firestore list reference
        val fItemList = db.collection("lists").document(itemList.fid)
        fItemList.set(itemList)
    }

    fun rmList(itemList: ItemList) {
        val itemListID = itemList.id

        // get items from local itemList
        val locItems = mItemViewModel.readAllData.value!!.filter {item -> item.list_id == itemListID}

        // get firestore list reference
        val fItemList = db.collection("lists").document(itemList.fid)

        // add items to new list in firestore
        for (locItem in locItems){

            // delete item from list in firestore
            fItemList.collection("items").document(locItem.fid).delete()
                .addOnSuccessListener {
                    Log.d(TAG, "Deleted item with ID ${locItem.id}")
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error adding document $exception")
                }
        }

        // delete list in firestore
        fItemList.delete()
    }
}

