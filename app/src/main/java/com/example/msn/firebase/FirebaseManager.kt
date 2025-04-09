package com.example.msn.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.example.msn.model.User
import com.example.msn.model.Message

class FirebaseManager {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    
    fun getCurrentUser() = auth.currentUser

    fun signIn(email: String, password: String, callback: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    fun createUser(email: String, password: String, displayName: String, callback: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = User(
                        uid = task.result?.user?.uid ?: "",
                        email = email,
                        displayName = displayName
                    )
                    saveUserToDatabase(user) { success ->
                        callback(success)
                    }
                } else {
                    callback(false)
                }
            }
    }

    private fun saveUserToDatabase(user: User, callback: (Boolean) -> Unit) {
        database.reference.child("users")
            .child(user.uid)
            .setValue(user)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    fun observeMessages(chatId: String, onNewMessage: (Message) -> Unit) {
        database.reference.child("chats")
            .child(chatId)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(Message::class.java)
                    message?.let { onNewMessage(it) }
                }
                
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun observeContacts(onContactsUpdate: (List<User>) -> Unit) {
        database.reference.child("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val contacts = mutableListOf<User>()
                    for (userSnapshot in snapshot.children) {
                        userSnapshot.getValue(User::class.java)?.let {
                            if (it.uid != getCurrentUser()?.uid) {
                                contacts.add(it)
                            }
                        }
                    }
                    onContactsUpdate(contacts)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}
