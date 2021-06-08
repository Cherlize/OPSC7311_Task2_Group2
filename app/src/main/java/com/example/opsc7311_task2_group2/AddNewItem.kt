package com.example.opsc7311_task2_group2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AddNewItem : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_item)

        val backButton = findViewById<Button>(R.id.btnBackItem)
        backButton.setOnClickListener{
            val intent = Intent(this, NewCollection::class.java)
            startActivity(intent)
        }
    }
}