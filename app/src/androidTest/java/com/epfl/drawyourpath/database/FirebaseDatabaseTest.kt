package com.epfl.drawyourpath.database

import com.google.firebase.database.DatabaseReference

class FirebaseDatabaseTest {

    private fun createDatabase(database: DatabaseReference): FirebaseDatabase {
        return FirebaseDatabase(database)
    }

    // When can use Mockito to directly mock the Firebase Database without the mock.
}
