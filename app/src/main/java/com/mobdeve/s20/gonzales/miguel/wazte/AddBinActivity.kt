package com.mobdeve.s20.gonzales.miguel.wazte

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddBinActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onetimelocation) // Use your existing layout file

        val btnAttachImage = findViewById<ImageButton>(R.id.btnAttachImage)
        val attachmentText = findViewById<TextView>(R.id.attachmentText)

        // Handle the image selection
        btnAttachImage.setOnClickListener {
            openImagePicker()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data

            // You can update the ImageButton with the selected image
            findViewById<ImageView>(R.id.btnAttachImage).setImageURI(imageUri)
            Toast.makeText(this, "Image Attached Successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Image Attachment Canceled", Toast.LENGTH_SHORT).show()
        }
    }
}
