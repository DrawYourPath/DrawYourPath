package com.epfl.drawyourpath.database

import android.widget.EditText

abstract class Database {
    abstract fun get(email: EditText, phoneNumber: EditText)
    abstract fun set(email: EditText, phoneNumber: EditText)
}