package com.example.opsc7311_task2_group2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ViewCollections : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_collections)

        val addButton = findViewById<Button>(R.id.btnAdd)

        addButton.setOnClickListener{
            val newCollectionIntent = Intent(this, NewCollection::class.java)
            startActivity(newCollectionIntent)
        }
    }
}