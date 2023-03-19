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

    /**
     * Test if a userModel with input correct data is correctly created
     */
    @Test
    fun createCorrectUser(){
        val user: UserModel = UserModel(userId,username,email,firstname,surname,dateOfBirth,distanceGoal,timeGoal,nbOfPaths,database)
        assertEquals(user.getUserId(), userId)
        assertEquals(user.getUsername(), username)
        assertEquals(user.getEmailAddress(), email)
        assertEquals(user.getFirstname(), firstname)
        assertEquals(user.getSurname(), surname)
        assertEquals(user.getDateOfBirth(), dateOfBirth)
        assertEquals(user.getDistanceGoal(), distanceGoal, 0.00001)
        assertEquals(user.getActivityTime(), timeGoal, 0.00001)
        assertEquals(user.getNumberOfPathsGoal(), nbOfPaths)
    }

    /**
     * Test if set a username unavailable will NOT change the username of the user
     */
    @Test
    fun setUserNameUnAvailableDoNothing(){
        val user: UserModel = UserModel(userId,username,email,firstname,surname,dateOfBirth,distanceGoal,timeGoal,nbOfPaths,database)
        val databaseBeforeUsernameList = database.userNameToUserId
        val databaseBeforeUserProfiles = database.userProfileWithUserNameLinckWithId
        user.setUsername("hugo")
        assertEquals(user.getUsername(), username)
        //control the database
        assertEquals(databaseBeforeUsernameList, database.userNameToUserId)
        assertEquals(databaseBeforeUserProfiles, database.userProfileWithUserNameLinckWithId)
    }

    /**
     * Test if set the same username will NOT change the username of the user
     */
    @Test
    fun setUserNameUnChangeDoNothing(){
        val user: UserModel = UserModel(userId,username,email,firstname,surname,dateOfBirth,distanceGoal,timeGoal,nbOfPaths,database)
        val databaseBeforeUsernameList = database.userNameToUserId
        val databaseBeforeUserProfiles = database.userProfileWithUserNameLinckWithId
        user.setUsername(username)
        assertEquals(user.getUsername(), username)
        //control the database
        assertEquals(databaseBeforeUsernameList, database.userNameToUserId)
        assertEquals(databaseBeforeUserProfiles, database.userProfileWithUserNameLinckWithId)
    }

    /**
     * Test if set the a new username will change correctly the username of the user
     */
    @Test
    fun setUserNameAvailable(){
        val user: UserModel = UserModel(userId,username,email,firstname,surname,dateOfBirth,distanceGoal,timeGoal,nbOfPaths,database)
        user.setUsername("nathan")
        assertEquals(user.getUsername(), "nathan")
        //control the database
        assertEquals(database.userNameToUserId.get("nathan"), userId)
        assertEquals(database.userProfileWithUserNameLinckWithId.get(userId), "nathan")
    }

    /**
     * Test if set an incorrect mail (incorrect format) throw an error
     */
    @Test
    fun setInvalidEMail(){
        val user: UserModel = UserModel(userId,username,email,firstname,surname,dateOfBirth,distanceGoal,timeGoal,nbOfPaths,database)
        val exception = assertThrows(java.lang.Error::class.java) {
            user.setEmailAddress("@incorrect")
        }
        assertEquals("Invalid email format !", exception.message)
    }

    /**
     * Test if set a correct email correctly modify the user profile
     */
    @Test
    fun setValidEMail(){
        val user: UserModel = UserModel(userId,username,email,firstname,surname,dateOfBirth,distanceGoal,timeGoal,nbOfPaths,database)
        user.setEmailAddress("eric.kurmann@epfl.ch")
        assertEquals(user.getEmailAddress(), "eric.kurmann@epfl.ch")
    }

    /**
     * Test if set an incorrect distance goal (equal to zero) throw an error
     */
    @Test
    fun setInvalidDistanceGoal(){
        val user: UserModel = UserModel(userId,username,email,firstname,surname,dateOfBirth,distanceGoal,timeGoal,nbOfPaths,database)
        val exception = assertThrows(java.lang.Error::class.java) {
            user.setDistanceGoal(0.0)
        }
        assertEquals("The distance goal can't be equal to 0 !", exception.message)
    }

    /**
     * Test if set a correct distance goal correctly modify the user profile
     */
    @Test
    fun setValidDistanceGoal(){
        val user: UserModel = UserModel(userId,username,email,firstname,surname,dateOfBirth,distanceGoal,timeGoal,nbOfPaths,database)
        user.setDistanceGoal(12.0)
        assertEquals(user.getDistanceGoal(), 12.0, 0.0001)
    }

    /**
     * Test if set an incorrect activity time goal (equal to zero) throw an error
     */
    @Test
    fun setInvalidActivotyTimeGoal(){
        val user: UserModel = UserModel(userId,username,email,firstname,surname,dateOfBirth,distanceGoal,timeGoal,nbOfPaths,database)
        val exception = assertThrows(java.lang.Error::class.java) {
            user.setActivityTimeGoal(0.0)
        }
        assertEquals("The activity time goal can't be equal to 0 !", exception.message)
    }

    /**
     * Test if set a correct activity time goal correctly modify the user profile
     */
    @Test
    fun setValidActivityTimeGoal(){
        val user: UserModel = UserModel(userId,username,email,firstname,surname,dateOfBirth,distanceGoal,timeGoal,nbOfPaths,database)
        user.setActivityTimeGoal(12.0)
        assertEquals(user.getActivityTime(), 12.0, 0.0001)
    }

    /**
     * Test if set an incorrect number of paths goal (equal to zero) throw an error
     */
    @Test
    fun setInvalidNbOfPathsGoal(){
        val user: UserModel = UserModel(userId,username,email,firstname,surname,dateOfBirth,distanceGoal,timeGoal,nbOfPaths,database)
        val exception = assertThrows(java.lang.Error::class.java) {
            user.setNumberOfPathsGoal(0)
        }
        assertEquals("The number of paths goal can't be equal to 0 !", exception.message)
    }

    /**
     * Test if set a correct number of paths goal correctly modify the user profile
     */
    @Test
    fun setValidNbOfPathsGoal(){
        val user: UserModel = UserModel(userId,username,email,firstname,surname,dateOfBirth,distanceGoal,timeGoal,nbOfPaths,database)
        user.setNumberOfPathsGoal(12)
        assertEquals(user.getNumberOfPathsGoal(), 12)
    }

    /**
     * Test if the age given by getAge is correct
     */
    @Test
    fun returnCorrectAge(){
        val user: UserModel = UserModel(userId,username,email,firstname,surname,dateOfBirth,distanceGoal,timeGoal,nbOfPaths,database)
        assertEquals(user.getAge(), 23)

        val user2: UserModel = UserModel(userId,username,email,firstname,surname, LocalDate.of(2000,5,20),distanceGoal,timeGoal,nbOfPaths,database)
        assertEquals(user2.getAge(), 22)
    }

    /**
     * This function check that the correct empty firenlist is return with getFriendList function
     */
    @Test
    fun emptyFriendListIsGet(){
        val user: UserModel = UserModel(userId,username,email,firstname,surname,dateOfBirth,distanceGoal,timeGoal,nbOfPaths,database)
        val friendList = user.getFriendList()
        assertEquals(friendList.isEmpty(), true)
    }

    /**
     * Test if remove a user with username not present in the friend list throw an error
     */
    @Test
    fun removeFriendNotOnFriendList(){
        val user: UserModel = UserModel(userId,username,email,firstname,surname,dateOfBirth,distanceGoal,timeGoal,nbOfPaths,database)
        val exception = assertThrows(java.lang.Error::class.java) {
            user.removeFriend("nathan")
        }
        assertEquals("This user is not in the friend list !", exception.message)
    }

    /**
     * Test if remove a user with username will remove this user of the friend list
     */
    @Test
    fun removeFriendOnFriendList(){
        val user: UserModel = UserModel(userId,username,email,firstname,surname,dateOfBirth,distanceGoal,timeGoal,nbOfPaths,database)
        //TODO: This function will be implemented during a next task
    }

    /**
     * Test if add a user with username will add this user to the friend list
     */
    @Test
    fun addFriendOnFriendList(){
        val user: UserModel = UserModel(userId,username,email,firstname,surname,dateOfBirth,distanceGoal,timeGoal,nbOfPaths,database)
        //TODO: This function will be implemented during a next task
    }

    /**
     * Test if add a user with username not present on the database throw an error
     */
    @Test
    fun addFriendOnFriendListNotPresentOnDataBase(){
        val user: UserModel = UserModel(userId,username,email,firstname,surname,dateOfBirth,distanceGoal,timeGoal,nbOfPaths,database)
        //TODO: This function will be implemented during a next task
    }
}