package com.github.ljts42.hw4_1ch_network

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.Executors

class FullImage : AppCompatActivity() {

    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_image)

        val url = intent.getStringExtra("imageUrl")
        val imageView: ImageView = findViewById(R.id.image_big)
        imageView.setImageResource(R.drawable.ic_broken_image)

        executor.execute {
            val image = MyInternetUtility.loadImage("img/$url")
            runOnUiThread {
                if (image != null) imageView.setImageBitmap(image)
            }
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