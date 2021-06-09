package com.example.opsc7311_task2_group2

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_new_item.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


private const val FILE_NAME = "photo.jpg"
private const val REQUEST_CODE = 42
private lateinit var photoFile: File
private lateinit var ImageUri : Uri
private var userID: String? = ""
private var imageDownloadURL : String = ""

class AddNewItem : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_item)



        findViewById<Button>(R.id.btnAddItemImage).setOnClickListener{
            if(askForPermissions()){
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                photoFile = getPhotoFile(FILE_NAME)
                val fileProvider = FileProvider.getUriForFile(this, "com.example.opsc7311_task2_group2.fileprovider",
                    photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

                startActivityForResult(takePictureIntent, REQUEST_CODE)

                if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                    //startActivityForResult(takePictureIntent, REQUEST_CODE)
                }else{
                    Toast.makeText(this,"Unable to open camera", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val itemImageViewer = findViewById<ImageView>(R.id.imgItemImage)

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            //val takenImage = data?.extras?.get("data") as Bitmap
            val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
            if (takenImage != null){
                itemImageViewer?.setImageBitmap(takenImage)
                ImageUri = getImageUri(this,takenImage)
                uploadImage()
            }
        }else{
            Log.e("Error",": result code not okay")
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun uploadImage(){
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading file...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageReference = FirebaseStorage.getInstance().getReference("images/$userID/$fileName")

        storageReference.putFile(ImageUri).addOnSuccessListener {
            Toast.makeText(this,"Successfully Uploaded",Toast.LENGTH_SHORT).show()
            if (progressDialog.isShowing) progressDialog.dismiss()
            storageReference.downloadUrl.addOnSuccessListener { imageDownloadRef ->
                imageDownloadURL = imageDownloadRef.toString()
                Toast.makeText(this,"Uploaded to: $imageDownloadURL",Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            if (progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(this,"Failed Uploading",Toast.LENGTH_SHORT).show()

        }

    }

    private fun getImageUri(context: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, inImage, "Title", null)

        return Uri.parse(path)
    }

    private fun getPhotoFile(fileName:String):File{
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName,".jpg",storageDirectory)
    }

    fun isPermissionsAllowed(): Boolean {
        return if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            false
        } else true
    }

    fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this as Activity,Manifest.permission.CAMERA)) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(this as Activity,arrayOf(Manifest.permission.CAMERA),REQUEST_CODE)
                ActivityCompat.requestPermissions(this as Activity,arrayOf(Manifest.permission.CAMERA),REQUEST_CODE)
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<String>,grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission is granted, you can perform your operation here
                } else {
                    // permission is denied, you can ask for permission again, if you want
                    //  askForPermissions()
                }
                return
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton("App Settings",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    // send to app settings if permission is denied permanently
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", getPackageName(), null)
                    intent.data = uri
                    startActivity(intent)
                })
            .setNegativeButton("Cancel",null)
            .show()
    }
}