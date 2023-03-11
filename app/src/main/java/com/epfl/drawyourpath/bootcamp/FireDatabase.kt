package com.epfl.drawyourpath.bootcamp

import android.widget.EditText
import com.epfl.drawyourpath.database.Database
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.concurrent.CompletableFuture

class FireDatabase : Database() {
    val db: DatabaseReference = Firebase.database.reference
    override fun get(email: EditText, phoneNumber: EditText) {
        val future = CompletableFuture<String>()

        db.child(phoneNumber.text.toString()).get().addOnSuccessListener {
            if (it.value == null) future.completeExceptionally(NoSuchFieldException())
            else future.complete(it.value as String)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        future.thenAccept {
            email.setText(it)
        }
    }

    override fun set(email: EditText, phoneNumber: EditText) {
        db.child(phoneNumber.text.toString()).setValue(email.text.toString())
    }
}