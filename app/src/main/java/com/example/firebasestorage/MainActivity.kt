package com.example.firebasestorage

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.net.CacheRequest

val REQUEST_CODE = 0
class MainActivity : AppCompatActivity() {
    var curFile: Uri? = null
    var imageRef = Firebase.storage.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val image = findViewById<ImageView>(R.id.ivImage)
        val uploadButton = findViewById<Button>(R.id.uploadButton)
        val retrieveButton = findViewById<Button>(R.id.retrieveButton)
        image.setOnClickListener{
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, REQUEST_CODE)
            }
        }
        uploadButton.setOnClickListener {
            uploadToFirebase("myImage")
        }
        retrieveButton.setOnClickListener {
            dowloadImage("myImage")
        }
    }

    private fun dowloadImage(fileName: String){
        val maxDowloadSize = 5L * 1024 * 1024
        val bytes = imageRef.child("images/$fileName").getBytes(maxDowloadSize)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    val bmp = BitmapFactory.decodeByteArray(task.result, 0, task.result!!.size)
                    findViewById<ImageView>(R.id.ivImage).setImageBitmap(bmp)
                }
            }.addOnFailureListener{
                Toast.makeText(this@MainActivity,it.message,Toast.LENGTH_LONG).show()
            }
    }
     private fun uploadToFirebase(fileName: String){
       curFile?.let {
           imageRef.child("images/$fileName").putFile(it)
               .addOnCompleteListener{ task ->
                   if(task.isSuccessful){
                       Toast.makeText(this@MainActivity, "Uploaded",Toast.LENGTH_LONG).show()
                   }
               }
               .addOnFailureListener{
                   Toast.makeText(this@MainActivity, it.message,Toast.LENGTH_LONG).show()
               }
       }
       }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            data?.data?.let {
                curFile = it
                findViewById<ImageView>(R.id.ivImage).setImageURI(it)
            }
        }
    }

    }

