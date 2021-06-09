package com.example.opsc7311_task2_group2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class ViewItems : AppCompatActivity()
{
    private val items = arrayListOf<String>()

    private var passedCategory : String =""

    override fun onCreate(savedInstanceState: Bundle?)
    {
        //Toast.makeText(this, "Category Selected "+ passedValue.getStringExtra("Category"), Toast.LENGTH_LONG).show()
        var passedValue = intent

        passedCategory = passedValue.getStringExtra("Category").toString()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_items)

        val addNewItemButton = findViewById<Button>(R.id.btnAddItemView)
        addNewItemButton.setOnClickListener{
            val newIntent = Intent(this, AddNewItem::class.java)
            newIntent.putExtra("Category", passedCategory)
            startActivity(newIntent)
        }

        readCollections()
    }

    private fun readCollections()
    {
        val db = FirebaseFirestore.getInstance()
        db.collection("Items")
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    for (document in it.result!!) {
                       // if(document.data.getValue("category") == intent.getStringExtra("Category")){
                            //val category = document.id.toString()
                            val itemName = document.data.getValue("itemName").toString()
                            //val description = document.data.getValue("description").toString()
                            //val image = document.data.getValue("image").toString()
                            //val date = document.data.getValue("date").toString()


                            //Toast.makeText(this, "Category Selected "+result, Toast.LENGTH_LONG).show()
                            //items.add(category)
                            items.add(itemName)
                            //items.add(description)
                            //items.add(image)
                            //items.add(date)
                       // }


                    }
                    val listView = findViewById<ListView>(R.id.listItems)
                    val arrayAdapter : ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, items )
                    listView.adapter = arrayAdapter
                    listView.setOnItemClickListener { adapterView, view, i, l ->
                        //Toast.makeText(this, "Category Selected "+items[i], Toast.LENGTH_SHORT).show()
                        val newIntent = Intent(this, ViewItemDetails::class.java)
                        newIntent.putExtra("Item", items[i])
                        newIntent.putExtra("Category", passedCategory)
                        startActivity(newIntent)
                    }

                }
            }
    }
}