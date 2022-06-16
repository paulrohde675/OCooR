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

    init {
        //db = Firebase.firestore
        Log.d("firebase", "Init Firebase")
        Log.d("firebase", "userID: ${mainActivity.userID}")
        addLiveDataListener()
    }

    private fun addLiveDataListener(){
        db.collection("lists")
            .whereArrayContains("collab", mainActivity.userID!!)
            .addSnapshotListener(mainActivity) { snapshots, e ->

                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> Log.d(TAG, "New city: ${dc.document.data}")
                        DocumentChange.Type.MODIFIED -> Log.d(TAG, "Modified city: ${dc.document.data}")
                        DocumentChange.Type.REMOVED -> Log.d(TAG, "Removed city: ${dc.document.data}")
                    }
                }
            }

        db.collection("lists")
            .whereEqualTo("userID", mainActivity.userID!!)
            .addSnapshotListener(mainActivity) { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> Log.d(TAG, "New city: ${dc.document.data}")
                        DocumentChange.Type.MODIFIED -> Log.d(TAG, "Modified city: ${dc.document.data}")
                        DocumentChange.Type.REMOVED -> Log.d(TAG, "Removed city: ${dc.document.data}")
                    }
                }
            }
    }

    fun uploadItem(item: Item){
        // Add single item to firebase
        val itemList = mItemListViewModel.readAllData.value?.filter { it.id == item.list_id }?.get(0)
        if(itemList != null){

            // check if itemList is synced
            if(itemList.fid == ""){
                return
            }

            val newItem = db.collection("lists").document(itemList.fid).collection("items").document()
            newItem.set(item)

            // update item
            item.fid = newItem.id
            mItemViewModel.addItem(item)
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
                db.collection("lists").document(itemList.fid).collection("items").document(item.id.toString()).set(item)
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
        newItemList.set(itemList)

        // add firebase id to itemList
        itemList.fid = newItemList.id
        mItemListViewModel.addItemList(itemList) // this write statement could probably be omitted

        // get list items
        val items = mItemViewModel.readAllData.value!!.filter {item -> item.list_id == itemListID}

        // add items to new list in firestore
        for (item in items){

            // add item to new list in firestore
            newItemList.collection("items").document(item.id.toString()).set(item)
                .addOnSuccessListener {
                    Log.d(TAG, "Added item with ID ${item.id}")
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error adding document $exception")
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

        // Check if already in cloud
        if (itemList.cloud == 0) {
            return
        }

        val itemListID = itemList.id

        // get items from local itemList
        val locItems = mItemViewModel.readAllData.value!!.filter {item -> item.list_id == itemListID}

        // get firestore list reference
        val fItemList = db.collection("lists").document(itemList.fid)

        // add items to new list in firestore
        for (locItem in locItems){

            // delete item from list in firestore
            fItemList.collection("items").document(locItem.id.toString()).delete()
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

