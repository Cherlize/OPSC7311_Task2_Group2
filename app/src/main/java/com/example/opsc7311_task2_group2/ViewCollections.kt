package com.example.opsc7311_task2_group2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class ViewCollections : AppCompatActivity() {

    private val categories = arrayListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_collections)

        val addButton = findViewById<Button>(R.id.btnAdd)
        addButton.setOnClickListener{
            val newCollectionIntent = Intent(this, NewCollection::class.java)
            startActivity(newCollectionIntent)
        }

        categories.clear()
        readCollections()
    }

    private fun readCollections()
    {
        val db = FirebaseFirestore.getInstance()
        db.collection("Categories")
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    for (document in it.result!!) {

                        val result = (document.id).toString() +" Goal:"+ (document.data.getValue("Goal")).toString()
                        categories.add(result)
                    }
                    val listView = findViewById<ListView>(R.id.listCollections)
                    val arrayAdapter : ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, categories )
                    listView.adapter = arrayAdapter

                    listView.setOnItemClickListener { adapterView, view, i, l ->
                        Toast.makeText(this, "Category Selected "+categories[i], Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }


}