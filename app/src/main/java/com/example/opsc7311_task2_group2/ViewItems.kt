package com.example.opsc7311_task2_group2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ramijemli.percentagechartview.PercentageChartView
import kotlinx.android.synthetic.main.activity_view_items.*

class ViewItems : AppCompatActivity()
{
    private val items = arrayListOf<String>()
    private var mAuth: FirebaseAuth? = null
    private val categoryItems = arrayListOf<String>()
    var currentGoal : String = ""
    var userID : String = ""


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_items)

        //Toast.makeText(this, "Category Selected "+ passedValue.getStringExtra("Category"), Toast.LENGTH_LONG).show()
        var passedValue = intent

        var passedCategory = passedValue.getStringExtra("Category").toString()
        var passedID = passedValue.getStringExtra("user_id").toString()
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth!!.currentUser?.uid.toString()



        val addNewItemButton = findViewById<Button>(R.id.btnAddItemView)
        addNewItemButton.setOnClickListener{
            val newIntent = Intent(this, AddNewItem::class.java)
            newIntent.putExtra("user_id",passedID)
            newIntent.putExtra("Category", passedCategory)
            startActivity(newIntent)
        }

        val backToCollections = findViewById<Button>(R.id.btnBackToCollections)
        backToCollections.setOnClickListener{
            val newIntent = Intent(this, ViewCollections::class.java)
            //newIntent.putExtra("Category", passedCategory)
            startActivity(newIntent)
        }

        categoryItems.clear()
        Toast.makeText(this, "Goal = ", Toast.LENGTH_LONG).show()
        readCollections()
        setGraph()
    }

    private fun readCollections()
    {
        var passedCategory = intent.getStringExtra("Category")
        val db = FirebaseFirestore.getInstance()

        var passedID = intent.getStringExtra("user_id")

        db.collection("Users").document(userID).collection("Categories").document(passedCategory.toString()).collection("Items")
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    for (document in it.result!!) {

                        val itemName = document.data.getValue("itemName").toString()
                        categoryItems.add(document.data.getValue("numItems").toString())
                        items.add(itemName)
                    }
                    val listView = findViewById<ListView>(R.id.listItems)
                    val arrayAdapter : ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_list_item_1, items )

                    listView.adapter = arrayAdapter
                    //Toast.makeText(this, "Category Selected "+items[0], Toast.LENGTH_SHORT).show()
                    listView.setOnItemClickListener { adapterView, view, i, l ->

                        //Toast.makeText(this, "Category Selected ", Toast.LENGTH_LONG).show()
                        val newIntent = Intent(this, ViewItemDetails::class.java)
                        newIntent.putExtra("Item", items[i])
                        newIntent.putExtra("Category", passedCategory)
                        startActivity(newIntent)
                    }

                }
            }
    }

    private fun setGraph(){

        val db = FirebaseFirestore.getInstance()
        var passedValue = intent
        var passedCategory = passedValue.getStringExtra("Category").toString()
        var totalItems : Float = 0f
        db.collection("Users").document(userID).collection("Categories")
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    for (document in it.result!!) {
                        if(document.id == passedCategory){
                            currentGoal = document.data.getValue("Goal").toString()


                        }

                    }
                    for (items in categoryItems){
                        totalItems += items.toFloat()
                    }
                    var graph = findViewById<PercentageChartView>(R.id.percentageGraph)
                    var newPercent = totalItems.toFloat()/currentGoal.toFloat()*100
                    graph.setProgress(newPercent,true)
                }

            }

    }
}