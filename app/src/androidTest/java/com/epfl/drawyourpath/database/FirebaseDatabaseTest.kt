package com.epfl.drawyourpath.database

import android.app.Activity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThrows
import org.junit.Test
import org.mockito.Mockito.*
import java.util.concurrent.Executor
import kotlin.Exception

class FirebaseDatabaseTest {

    private fun createDatabase(database: DatabaseReference): FirebaseDatabase {
        return FirebaseDatabase(database)
    }

    private fun <T> mockSnapshot(value: T?): DataSnapshot {
        val snap = mock(DataSnapshot::class.java)
        `when`(snap.value).thenReturn(value)
        return snap
    }

    private fun mockTask(snapshot: DataSnapshot?, exception: Exception? = null): Task<DataSnapshot> {
        return object : Task<DataSnapshot>() {
            override fun addOnFailureListener(p0: OnFailureListener): Task<DataSnapshot> {
                if (exception != null) {
                    p0.onFailure(exception)
                }
                return this
            }

            override fun addOnSuccessListener(p0: OnSuccessListener<in DataSnapshot>): Task<DataSnapshot> {
                if (exception == null) {
                    p0.onSuccess(snapshot)
                }
                return this
            }

            override fun addOnFailureListener(p0: Activity, p1: OnFailureListener): Task<DataSnapshot> = addOnFailureListener(p1)

            override fun addOnFailureListener(p0: Executor, p1: OnFailureListener): Task<DataSnapshot> = addOnFailureListener(p1)

            override fun getException(): Exception? = exception

            override fun getResult(): DataSnapshot = snapshot!!

            override fun <X : Throwable?> getResult(p0: Class<X>): DataSnapshot = snapshot!!

            override fun isCanceled(): Boolean = false

            override fun isComplete(): Boolean = true

            override fun isSuccessful(): Boolean = exception == null

            override fun addOnSuccessListener(p0: Executor, p1: OnSuccessListener<in DataSnapshot>): Task<DataSnapshot> = addOnSuccessListener(p1)

            override fun addOnSuccessListener(p0: Activity, p1: OnSuccessListener<in DataSnapshot>): Task<DataSnapshot> = addOnSuccessListener(p1)
        }
    }

    private fun mockDatabaseWithUser(userData: UserData, databaseException: Exception? = null): DatabaseReference {
        val database = mock(DatabaseReference::class.java)
        val usersRoot = mock(DatabaseReference::class.java)
        val userRoot = mock(DatabaseReference::class.java)

        val userProfile = mock(DatabaseReference::class.java)

        // Mock username entry
        val username = mock(DatabaseReference::class.java)
        val usernameSnapshot = mockSnapshot(userData.username)

        // TODO: mock more props for other tests

        `when`(username.get()).thenReturn(mockTask(usernameSnapshot, databaseException))
        `when`(userProfile.child(FirebaseKeys.USERNAME)).thenReturn(username)
        `when`(userRoot.child(FirebaseKeys.PROFILE)).thenReturn(userProfile)
        `when`(usersRoot.child(userData.userId!!)).thenReturn(userRoot)
        `when`(database.child(FirebaseKeys.USERS_ROOT)).thenReturn(usersRoot)

        return database
    }

    // We can use Mockito to directly mock the Firebase Database without the mock.

    @Test
    fun isUserInDatabaseForExistingUserReturnsTrue() {
        val user = UserData(
            userId = "uid",
            username = "uname",
        )
        val databaseRef = mockDatabaseWithUser(user)

        val res = FirebaseDatabase(databaseRef).isUserInDatabase(user.userId!!).get()

        assertThat(res, `is`(true))
    }

    @Test
    fun isUserInDatabaseForNonExistingUserReturnsFalse() {
        val user = UserData(
            userId = "uid",
        )
        val databaseRef = mockDatabaseWithUser(user)

        val res = FirebaseDatabase(databaseRef).isUserInDatabase(user.userId!!).get()

        assertThat(res, `is`(false))
    }

    @Test
    fun isUserInDatabaseWithFailingDatabaseThrows() {
        val user = UserData(
            userId = "uid",
            username = "uname",
        )
        val databaseRef = mockDatabaseWithUser(user, Exception("Foobar"))

        assertThrows(Throwable::class.java) {
            FirebaseDatabase(databaseRef).isUserInDatabase(user.userId!!).get()
        }
    }

    @Test
    fun getUsernameReturnsUsername() {
        val user = UserData(
            userId = "uid",
            username = "uname",
        )
        val databaseRef = mockDatabaseWithUser(user)

        val res = FirebaseDatabase(databaseRef).getUsername(user.userId!!).get()

        assertThat(res, `is`(user.username))
    }

    @Test
    fun getUsernameThrowsForInvalidsUser() {
        val user = UserData(
            userId = "uid",
        )
        val databaseRef = mockDatabaseWithUser(user)

        assertThrows(Throwable::class.java) {
            FirebaseDatabase(databaseRef).getUsername(user.userId!!).get()
        }
    }

    @Test
    fun getUsernameThrowsWhenDatabaseFailed() {
        val user = UserData(
            userId = "uid",
            username = "uname",
        )
        val databaseRef = mockDatabaseWithUser(user, Exception("Foobar"))

        assertThrows(Throwable::class.java) {
            FirebaseDatabase(databaseRef).getUsername(user.userId!!).get()
        }
    }
}
