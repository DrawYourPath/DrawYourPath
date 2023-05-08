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

    companion object {
        val nullSnapshot = mockNullSnapshot()

        // Can't use mockSnapshot() directly to create the initial null snapshot because of recursion.
        private fun mockNullSnapshot(): DataSnapshot {
            val snap = mock(DataSnapshot::class.java)
            `when`(snap.value).thenReturn(null)
            `when`(snap.children).thenReturn(emptyList())
            `when`(snap.child(any())).thenReturn(snap)
            return snap
        }

        fun <T> mockSnapshot(value: T?): DataSnapshot {
            val snap = mock(DataSnapshot::class.java)
            `when`(snap.value).thenReturn(value)
            `when`(snap.children).thenReturn(emptyList())
            `when`(snap.child(any())).thenReturn(nullSnapshot)
            return snap
        }

        fun <T> mockSnapshotPath(snapshot: DataSnapshot, path: String, value: T): DataSnapshot {
            val child = mockSnapshot(value)
            `when`(snapshot.child(path)).thenReturn(child)
            return child
        }

        fun mockParent(children: Map<String, DataSnapshot>): DataSnapshot {
            val parent = mock(DataSnapshot::class.java)
            children.forEach {
                `when`(parent.child(it.key)).thenReturn(it.value)
            }
            `when`(parent.children).thenReturn(children.map { it.value })
            `when`(parent.value).thenReturn(nullSnapshot)
            return parent
        }

        fun <T> mockTask(value: T?, exception: Exception? = null): Task<T> {
            return object : Task<T>() {
                override fun addOnFailureListener(p0: OnFailureListener): Task<T> {
                    if (exception != null) {
                        p0.onFailure(exception)
                    }
                    return this
                }

                override fun addOnSuccessListener(p0: OnSuccessListener<in T>): Task<T> {
                    if (exception == null) {
                        p0.onSuccess(value)
                    }
                    return this
                }

                override fun addOnFailureListener(p0: Activity, p1: OnFailureListener): Task<T> =
                    addOnFailureListener(p1)

                override fun addOnFailureListener(p0: Executor, p1: OnFailureListener): Task<T> =
                    addOnFailureListener(p1)

                override fun getException(): Exception? = exception

                override fun getResult(): T = value!!

                override fun <X : Throwable?> getResult(p0: Class<X>): T = value!!

                override fun isCanceled(): Boolean = false

                override fun isComplete(): Boolean = true

                override fun isSuccessful(): Boolean = exception == null

                override fun addOnSuccessListener(
                    p0: Executor,
                    p1: OnSuccessListener<in T>,
                ): Task<T> = addOnSuccessListener(p1)

                override fun addOnSuccessListener(
                    p0: Activity,
                    p1: OnSuccessListener<in T>,
                ): Task<T> = addOnSuccessListener(p1)
            }
        }
    }

    private fun mockEmptyDatabase(): DatabaseReference {
        val database = mock(DatabaseReference::class.java)
        `when`(database.child(any())).thenReturn(database)
        `when`(database.get()).thenReturn(mockTask(nullSnapshot, null))
        `when`(database.updateChildren(any())).thenReturn(mockTask(null, null))
        `when`(database.removeValue()).thenReturn(mockTask(null, null))
        return database
    }

    private fun mockDatabaseWithUser(userData: UserData, databaseException: Exception? = null): DatabaseReference {
        val database = mock(DatabaseReference::class.java)
        val usersRoot = mock(DatabaseReference::class.java)
        val userRoot = mock(DatabaseReference::class.java)

        val userProfile = mock(DatabaseReference::class.java)

        // Mock username entry
        val username = mock(DatabaseReference::class.java)
        val usernameSnapshot = mockSnapshot(userData.username)

        // Mock userdata entry
        val userDataSnapshot = mockParent(
            mapOf(
                FirebaseKeys.PROFILE to mockParent(
                    mapOf(
                        FirebaseKeys.USERNAME to mockSnapshot(userData.username),
                        FirebaseKeys.EMAIL to mockSnapshot(userData.email),
                        FirebaseKeys.FIRSTNAME to mockSnapshot(userData.firstname),
                        FirebaseKeys.BIRTHDATE to mockSnapshot(userData.birthDate),
                        FirebaseKeys.SURNAME to mockSnapshot(userData.surname),
                        FirebaseKeys.BIRTHDATE to mockSnapshot(userData.birthDate),
                        FirebaseKeys.PICTURE to mockSnapshot(userData.picture),
                        FirebaseKeys.FRIENDS to mockSnapshot(null),
                    ),
                ),
                FirebaseKeys.GOALS to mockSnapshot(null),
                FirebaseKeys.DAILY_GOALS to mockSnapshot(null),
                FirebaseKeys.RUN_HISTORY to mockSnapshot(null),
                FirebaseKeys.USER_CHATS to mockSnapshot(null),
            ),
        )

        // TODO: mock more props for other tests
        `when`(username.get()).thenReturn(mockTask(usernameSnapshot, databaseException))
        `when`(userProfile.child(FirebaseKeys.USERNAME)).thenReturn(username)
        `when`(userRoot.get()).thenReturn(mockTask(userDataSnapshot, null))
        `when`(userRoot.child(FirebaseKeys.PROFILE)).thenReturn(userProfile)
        `when`(userRoot.updateChildren(any())).thenReturn(mockTask(null, databaseException))
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
        val databaseRef = mockEmptyDatabase()

        val res = FirebaseDatabase(databaseRef).isUserInDatabase("uid").get()

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

    @Test
    fun createUserCreatesUser() {
        val userData = UserData(
            userId = "uid",
            birthDate = 10,
            username = "uname",
            surname = "surname",
        )

        val dbRef = mockDatabaseWithUser(userData)

        val db = FirebaseDatabase(dbRef)
        db.createUser(userData.userId!!, userData).get()

        val resData = db.getUsername(userData.userId!!).get()
        assertThat(resData, `is`(userData.username))
    }

    @Test
    fun createUserWithFailingDatabaseThrows() {
        val userData = UserData(
            userId = "uid",
            birthDate = 10,
            username = "uname",
            surname = "surname",
        )

        val dbRef = mockDatabaseWithUser(userData, Exception("error"))

        assertThrows(Exception::class.java) {
            FirebaseDatabase(dbRef).createUser(userData.userId!!, userData).get()
        }
    }

    @Test
    fun setUserDataSetsUserData() {
        val userData = UserData(
            userId = "uid",
            birthDate = 10,
            username = "uname",
            surname = "surname",
        )

        val dbRef = mockDatabaseWithUser(userData)

        val db = FirebaseDatabase(dbRef)

        db.setUserData(userData.userId!!, userData).get()

        val data = db.getUserData(userData.userId!!).get()

        assertThat(data.birthDate, `is`(userData.birthDate))
        assertThat(data.username, `is`(userData.username))
    }
}
