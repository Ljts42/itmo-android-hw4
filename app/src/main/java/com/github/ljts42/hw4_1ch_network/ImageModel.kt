package com.github.ljts42.hw4_1ch_network

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel

class ImageModel : ViewModel() {
    private var image: Bitmap? = null

    fun getImage(): Bitmap? = image
    fun setImage(img: Bitmap) {
        image = img
    }
}