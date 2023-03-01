package com.github.drawyourpath.bootcamp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.concurrent.CompletableFuture

class FirebaseBootcampActivity : AppCompatActivity() {
    val db: DatabaseReference = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_bootcamp)

        val mailInput: EditText = findViewById(R.id.textMailFirebase)
        val phoneInput: EditText = findViewById(R.id.textPhoneFirebase)

        val setButton : Button = findViewById(R.id.setButtonFirebase)
        setButton.setOnClickListener{set(mailInput, phoneInput)}

        val getButton : Button = findViewById(R.id.getButtonFirebase)
        getButton.setOnClickListener{get(mailInput, phoneInput)}
    }

    private fun set(email: EditText, phoneNumber: EditText) {

        db.child(phoneNumber.text.toString()).setValue(email.text.toString())
    }

    private fun get(email: EditText, phoneNumber: EditText) {
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
}