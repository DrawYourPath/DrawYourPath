package com.epfl.drawyourpath.auth

import android.app.Activity
import android.net.Uri
import androidx.core.os.bundleOf
import com.epfl.drawyourpath.authentication.FirebaseAuth
import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.authentication.USE_MOCK_AUTH
import com.epfl.drawyourpath.authentication.createAuth
import com.epfl.drawyourpath.database.FirebaseDatabaseTest
import com.google.firebase.auth.FirebaseUser
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*

class AuthTest {

    val testUser = mapOf(
        "displayName" to "Michel",
        "email" to "email",
        "phone" to "phone",
        "photoUri" to "https://foo.bar",
        "uid" to "uid",
        "anon" to false,
    )

    private fun <T : Map<String, Any?>> mockUser(userData: T, opException: Exception? = null): FirebaseUser {
        val user = mock(FirebaseUser::class.java)

        val uri = Uri.parse(userData["photoUri"] as String?)

        `when`(user.uid).thenReturn(userData["uid"] as String?)
        `when`(user.displayName).thenReturn(userData["displayName"] as String?)
        `when`(user.email).thenReturn(userData["email"] as String?)
        `when`(user.phoneNumber).thenReturn(userData["phone"] as String?)
        `when`(user.photoUrl).thenReturn(uri)
        `when`(user.isAnonymous).thenReturn(userData["anon"] as Boolean?)
        `when`(user.updatePassword(any())).thenReturn(FirebaseDatabaseTest().mockTask(null, opException))

        return user
    }

    @Test
    fun convertNullUserReturnsNull() {
        assertNull(FirebaseAuth.convertUser(null))
    }

    @Test
    fun convertUserReturnsExpectedData() {
        val mockUser = mockUser(testUser)

        val convUser = FirebaseAuth.convertUser(mockUser)

        assertNotNull(convUser)
        assertThat(convUser?.getDisplayName(), `is`(testUser["displayName"]))
        assertThat(convUser?.getEmail(), `is`(testUser["email"]))
        assertThat(convUser?.getUid(), `is`(testUser["uid"]))
        assertThat(convUser?.isAnonymous(), `is`(testUser["anon"]))
        assertThat(convUser?.getPhoneNumber(), `is`(testUser["phone"]))
        assertThat(convUser?.getPhotoUrl().toString(), `is`(testUser["photoUri"]))

        // Checks that update password completes without error
        convUser?.updatePassword("Foobar")?.get()
    }

    @Test
    fun convertUserWithFailedDatabaseThrowsWhileUpdatingPassword() {
        val mockUser = mockUser(testUser, Exception("Foobar"))

        val convUser = FirebaseAuth.convertUser(mockUser)

        assertNotNull(convUser)

        assertThrows(Throwable::class.java) {
            convUser?.updatePassword("Foobar")?.get()
        }
    }

    @Test
    fun emptyMockMethodsActAsExpected() {
        val auth = MockAuth()
        val mockActivity = mock(Activity::class.java)
        auth.onActivityResult(mockActivity, 0, 0, null)
        auth.onActivityCreate(mockActivity, null)
        auth.signOut()
        auth.clearListener()
    }

    @Test
    fun createAuthReturnsFirebaseAuthWhenTestNotSpecified() {
        assertTrue(createAuth(null) is FirebaseAuth)
    }

    @Test
    fun createAuthReturnsMockAuthWhenTestSpecified() {
        assertTrue(createAuth(bundleOf(USE_MOCK_AUTH to true)) is MockAuth)
    }
}
