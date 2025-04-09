package com.example.msn

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.example.msn.model.Message
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class ChatActivity : AppCompatActivity() {
    private lateinit var messagesList: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        messagesList = findViewById(R.id.messagesList)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)

        messagesList.layoutManager = LinearLayoutManager(this)

        sendButton.setOnClickListener {
            val message = messageInput.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message)
                messageInput.setText("")
            }
        }

        setupMessageListener()
    }

    private fun sendMessage(content: String) {
        val currentUser = auth.currentUser ?: return
        val chatId = intent.getStringExtra("chatId") ?: return
        val receiverId = intent.getStringExtra("receiverId") ?: return

        val message = Message(
            senderId = currentUser.uid,
            receiverId = receiverId,
            content = content
        )

        database.reference.child("chats")
            .child(chatId)
            .push()
            .setValue(message)
    }

    private fun setupMessageListener() {
        val chatId = intent.getStringExtra("chatId") ?: return
        
        database.reference.child("chats").child(chatId)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(Message::class.java)
                    message?.let {
                        // Adicionar mensagem ao RecyclerView
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
    }
}
