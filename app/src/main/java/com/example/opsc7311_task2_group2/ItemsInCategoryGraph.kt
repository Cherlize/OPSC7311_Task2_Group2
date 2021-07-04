package com.example.opsc7311_task2_group2

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ItemsInCategoryGraph : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var userID : String = ""
    private val collectionsList = arrayListOf<PieEntry>()
    private var items = arrayListOf<String>()
    private var itemQuantity = arrayListOf<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items_in_category_graph)

        var passedCategory = intent.getStringExtra("Category").toString()

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth!!.currentUser?.uid.toString()

        val db = FirebaseFirestore.getInstance()

        items.clear()
        itemQuantity.clear()

        db.collection("Users").document(userID).collection("Categories").document(passedCategory)
            .collection("Items")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    for (document in it.result!!) {
                        items.add(document.id)
                        itemQuantity.add(document.data.getValue("numItems").toString().toInt())
                    }
                }
                PieChartMaker()
            }

        val  backButton = findViewById<Button>(R.id.btnBackGraph)
        backButton.setOnClickListener{

            val Intent = Intent(this, ViewItems::class.java)
            Intent.putExtra("Category", passedCategory)
            Intent.putExtra("user_id",userID)
            startActivity(Intent)
        }
    }


    fun PieChartMaker()
    {
        for (i in items.indices)
        {
            collectionsList.add(PieEntry(itemQuantity[i].toFloat(),items[i]))
        }

        var pieChart : PieChart = findViewById<PieChart>(R.id.PieChartCollections)

        var pieDataSet: PieDataSet = PieDataSet(collectionsList,"Collections")
        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS,200)
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = 16f


        var pieData: PieData = PieData(pieDataSet)

        pieChart.setUsePercentValues(true)
        pieChart.transparentCircleRadius = 30f
        pieChart.setExtraOffsets(5f,10f,5f,5f)
        pieChart.legend.textColor = Color.WHITE
        pieChart.legend.isEnabled = false

        pieChart.holeRadius = 25f
        pieChart.setHoleColor(Color.LTGRAY)

        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.centerText = "Collections"
        pieChart.animate()
        pieChart.invalidate()
    }

}