package com.epfl.drawyourpath

import android.app.Application
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DrawYourPathApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Firebase.database.setPersistenceEnabled(true)
    }
}
