package com.example.opsc7311_task2_group2

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.DateTime
import com.ramijemli.percentagechartview.PercentageChartView
import kotlinx.android.synthetic.main.activity_view_items.*
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.HashMap
import kotlin.math.roundToInt

class ViewItems : AppCompatActivity()
{
    private val CHANNEL_ID = "channel_id_important_01"
    private val GROUP_KEY_GOALS = "android.group.goals"
    var notificationId = 101

    private val items = arrayListOf<String>()
    private var mAuth: FirebaseAuth? = null
    private val categoryItems = arrayListOf<String>()
    var currentGoal : String = ""
    var currentGoalNum : Float = 0f
    var totalItemNum : Float = 0f
    var userID : String = ""

    var totalItems : Float = 0f
    var valueIsImportant : String = ""

    lateinit var switchIsImportant : Switch
    var passedCategory : String = ""

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_items)
        createNotificationChannel()
        //Toast.makeText(this, "Category Selected "+ passedValue.getStringExtra("Category"), Toast.LENGTH_LONG).show()
        var passedValue = intent

        passedCategory = passedValue.getStringExtra("Category").toString()
        var passedID = passedValue.getStringExtra("user_id").toString()
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth!!.currentUser?.uid.toString()
        notificationId = 105


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
        //Toast.makeText(this, "Goal = ", Toast.LENGTH_LONG).show()

        val graphButton = findViewById<Button>(R.id.btnGraph)
        graphButton.setOnClickListener{

            val Intent = Intent(this, ItemsInCategoryGraph::class.java)
            Intent.putExtra("user_id",passedID)
            Intent.putExtra("Category", passedCategory)
            startActivity(Intent)
        }




        readCollections()
        setGraph()
        switchIsImportant = findViewById<Switch>(R.id.switchEditIsImportant)
        val db = FirebaseFirestore.getInstance()

        switchIsImportant.setOnCheckedChangeListener { _, isChecked ->
                val data: MutableMap<String, Any> = HashMap()
                data["IsImportant"] = isChecked.toString()
                db.collection("Users").document(userID).collection("Categories")
                    .document(passedCategory).update(data)
        }



    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotificationChannel(channelID :String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun readCollections()
    {

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
        switchIsImportant = findViewById<Switch>(R.id.switchEditIsImportant)


        val db = FirebaseFirestore.getInstance()
        var passedValue = intent
        var passedCategory = passedValue.getStringExtra("Category").toString()
        db.collection("Users").document(userID).collection("Categories")
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    for (document in it.result!!) {
                        if(document.id == passedCategory){
                            currentGoal = document.data.getValue("Goal").toString()
                            currentGoalNum = currentGoal.toFloat()

                            valueIsImportant = document.data.getValue("IsImportant").toString()

                            if (valueIsImportant == "true"){
                                switchIsImportant.isChecked = true
                            }

                            loadNotification(passedCategory)
                        }

                    }
                    for (items in categoryItems){
                        totalItems += items.toFloat()
                    }
                    totalItemNum = totalItems
                    var graph = findViewById<PercentageChartView>(R.id.percentageGraph)
                    var newPercent = totalItems.toFloat()/currentGoal.toFloat()*100
                    if (newPercent > 100){
                        newPercent = 100f
                    }
                    graph.setProgress(newPercent,true)
                }

            }


    }

    private fun loadNotification(passedCategory:String){

        val categoryID: CharArray = toCharacterArray(passedCategory)
        for(char in categoryID){
            notificationId += char.toInt()
        }

        val intent = Intent(this,LoginScreen::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this,0,intent,0)
        val bitmap:Bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.clipboard)
        val bitmapLargeIcon:Bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.hazmatguyclose)

        if (valueIsImportant == "true"){
            var builder = NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.hazmatguyclose)
                .setContentTitle("$passedCategory!")
                .setContentText("Don't forget to stock up!")
                .setLargeIcon(bitmapLargeIcon)
                .setStyle((NotificationCompat.BigPictureStyle().bigPicture(bitmapLargeIcon)))
                .setContentIntent(pendingIntent)
                .setGroup(GROUP_KEY_GOALS)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(this)){
                notify(notificationId,builder.build())
            }
        }
    }

    fun toCharacterArray(str: String): CharArray {
        return str.toCharArray()
    }

}