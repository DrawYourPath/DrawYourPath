package com.epfl.drawyourpath.bootcamp.userProfile

import com.epfl.drawyourpath.database.MockDataBase
import com.epfl.drawyourpath.userProfile.UserModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalDate

class UserModelTest {
    private val userId = "id1234"
    private val username = "albert"
    private val email = "hugo.hof@epfl.ch"
    private val firstname = "Hugo"
    private val surname = "Hof"
    private val dateOfBirth = LocalDate.of(2000,2, 20)
    private val distanceGoal = 10.0
    private val timeGoal = 60.0
    private val nbOfPaths = 5
    private val database = MockDataBase()

    /**
     * Create a UserModel with empty userId throw an error
     */
    @Test
    fun createUserWithEmptyUserId(){
        val exception = assertThrows(java.lang.Error::class.java) {
            val user = UserModel("", username,email,firstname,surname, dateOfBirth, distanceGoal,timeGoal, nbOfPaths, database)
        }
        assertEquals("The userId can't be empty !", exception.message)
    }

    /**
     * Create a UserModel with invalid userId (not present on the database) throw an error
     */
    @Test
    fun createUserWithInvalidUserId(){
        val exception = assertThrows(java.lang.Error::class.java) {
            val user = UserModel("incorrect", username,email,firstname,surname, dateOfBirth, distanceGoal,timeGoal, nbOfPaths, database)
        }
        assertEquals("The userId must be present on the database !", exception.message)
    }

    /**
     * Create a UserModel with empty username throw an error
     */
    @Test
    fun createUserWithEmptyUsername(){
        val exception = assertThrows(java.lang.Error::class.java) {
            val user = UserModel(userId, "",email,firstname,surname, dateOfBirth, distanceGoal,timeGoal, nbOfPaths, database)
        }
        assertEquals("The username can't be empty !", exception.message)
    }

    /**
     * Create a UserModel with invalid username (not associated to the correct userid in the database) throw an error
     */
    @Test
    fun createUserWithInvalidUsername(){
        val exception = assertThrows(java.lang.Error::class.java) {
            val user = UserModel(userId, "incorrect",email,firstname,surname, dateOfBirth, distanceGoal,timeGoal, nbOfPaths, database)
        }
        assertEquals("The username not correspond to the given userId !", exception.message)
    }

    /**
     * Create a UserModel with invalid email address throw an error
     */
    @Test
    fun createUserWithInvalidEmailAddress(){
        val exception = assertThrows(java.lang.Error::class.java) {
            val user = UserModel(userId, username,"@not_correct.",firstname,surname, dateOfBirth, distanceGoal,timeGoal, nbOfPaths, database)
        }
        assertEquals("The mail address is not in the correct format !", exception.message)
    }


    /**
     * Create a UserModel with invalid firstname throw an error
     */
    @Test
    fun createUserWithInvalidFirstname(){
        val exception = assertThrows(java.lang.Error::class.java) {
            val user = UserModel(userId, username,email,"558",surname, dateOfBirth, distanceGoal,timeGoal, nbOfPaths, database)
        }
        assertEquals("Incorrect firstname", exception.message)

        val exception2 = assertThrows(java.lang.Error::class.java) {
            val user = UserModel(userId, username,email,"",surname, dateOfBirth, distanceGoal,timeGoal, nbOfPaths, database)
        }
        assertEquals("Incorrect firstname", exception2.message)
    }

    /**
     * Create a UserModel with invalid surname throw an error
     */
    @Test
    fun createUserWithInvalidSurname(){
        val exception = assertThrows(java.lang.Error::class.java) {
            val user = UserModel(userId, username,email,firstname,"4445", dateOfBirth, distanceGoal,timeGoal, nbOfPaths, database)
        }
        assertEquals("Incorrect surname", exception.message)

        val exception2 = assertThrows(java.lang.Error::class.java) {
            val user = UserModel(userId, username,email,firstname,"", dateOfBirth, distanceGoal,timeGoal, nbOfPaths, database)
        }
        assertEquals("Incorrect surname", exception2.message)
    }

    /**
     * Create a UserModel with invalid date of birth throw an error
     */
    @Test
    fun createUserWithInvalidDate(){
        //under 10 years
        val exception = assertThrows(java.lang.Error::class.java) {
            val user = UserModel(userId, username,email,firstname,surname, LocalDate.now(), distanceGoal,timeGoal, nbOfPaths, database)
        }
        assertEquals("Incorrect date of birth !", exception.message)
        //over 100 years
        val exception2 = assertThrows(java.lang.Error::class.java) {
            val user = UserModel(userId, username,email,firstname,surname, LocalDate.of(1900,2,2), distanceGoal,timeGoal, nbOfPaths, database)
        }
        assertEquals("Incorrect date of birth !", exception2.message)
    }

    /**
     * Create a user model with invalid distance goal (equal to 0)
     */
    @Test
    fun createUserWithInvalidDistanceGoal(){
        val exception = assertThrows(java.lang.Error::class.java) {
            val user = UserModel(userId, username,email,firstname,surname, dateOfBirth, 0.0,timeGoal, nbOfPaths, database)
        }
        assertEquals("The distance goal can't be equal to 0.", exception.message)
    }

    /**
     * Create a user model with invalid activity time goal (equal to 0)
     */
    @Test
    fun createUserWithInvalidActivityTimeGoal(){
        val exception = assertThrows(java.lang.Error::class.java) {
            val user = UserModel(userId, username,email,firstname,surname, dateOfBirth, distanceGoal,0.0, nbOfPaths, database)
        }
        assertEquals("The activity time goal can't be equal to 0.", exception.message)
    }

    /**
     * Create a user model with invalid number of paths goal (equal to 0)
     */
    @Test
    fun createUserWithInvalidNbOfPathsGoal(){
        val exception = assertThrows(java.lang.Error::class.java) {
            val user = UserModel(userId, username,email,firstname,surname, dateOfBirth, distanceGoal,timeGoal, 0, database)
        }
        assertEquals("The number of paths goal can't be equal to 0.", exception.message)
    }
}