package com.example.opsc7311_task2_group2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_new_collection.*
import java.util.HashMap

class NewCollection : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_collection)

        val saveButton = findViewById<Button>(R.id.btnSave)
        val backButton = findViewById<Button>(R.id.btnBack)
        val addItemButton = findViewById<Button>(R.id.btnAddItem)

        backButton.setOnClickListener{
            val backIntent = Intent(this, ViewCollections::class.java)
            startActivity(backIntent)
        }

        saveButton.setOnClickListener {

            val db = FirebaseFirestore.getInstance()
            val categoryName = findViewById<EditText>(R.id.edtNewCollectionName).text.toString()
            val categoryGoal = findViewById<EditText>(R.id.edtGoal).text.toString()

            val data: MutableMap<String, Any> = HashMap()
            data["Goal"] = categoryGoal

            db.collection("Categories").document(categoryName).set(data)

        }

        addItemButton.setOnClickListener{
            val addItemIntent = Intent(this, AddNewItem::class.java)
            startActivity(addItemIntent)
        }

    }
}