package com.example.opsc7311_task2_group2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore


class AddEntries : AppCompatActivity()
{
    var entry = Entry()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_entries)
        val btnGoToActivity = findViewById<Button>(R.id.btnCancel)
        btnGoToActivity.setOnClickListener{

            goBackToMainActivity()
        }


        val btnCreateEntry = findViewById<Button>(R.id.btnAdd)


        btnCreateEntry.setOnClickListener{
            entry = createEntry()
            saveToDatabase()
            goBackToMainActivity()

        }
        
    }
    private fun createEntry() : Entry
    {

        val date = findViewById<EditText>(R.id.editDate).text.toString()
        val category = findViewById<EditText>(R.id.editCategory).text.toString()
        val description = findViewById<EditText>(R.id.editDescription).text.toString()

        val entry = Entry(description, date, category)

        return entry

    }

    private fun goBackToMainActivity()
    {

        val newTent = Intent(this, MainActivity :: class.java)
        startActivity(newTent)

    }

    private fun saveToDatabase()
    {
        val db = FirebaseFirestore.getInstance()
        val entryMap : MutableMap<String, Any> = HashMap()
        val doc = db.collection("Items")

        entryMap["date"] = entry.date
        entryMap["category"] = entry.category
        entryMap["description"] = entry.description
        entryMap["image"] = entry.urlCode
        entryMap["itemName"] = entry.itemName
        doc.document(entry.category).collection(entry.itemName).add(entryMap)

    }
}