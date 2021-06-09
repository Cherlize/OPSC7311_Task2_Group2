package com.example.opsc7311_task2_group2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class AddNewItem : AppCompatActivity()
{
    var entry = Entry()
    //val passedValue = intent
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_item)
        //Toast.makeText(this, "Please work  "+ passedValue.getStringExtra("Category").toString(), Toast.LENGTH_LONG).show()
        val backButton = findViewById<Button>(R.id.btnBackItem)
        backButton.setOnClickListener{
            val intent = Intent(this, ViewCollections::class.java)
            startActivity(intent)
        }

        val btnCreateEntry = findViewById<Button>(R.id.btnSaveItem)


        btnCreateEntry.setOnClickListener{
            entry = createEntry()
            saveToDatabase()


        }
    }

    private fun createEntry() : Entry
    {

        val date = findViewById<EditText>(R.id.editTextDate).text.toString()
        val category = intent.getStringExtra("Category").toString()
        val itemName = findViewById<EditText>(R.id.edtItemName).text.toString()
        val description = findViewById<EditText>(R.id.edtDescription).text.toString()

        val entry = Entry(description, date, category,"No Image", itemName)

        return entry

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
        //Toast.makeText(this, "Category Selected "+ entry.category, Toast.LENGTH_LONG).show()
        db.collection("Items").document(entry.itemName).set(entryMap)
    }


}