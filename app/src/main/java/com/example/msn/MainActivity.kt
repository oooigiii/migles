package com.example.msn

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.msn.adapter.ContactsAdapter
import com.example.msn.firebase.FirebaseManager
import android.content.Intent

class MainActivity : AppCompatActivity() {
    private lateinit var contactsList: RecyclerView
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var contactsAdapter: ContactsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseManager = FirebaseManager()
        setupContactsList()
        observeContacts()
    }

    private fun setupContactsList() {
        contactsList = findViewById(R.id.contactsList)
        contactsAdapter = ContactsAdapter { user ->
            startChat(user.uid)
        }
        contactsList.layoutManager = LinearLayoutManager(this)
        contactsList.adapter = contactsAdapter
    }

    private fun observeContacts() {
        firebaseManager.observeContacts { contacts ->
            contactsAdapter.updateContacts(contacts)
        }
    }

    private fun startChat(userId: String) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("receiverId", userId)
        }
        startActivity(intent)
    }
}
