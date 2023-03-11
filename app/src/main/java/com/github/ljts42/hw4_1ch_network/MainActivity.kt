package com.github.ljts42.hw4_1ch_network

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ljts42.hw4_1ch_network.databinding.ActivityMainBinding
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var messagesList: MutableList<Message> = mutableListOf()
    private val executor = Executors.newSingleThreadExecutor()
    private var lastMessageId = 0
    private var isLoading = false
    private val numberToLoad = 10
    private val username = "Test"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initRecyclerView()
        binding.btnSendMsg.setOnClickListener {
            executor.execute {
                MyInternetUtility.sendMessage(
                    Message(
                        from = username, data = binding.inputField.text.toString()
                    )
                )
            }
            binding.inputField.setText("")
        }
    }

    private fun initRecyclerView() {
        val viewManager = LinearLayoutManager(this)
        binding.chatRecycleView.apply {
            layoutManager = viewManager
            adapter = MessageAdapter(messagesList, onClickImage = {
                val intent = Intent(context, FullImage::class.java)
                intent.putExtra("imageUrl", it.data)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            })
        }
        getMessages(5000, 20)

        binding.chatRecycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = binding.chatRecycleView.layoutManager as LinearLayoutManager
                if (layoutManager.findLastVisibleItemPosition() == messagesList.size - 1) {
                    getMessages(lastMessageId, numberToLoad)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = binding.chatRecycleView.layoutManager as LinearLayoutManager
                if (newState == RecyclerView.SCROLL_STATE_IDLE && layoutManager.findLastVisibleItemPosition() == messagesList.size - 1) {
                    getMessages(lastMessageId, numberToLoad)
                }
            }
        })
    }

    private fun getMessages(start: Int, count: Int) {
        if (isLoading) return
        isLoading = true
        executor.execute {
            val newElementsList = MyInternetUtility.getMessages(start, count)
            runOnUiThread {
                if (newElementsList.isNotEmpty()) {
                    lastMessageId = newElementsList.last().id.toInt()
                    messagesList.addAll(newElementsList)
                    binding.chatRecycleView.adapter?.notifyItemRangeInserted(
                        start, newElementsList.size
                    )
                }
                isLoading = false
            }
        }
    }
}