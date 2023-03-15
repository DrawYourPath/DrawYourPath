package com.epfl.drawyourpath.bootcamp

import android.widget.EditText

class MockDataBase : Database() {
    private var dataMap: HashMap<String, String> = HashMap()
    override fun get(email: EditText, phoneNumber: EditText) {
        val number: String? = dataMap.get(email.text.toString())
        phoneNumber.setText(number)
    }

    override fun set(email: EditText, phoneNumber: EditText) {
        dataMap.put(email.text.toString(), phoneNumber.text.toString())
    }
}