package com.epfl.drawyourpath.userProfile

import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.database.MockDataBase
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalDate

class UserModelTest {
    private val userId = "aUyFLWgYxmoELRUr3jWYie61jbKO"
    private val username = "albert"
    private val email = "mockuser@mockdomain.org"
    private val firstname = "Hugo"
    private val surname = "Hof"
    private val dateOfBirth = LocalDate.of(2000, 2, 20)
    private val distanceGoal = 10.0
    private val timeGoal = 60.0
    private val nbOfPaths = 5
    private val database = MockDataBase()
    private val auth = MockAuth.MOCK_USER


    /**
     * Create a UserModel with invalid firstname throw an error
     */
    @Test
    fun createUserWithInvalidFirstname() {
        val exception = assertThrows(java.lang.Error::class.java) {
            UserModel(
                auth,
                username,
                "558",
                surname,
                dateOfBirth,
                distanceGoal,
                timeGoal,
                nbOfPaths,
                database
            )
        }
        assertEquals("Incorrect firstname", exception.message)

        val exception2 = assertThrows(java.lang.Error::class.java) {
            UserModel(
                auth,
                username,
                "",
                surname,
                dateOfBirth,
                distanceGoal,
                timeGoal,
                nbOfPaths,
                database
            )
        }
        assertEquals("Incorrect firstname", exception2.message)
    }

    /**
     * Create a UserModel with invalid surname throw an error
     */
    @Test
    fun createUserWithInvalidSurname() {
        val exception = assertThrows(java.lang.Error::class.java) {
            UserModel(
                auth,
                username,
                firstname,
                "4445",
                dateOfBirth,
                distanceGoal,
                timeGoal,
                nbOfPaths,
                database
            )
        }
        assertEquals("Incorrect surname", exception.message)

        val exception2 = assertThrows(java.lang.Error::class.java) {
            UserModel(
                auth,
                username,
                firstname,
                "",
                dateOfBirth,
                distanceGoal,
                timeGoal,
                nbOfPaths,
                database
            )
        }
        assertEquals("Incorrect surname", exception2.message)
    }

    /**
     * Create a UserModel with invalid date of birth throw an error
     */
    @Test
    fun createUserWithInvalidDate() {
        //under 10 years
        val exception = assertThrows(java.lang.Error::class.java) {
            UserModel(
                auth,
                username,
                firstname,
                surname,
                LocalDate.now(),
                distanceGoal,
                timeGoal,
                nbOfPaths,
                database
            )
        }
        assertEquals("Incorrect date of birth !", exception.message)
        //over 100 years
        val exception2 = assertThrows(java.lang.Error::class.java) {
            UserModel(
                auth,
                username,
                firstname,
                surname,
                LocalDate.of(1900, 2, 2),
                distanceGoal,
                timeGoal,
                nbOfPaths,
                database
            )
        }
        assertEquals("Incorrect date of birth !", exception2.message)
    }

    /**
     * Create a user model with invalid distance goal (equal to 0)
     */
    @Test
    fun createUserWithInvalidDistanceGoal() {
        val exception = assertThrows(java.lang.Error::class.java) {
            UserModel(
                auth,
                username,
                firstname,
                surname,
                dateOfBirth,
                0.0,
                timeGoal,
                nbOfPaths,
                database
            )
        }
        assertEquals("The distance goal can't be equal or less than 0.", exception.message)
    }

    /**
     * Create a user model with invalid activity time goal (equal to 0)
     */
    @Test
    fun createUserWithInvalidActivityTimeGoal() {
        val exception = assertThrows(java.lang.Error::class.java) {
            UserModel(
                auth,
                username,
                firstname,
                surname,
                dateOfBirth,
                distanceGoal,
                0.0,
                nbOfPaths,
                database
            )
        }
        assertEquals("The activity time goal can't be equal or less than 0.", exception.message)
    }

    /**
     * Create a user model with invalid number of paths goal (equal to 0)
     */
    @Test
    fun createUserWithInvalidNbOfPathsGoal() {
        val exception = assertThrows(java.lang.Error::class.java) {
            UserModel(
                auth,
                username,
                firstname,
                surname,
                dateOfBirth,
                distanceGoal,
                timeGoal,
                0,
                database
            )
        }
        assertEquals("The number of paths goal can't be equal or less than 0.", exception.message)
    }

    /**
     * Test if a userModel with input correct data is correctly created
     */
    @Test
    fun createCorrectUser() {
        val user: UserModel = UserModel(
            auth,
            username,
            firstname,
            surname,
            dateOfBirth,
            distanceGoal,
            timeGoal,
            nbOfPaths,
            database
        )
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
    fun setUserNameUnAvailableDoNothing() {
        val user: UserModel = UserModel(
            auth,
            username,
            firstname,
            surname,
            dateOfBirth,
            distanceGoal,
            timeGoal,
            nbOfPaths,
            database
        )
        val databaseBeforeUsernameList = database.usernameToUserId
        val databaseBeforeUserProfiles = database.userIdToUsername
        user.setUsername("nathan")
        assertEquals(user.getUsername(), username)
        //control the database
        assertEquals(databaseBeforeUsernameList, database.usernameToUserId)
        assertEquals(databaseBeforeUserProfiles, database.userIdToUsername)
    }

    /**
     * Test if set the same username will NOT change the username of the user
     */
    @Test
    fun setUserNameUnChangeDoNothing() {
        val user: UserModel = UserModel(
            auth,
            username,
            firstname,
            surname,
            dateOfBirth,
            distanceGoal,
            timeGoal,
            nbOfPaths,
            database
        )
        val databaseBeforeUsernameList = database.usernameToUserId
        val databaseBeforeUserProfiles = database.userIdToUsername
        user.setUsername(username)
        assertEquals(user.getUsername(), username)
        //control the database
        assertEquals(databaseBeforeUsernameList, database.usernameToUserId)
        assertEquals(databaseBeforeUserProfiles, database.userIdToUsername)
    }

    /**
     * Test if set the a new username will change correctly the username of the user
     */
    @Test
    fun setUserNameAvailable() {
        val user: UserModel = UserModel(
            auth,
            username,
            firstname,
            surname,
            dateOfBirth,
            distanceGoal,
            timeGoal,
            nbOfPaths,
            database
        )
        user.setUsername("nathan2")
        assertEquals(user.getUsername(), "nathan2")
        //control the database(compare with boolean to evict the null if condition)
        assertEquals(database.usernameToUserId.get("nathan2"), userId)
        assertEquals(database.userIdToUsername.get(userId), "nathan2")
    }

    /**
     * Test if set an incorrect distance goal (equal to zero) throw an error
     */
    @Test
    fun setInvalidDistanceGoal() {
        val user: UserModel = UserModel(
            auth,
            username,
            firstname,
            surname,
            dateOfBirth,
            distanceGoal,
            timeGoal,
            nbOfPaths,
            database
        )
        val exception = assertThrows(java.lang.Error::class.java) {
            user.setDistanceGoal(0.0)
        }
        assertEquals("The distance goal can't be equal or less than 0.", exception.message)
        //check the database(compare with boolean to evict the null if condition)
        assertEquals(
            (database.userIdToUserAccount.get(userId)?.getDistanceGoal() ?: 0) == distanceGoal, true
        )
    }

    /**
     * Test if set a correct distance goal correctly modify the user profile
     */
    @Test
    fun setValidDistanceGoal() {
        val user: UserModel = UserModel(
            auth,
            username,
            firstname,
            surname,
            dateOfBirth,
            distanceGoal,
            timeGoal,
            nbOfPaths,
            database
        )
        user.setDistanceGoal(12.0)
        assertEquals(user.getDistanceGoal(), 12.0, 0.0001)
        //check the database(compare with boolean to evict the null if condition)
        assertEquals(
            (database.userIdToUserAccount.get(userId)?.getDistanceGoal() ?: 0) == 12.0,
            true
        )
    }

    /**
     * Test if set an incorrect activity time goal (equal to zero) throw an error
     */
    @Test
    fun setInvalidActivityTimeGoal() {
        val user: UserModel = UserModel(
            auth,
            username,
            firstname,
            surname,
            dateOfBirth,
            distanceGoal,
            timeGoal,
            nbOfPaths,
            database
        )
        val exception = assertThrows(java.lang.Error::class.java) {
            user.setActivityTimeGoal(0.0)
        }
        assertEquals("The activity time goal can't be equal or less than 0.", exception.message)
        //check the database(compare with boolean to evict the null if condition)
        assertEquals(
            (database.userIdToUserAccount.get(userId)?.getActivityTime() ?: 0) == timeGoal,
            true
        )
    }

    /**
     * Test if set a correct activity time goal correctly modify the user profile
     */
    @Test
    fun setValidActivityTimeGoal() {
        val user: UserModel = UserModel(
            auth,
            username,
            firstname,
            surname,
            dateOfBirth,
            distanceGoal,
            timeGoal,
            nbOfPaths,
            database
        )
        user.setActivityTimeGoal(12.0)
        assertEquals(user.getActivityTime(), 12.0, 0.0001)
        //check the database(compare with boolean to evict the null if condition)
        assertEquals(
            (database.userIdToUserAccount.get(userId)?.getActivityTime() ?: 0) == 12.0,
            true
        )
    }

    /**
     * Test if set an incorrect number of paths goal (equal to zero) throw an error
     */
    @Test
    fun setInvalidNbOfPathsGoal() {
        val user: UserModel = UserModel(
            auth,
            username,
            firstname,
            surname,
            dateOfBirth,
            distanceGoal,
            timeGoal,
            nbOfPaths,
            database
        )
        val exception = assertThrows(java.lang.Error::class.java) {
            user.setNumberOfPathsGoal(0)
        }
        assertEquals("The number of paths goal can't be equal or less than 0.", exception.message)
        //check the database
        assertEquals(
            database.userIdToUserAccount.get(userId)?.getNumberOfPathsGoal() ?: 0,
            nbOfPaths
        )
    }

    /**
     * Test if set a correct number of paths goal correctly modify the user profile
     */
    @Test
    fun setValidNbOfPathsGoal() {
        val user: UserModel = UserModel(
            auth,
            username,
            firstname,
            surname,
            dateOfBirth,
            distanceGoal,
            timeGoal,
            nbOfPaths,
            database
        )
        user.setNumberOfPathsGoal(12)
        assertEquals(user.getNumberOfPathsGoal(), 12)
        //check the database
        assertEquals(database.userIdToUserAccount.get(userId)?.getNumberOfPathsGoal() ?: 0, 12)
    }

    /**
     * Test if the age given by getAge is correct
     */
    @Test
    fun returnCorrectAge() {
        val user: UserModel = UserModel(
            auth,
            username,
            firstname,
            surname,
            dateOfBirth,
            distanceGoal,
            timeGoal,
            nbOfPaths,
            database
        )
        assertEquals(user.getAge(), 23)

        val user2: UserModel = UserModel(
            auth,
            username,
            firstname,
            surname,
            LocalDate.of(2000, 5, 20),
            distanceGoal,
            timeGoal,
            nbOfPaths,
            database
        )
        assertEquals(user2.getAge(), 22)
    }

    /**
     * This function check that the correct empty firenlist is return with getFriendList function
     */
    @Test
    fun emptyFriendListIsGet() {
        val user: UserModel = UserModel(
            auth,
            username,
            firstname,
            surname,
            dateOfBirth,
            distanceGoal,
            timeGoal,
            nbOfPaths,
            database
        )
        val friendList = user.getFriendList()
        assertEquals(friendList.isEmpty(), true)
    }

    /**
     * This function test if the correct friends list is return after initialize it in the constructor
     */
    @Test
    fun correctFriendsListIsGet(){
        val expectedFriendsList = listOf<String>("friend1", "friend2")
        val user = UserModel(userId, auth.getEmail(), username, firstname, surname, dateOfBirth, distanceGoal,
        timeGoal, nbOfPaths, null, expectedFriendsList, database)
        assertEquals(user.getFriendList(), expectedFriendsList)
    }

    /**
     * Test if remove a user with username not present in the friend list throw an error
     */
    @Test
    fun removeFriendNotOnFriendList() {
        val user = UserModel(
            auth,
            username,
            firstname,
            surname,
            dateOfBirth,
            distanceGoal,
            timeGoal,
            nbOfPaths,
            database
        )
        val exception = assertThrows(java.lang.Error::class.java) {
            user.removeFriend("notId")
        }
        assertEquals("This user with userId notId is not in the friend list !", exception.message)
    }


    /**
     * Test if remove a user with userId will remove this user of the friend list
     */
    @Test
    fun removeFriendOnFriendList() {
        val newDataBase = MockDataBase()
        val expectedFriendsList = emptyList<String>()
        //select a user present on the database
        val user = newDataBase.userModelTest
        //check that at the beginning the friends list contains one user
        assertEquals(user.getFriendList(), listOf(newDataBase.userIdFriend1))
        //remove the user
        val isRemoved = user.removeFriend(newDataBase.userIdFriend1).get()
        assertEquals(isRemoved, true)
        assertEquals(user.getFriendList(), expectedFriendsList)
        //check the database
        assertEquals(newDataBase.userIdToUserAccount.get(userId)?.getFriendList() ?: listOf("not"),expectedFriendsList)
    }

    /**
     * Test if add a user with username will add this user to the friend list
     */
    @Test
    fun addFriendOnFriendList() {
        val newDatabase = MockDataBase()
        val expectedFriendList = listOf<String>(newDatabase.userIdFriend1, newDatabase.userIdFriend2)
        //select a user present on the database
        val user = newDatabase.userModelTest
        //check that at the beginning the friends list of the user contains only one user: friend1
        assertEquals(user.getFriendList(), listOf(newDatabase.userIdFriend1))
        ///add the user with userId friend2
        val isAdded = user.addFriend(newDatabase.userIdFriend2).get()
        assertEquals(isAdded, true)
        assertEquals(user.getFriendList(), expectedFriendList)
        //check the database
        assertEquals(newDatabase.userIdToUserAccount.get(userId)?.getFriendList() ?: listOf("not"), expectedFriendList)
    }

    /**
     * Test if add a user with userId not present on the database throw an error
     */
    @Test
    fun addFriendOnFriendListNotPresentOnDatabse() {
        val newDatabase = MockDataBase()
        //select a user present on the database
        val user = newDatabase.userModelTest
        //check that at the beginning the friends list of the user contains only one user: friend1
        assertEquals(user.getFriendList(), listOf(newDatabase.userIdFriend1))
        ///add the user with userId "notId" not present on the database
        val exception = Assert.assertThrows(java.util.concurrent.ExecutionException::class.java) {
            user.addFriend("notId").get()
        }
        assertEquals("java.lang.Error: The user with notId is not present on the database.", exception.message)
        assertEquals(newDatabase.userIdToUserAccount.get(userId)?.getFriendList(), listOf(newDatabase.userIdFriend1))
    }
}