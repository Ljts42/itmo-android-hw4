package com.github.ljts42.hw4_1ch_network

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ljts42.hw4_1ch_network.databinding.ActivityMainBinding
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var messagesViewModel: MessagesViewModel
    private val executor = Executors.newSingleThreadExecutor()
    private var isLoading = false
    private val username = "Test"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        messagesViewModel = ViewModelProvider(this)[MessagesViewModel::class.java]

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
            adapter = MessageAdapter(
                messagesViewModel,
                onClickImage = {
                    val intent = Intent(context, FullImage::class.java)
                    intent.putExtra("imageUrl", it.data)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                })
        }
        getMessages()

        binding.chatRecycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = binding.chatRecycleView.layoutManager as LinearLayoutManager
                if (layoutManager.findLastVisibleItemPosition() == messagesViewModel.getMessages().size - 1) {
                    getMessages()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = binding.chatRecycleView.layoutManager as LinearLayoutManager
                if (newState == RecyclerView.SCROLL_STATE_IDLE && layoutManager.findLastVisibleItemPosition() == messagesViewModel.getMessages().size - 1) {
                    getMessages()
                }
            }
        })
    }

    private fun getMessages(count: Int = 10) {
        if (isLoading) return
        isLoading = true
        executor.execute {
            val newElementsList =
                MyInternetUtility.getMessages(messagesViewModel.getLastId(), count)
            runOnUiThread {
                if (newElementsList.isNotEmpty()) {
                    val start = messagesViewModel.getMessages().size
                    messagesViewModel.addMessages(newElementsList)
                    binding.chatRecycleView.adapter?.notifyItemRangeInserted(
                        start, newElementsList.size
                    )
                    messagesViewModel.updateLastId(newElementsList.last().id.toInt())
                }
                isLoading = false
            }
        }
    }
}