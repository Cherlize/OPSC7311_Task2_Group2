package com.example.opsc7311_task2_group2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.view.menu.MenuBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_image_taker.*

class ViewItemDetails : AppCompatActivity()
{
    private val itemDetails = arrayListOf<String>()
    private var passedIntent = intent
    var numberOfItems : Int = 0
    var entry = Entry()

    var auth = FirebaseAuth.getInstance();
    var userID : String = ""
    //private var passedCategory = passedIntent.getStringExtra("Category")
    //private var passedItem = intent.getStringExtra("Item")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_item_details)
        userID = auth!!.currentUser?.uid.toString()
        //Toast.makeText(this, "Category Selected ", Toast.LENGTH_LONG).show()
       // val listView = findViewById<ListView>(R.id.listItemDetails)
        readCollections()

        val backToItems = findViewById<Button>(R.id.btnBackToItems)
        backToItems.setOnClickListener{
            val newIntent = Intent(this, ViewItems::class.java)
            newIntent.putExtra("Category", intent.getStringExtra("Category"))
            startActivity(newIntent)
        }

        val addItems = findViewById<Button>(R.id.btnAddItem)

        addItems.setOnClickListener{


            addNumber()

        }

        val subtracItems = findViewById<Button>(R.id.btnRemoveItem)

        subtracItems.setOnClickListener{
            subtractNumber()

        }
        val deleteCurrentItem = findViewById<Button>(R.id.btnDeleteItem)

        deleteCurrentItem.setOnClickListener{
            deleteItem()
        }

    }

    private fun deleteItem(){
        var passedCategory = intent.getStringExtra("Category")
        var passedItem = intent.getStringExtra("Item")
        val db = FirebaseFirestore.getInstance()
        db.collection("Users").document(userID).collection("Categories").document(passedCategory.toString()).collection("Items").document(passedItem.toString())
            .delete()

        val newIntent = Intent(this, ViewItems::class.java)
        newIntent.putExtra("Category", intent.getStringExtra("Category"))
        startActivity(newIntent)

    }

    private fun readCollections()
    {
        var image : String = ""
        var passedCategory = intent.getStringExtra("Category")
        var passedItem = intent.getStringExtra("Item")
        val db = FirebaseFirestore.getInstance()

        db.collection("Users").document(userID).collection("Categories").document(passedCategory.toString()).collection("Items")
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    for (document in it.result!!) {
                        //if(document.id == passedCategory){
                            if(document.data.getValue("itemName").toString() == passedItem){
                                val category = passedCategory
                                val itemName = passedItem
                                val description = document.data.getValue("description").toString()
                                image = document.data.getValue("image").toString()
                                val date = document.data.getValue("date").toString()
                                val numItems = document.data.getValue("numItems").toString()
                                numberOfItems = document.data.getValue("numItems").toString().toInt()

                                entry.category = category.toString()
                                entry.itemName = itemName.toString()
                                entry.description = description.toString()
                                entry.urlCode = image
                                entry.date = date.toString()
                                entry.numItems = numItems.toInt()



                                Toast.makeText(this, "Category Selected "+ category, Toast.LENGTH_LONG).show()
                                itemDetails.add( "Category: " + category)
                                itemDetails.add("Item: " + itemName)
                                itemDetails.add("Description: " + description)
                                //itemDetails.add("Image: " + image)
                                itemDetails.add("Date Added: " + date)
                                itemDetails.add("Number of " + itemName + "s : " + numItems)
                            }

                        //}


                    }
                    val listView = findViewById<ListView>(R.id.listItemDetails)
                    val arrayAdapter : ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemDetails )
                    listView.adapter = arrayAdapter

                    val itemImage = findViewById<ImageView>(R.id.imgtItemImage)
                    if(!image.equals("") ){
                        Picasso.get().load(image).into(itemImage)
                    }



                }
            }
    }

    private fun addNumber(){

        var passedCategory = intent.getStringExtra("Category")
        var passedItem = intent.getStringExtra("Item")
        numberOfItems += 1
        val data = hashMapOf("numItems" to numberOfItems)

        val db = FirebaseFirestore.getInstance()
        db.collection("Users").document(userID).collection("Categories").document(passedCategory.toString()).collection("Items").document(passedItem.toString())
            .set(data, SetOptions.merge())

        itemDetails.clear()
        readCollections()

    }

    private fun saveToDatabase()
    {
        val db = FirebaseFirestore.getInstance()
        val entryMap : MutableMap<String, Any> = HashMap()


        entryMap["date"] = entry.date
        entryMap["category"] = entry.category
        entryMap["description"] = entry.description
        entryMap["image"] = entry.urlCode
        entryMap["itemName"] = entry.itemName
        entryMap["numItems"] = entry.numItems
        //Toast.makeText(this, "Category Selected "+ entry.category, Toast.LENGTH_LONG).show()
        db.collection("Users").document(userID.toString()).collection("Categories").document(entry.category).collection("Items").document(entry.itemName).set(entryMap)
    }

    private fun subtractNumber(){
        var passedCategory = intent.getStringExtra("Category")
        var passedItem = intent.getStringExtra("Item")

        if(numberOfItems > 0){
            numberOfItems -= 1
        }

        val data = hashMapOf("numItems" to numberOfItems)

        val db = FirebaseFirestore.getInstance()
        db.collection("Users").document(userID).collection("Categories").document(passedCategory.toString()).collection("Items").document(passedItem.toString())
            .set(data, SetOptions.merge())

        itemDetails.clear()
        readCollections()
    }


}