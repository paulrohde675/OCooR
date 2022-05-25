package FireBase

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.example.ocoor.MainActivity
import com.example.ocoor.Utils.Item
import com.example.ocoor.Utils.SingletonHolder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class FireBaseUtil(val context: Context) {
    companion object : SingletonHolder<FireBaseUtil, Context>(::FireBaseUtil)
    //val fireStoreDatabase = FirebaseFirestore.getInstance()
    val db = Firebase.firestore
    val mainActivity = context as MainActivity
    val mItemViewModel = mainActivity.mItemViewModel
    val mItemListViewModel = mainActivity.mItemListViewModel

    fun uploadData() {
        // create a dummy data
        val hashMap = hashMapOf<String, Any>(
            "first_name" to "John",
            "last_name" to "John",
            "email" to "test@test.com",
            "id" to 24
        )

        println("Test Firebase")

        // use the add() method to create a document inside users collection
        db.collection("users")
            .add(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "Added document with ID ${it.id}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding document $exception")
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

            val itemMap = hashMapOf<String, Any>(
                "id" to item.id,
                "status" to item.status,
                "unit" to item.unit,
                "itemText" to item.itemText,
                "amount" to item.amount,
                "good" to item.good,
            )
            db.collection("lists").document(itemList.fid).collection("items").document(item.id.toString()).set(itemMap)
        }
    }

    fun uploadList(itemListID:Int){

        val itemList = mItemListViewModel.readAllData.value?.filter { it.id == itemListID }?.get(0)
        if(itemList != null){

            // init new list on firestore
            //------------------------------------------------
            // add list to firestore
            val newItemList = db.collection("lists").document()
            newItemList.set(itemList)

            // add firebase id to itemList
            itemList.fid = newItemList.id
            mItemListViewModel.addItemList(itemList)

            // init list data
            val itemList = hashMapOf<String, Any>(
                "name" to itemList.name,
                "id" to itemList.id,
            )

            // get list items
            val items = mItemViewModel.readAllData.value!!.filter {item -> item.status == "False" && item.list_id == itemListID}

            // add items to new list in firestore
            for (item in items){
                // init item map
                val itemMap = hashMapOf<String, Any>(
                    "id" to item.id,
                    "status" to item.status,
                    "unit" to item.itemText,
                    "amount" to item.amount,
                    "good" to item.good,
                )

                // add item to new list in firestore
                newItemList.collection("items").add(itemMap)
                    .addOnSuccessListener {
                        Log.d(TAG, "Added item with ID ${it.id}")
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error adding document $exception")
                    }
            }
        }
    }
}

