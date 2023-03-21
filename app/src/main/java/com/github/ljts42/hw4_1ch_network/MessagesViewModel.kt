package com.github.ljts42.hw4_1ch_network

import android.graphics.Bitmap
import android.util.LruCache
import androidx.lifecycle.ViewModel

class MessagesViewModel : ViewModel() {
    private val cacheSize = 4 * 1024 * 1024
    private val messagesList: MutableList<Message> = mutableListOf()
    private val cache = LruCache<String, Bitmap>(cacheSize)
    private var lastMessageId = 5000

    fun getImage(url: String): Bitmap? {
        var image = cache.get(url)
        if (image == null) {
            image = MyInternetUtility.loadImage(url)
            if (image != null) {
                cache.put(url, image)
            }
        }
        return image
    }

    fun getMessages(): MutableList<Message> {
        return messagesList
    }

    fun addMessages(messages: List<Message>) {
        messagesList.addAll(messages)
    }

    fun getLastId() = lastMessageId

    fun updateLastId(newLast: Int) {
        lastMessageId = newLast
    }
}