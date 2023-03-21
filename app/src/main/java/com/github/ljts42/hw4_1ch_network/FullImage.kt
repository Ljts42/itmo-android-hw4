package com.github.ljts42.hw4_1ch_network

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.util.concurrent.Executors

class FullImage : AppCompatActivity() {

    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var imageModel: ImageModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_image)
        imageModel = ViewModelProvider(this)[ImageModel::class.java]

        val url = intent.getStringExtra("imageUrl")
        val imageView: ImageView = findViewById(R.id.image_big)
        if (imageModel.getImage() == null) {
            imageView.setImageResource(R.drawable.ic_broken_image)

            executor.execute {
                val image = MyInternetUtility.loadImage("img/$url")
                runOnUiThread {
                    if (image != null) {
                        imageModel.setImage(image)
                        imageView.setImageBitmap(image)
                    }
                }
            }
        } else {
            imageView.setImageBitmap(imageModel.getImage())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}