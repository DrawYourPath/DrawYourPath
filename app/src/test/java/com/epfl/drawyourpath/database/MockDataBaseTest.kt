package com.epfl.drawyourpath.database

import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.authentication.User
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.UserModel
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class MockDataBaseTest {
    val userIdTest: String = "aUyFLWgYxmoELRUr3jWYie61jbKO"
    val userAuthTest: User = MockAuth.MOCK_USER
    val usernameTest: String = "albert"
    val distanceGoalTest: Double = 10.0
    val activityTimeGoalTest: Double = 60.0
    val nbOfPathsGoalTest: Int = 5
    val firstnameTest = "Hugo"
    val surnameTest = "Hof"
    val takenUsername = "nathan"
    val dateOfBirthTest = LocalDate.of(2000, 2, 20)
    val userModelTest = UserModel(
        userAuthTest,
        usernameTest,
        firstnameTest,
        surnameTest,
        dateOfBirthTest,
        distanceGoalTest,
        activityTimeGoalTest,
        nbOfPathsGoalTest,
        MockDataBase()
    )

    /**
     * Test if userId present in the database is given has present
     */
    @Test
    fun isUserStoredInDatabasePresent() {
        val database = MockDataBase()
        val test = database.isUserStoredInDatabase(userIdTest).get()
        assertEquals(test, true)
    }

    /**
     * Test if a userId not present in the database is given has not present
     */
    @Test
    fun isUserStoredInDatabaseNotPresent() {
        val database = MockDataBase()
        val test = database.isUserStoredInDatabase("ex").get()
        assertEquals(test, false)
    }

    /**
     * Test if we obtain the correct username with a given userId present on the database
     */
    @Test
    fun getUsernameFromUserIdPresent() {
        val database = MockDataBase()
        val username = database.getUsernameFromUserId(userIdTest).get()
        assertEquals(username, usernameTest)
    }

    /**
     * Test if we obtain an null username with a given userId not present on the database
     */
    @Test
    fun getUsernameFromUserIdNotPresent() {
        val database = MockDataBase()
        val username = database.getUsernameFromUserId("test").get()
        assertEquals(username, null)
    }

    /**
     * Test if we obtain the correct userId with a given username present on the database
     */
    @Test
    fun getUserIdFromUsernamePresent() {
        val database = MockDataBase()
        val userId = database.getUserIdFromUsername(usernameTest).get()
        assertEquals(userId, userIdTest)
    }

    /**
     * Test if we obtain a null userId with a given username not present on the database
     */
    @Test
    fun getUserIdFromUsernameNotPresent() {
        val database = MockDataBase()
        val userId = database.getUserIdFromUsername("test").get()
        assertEquals(userId, null)
    }

    /**
     * Test if a given username present on the database is return as unavailable
     */
    @Test
    fun isUsernameAvailablePresent() {
        val database = MockDataBase()
        val availability = database.isUsernameAvailable(usernameTest).get()
        assertEquals(availability, false)
    }

    /**
     * Test if a given username not present on the database is return as available
     */
    @Test
    fun isUsernameAvailableNotPresent() {
        val database = MockDataBase()
        val availability = database.isUsernameAvailable("test").get()
        assertEquals(availability, true)
    }

    /**
     * Test if a given available username is correctly updated in the database
     */
    @Test
    fun updateUsernameAvailable() {
        val database = MockDataBase()
        database.updateUsername("test").get()
        assertEquals(database.usernameToUserId.contains(usernameTest), false)
        assertEquals(database.usernameToUserId.get("test"), userIdTest)
        assertEquals(database.userIdToUsername.get(userIdTest), "test")
    }

    /**
     * Test if a given unavailable username is not set in the database
     */
    @Test
    fun updateUsernameNotAvailable() {
        val database = MockDataBase()

        val exception = Assert.assertThrows(java.util.concurrent.ExecutionException::class.java) {
            database.updateUsername(takenUsername).get()
        }
        assertEquals(exception.message, "java.lang.Error: The username is not available !")
        assertEquals(database.usernameToUserId.get(takenUsername), "exId")
        assertEquals(database.usernameToUserId.get(usernameTest), userIdTest)
        assertEquals(database.userIdToUsername.get(userIdTest), usernameTest)
    }

    /**
     * Test that setting an available username is correctly set
     */
    @Test
    fun setUsernameAvailable() {
        val database = MockDataBase()
        database.setUsername("hugo").get()
        assertEquals(database.userIdToUsername.get(userIdTest), "hugo")
        assertEquals(database.usernameToUserId.get("hugo"), userIdTest)
    }

    /**
     * Test that setting an unavailable username is not set into the database
     */
    @Test
    fun setUsernameNotAvailable() {
        val database = MockDataBase()
        database.setUsername(takenUsername).get()
        assertEquals(database.usernameToUserId.get(takenUsername), "exId")
        assertEquals(database.userIdToUsername.get(userIdTest) == takenUsername, false)
    }

    /**
     * Test if the user account is correctly initiate with the userModel
     */
    @Test
    fun initUserProfileCorrectly() {
        val database = MockDataBase()
        database.initUserProfile(
            UserModel(
                userAuthTest,
                usernameTest,
                "hugo",
                "nathan",
                LocalDate.of(2000, 1, 1),
                12.0,
                30.0,
                2,
                database
            )
        ).get()
        assertEquals(database.userIdToUserAccount.get(userIdTest)?.getUsername(), usernameTest)
        assertEquals(
            database.userIdToUserAccount.get(userIdTest)?.getEmailAddress(),
            userAuthTest.getEmail()
        )
        assertEquals(database.userIdToUserAccount.get(userIdTest)?.getUserId(), userIdTest)
        assertEquals(database.userIdToUserAccount.get(userIdTest)?.getFirstname(), "hugo")
        assertEquals(database.userIdToUserAccount.get(userIdTest)?.getSurname(), "nathan")
        assertEquals(
            database.userIdToUserAccount.get(userIdTest)?.getDateOfBirth(),
            LocalDate.of(2000, 1, 1)
        )
        assertEquals(
            database.userIdToUserAccount.get(userIdTest)?.getCurrentDistanceGoal()?.toInt(), 12
        )
        assertEquals(
            database.userIdToUserAccount.get(userIdTest)?.getCurrentActivityTime()?.toInt(), 30
        )
        assertEquals(database.userIdToUserAccount.get(userIdTest)?.getCurrentNumberOfPathsGoal(), 2)
    }

    /**
     * Test if the user account with a given userId is correctly given
     */
    @Test
    fun getUserAccountCorrectly() {
        val database = MockDataBase()
        val user = database.getUserAccount(userIdTest).get()
        assertEquals(user.getUserId(), userIdTest)
        assertEquals(user.getUsername(), usernameTest)
        assertEquals(user.getEmailAddress(), userAuthTest.getEmail())
        assertEquals(user.getFirstname(), firstnameTest)
        assertEquals(user.getSurname(), surnameTest)
        assertEquals(user.getDateOfBirth(), dateOfBirthTest)
        assertEquals(user.getCurrentDistanceGoal(), distanceGoalTest, 0.001)
        assertEquals(user.getCurrentActivityTime(), activityTimeGoalTest, 0.001)
        assertEquals(user.getCurrentNumberOfPathsGoal(), nbOfPathsGoalTest)
    }

    /**
     * Test if the user account legged in the app is correctly given
     */
    @Test
    fun getLoggedUserAccountCorrectly() {
        val database = MockDataBase()
        val user = database.getLoggedUserAccount().get()
        assertEquals(user.getUserId(), userIdTest)
        assertEquals(user.getUsername(), usernameTest)
        assertEquals(user.getEmailAddress(), userAuthTest.getEmail())
        assertEquals(user.getFirstname(), firstnameTest)
        assertEquals(user.getSurname(), surnameTest)
        assertEquals(user.getDateOfBirth(), dateOfBirthTest)
        assertEquals(user.getCurrentDistanceGoal(), distanceGoalTest, 0.001)
        assertEquals(user.getCurrentActivityTime(), activityTimeGoalTest, 0.001)
        assertEquals(user.getCurrentNumberOfPathsGoal(), nbOfPathsGoalTest)
    }

    /**
     * Test if setting an invalid distance goal throw an error
     */
    @Test
    fun setDistanceGoalInvalid() {
        val database = MockDataBase()
        val exception = Assert.assertThrows(java.util.concurrent.ExecutionException::class.java) {
            database.setCurrentDistanceGoal(-1.00).get()
        }
        assertEquals(
            "java.lang.Error: The distance goal can't be less or equal than 0.",
            exception.message
        )
        assertEquals(
            database.userIdToUserAccount.get(userIdTest)?.getCurrentDistanceGoal()?.toInt(),
            distanceGoalTest.toInt()
        )
    }

    /**
     * Test if the distance goal is correctly set
     */
    @Test
    fun setDistanceGoalValid() {
        val database = MockDataBase()
        database.setCurrentDistanceGoal(13.0).get()
        assertEquals(
            database.userIdToUserAccount.get(userIdTest)?.getCurrentDistanceGoal()?.toInt(), 13
        )
    }

    /**
     * Test if setting an invalid activity time goal throw an error
     */
    @Test
    fun setActivityTimeGoalInvalid() {
        val database = MockDataBase()
        val exception = Assert.assertThrows(java.util.concurrent.ExecutionException::class.java) {
            database.setCurrentActivityTimeGoal(-1.00).get()
        }
        assertEquals(
            "java.lang.Error: The activity time goal can't be less or equal than 0.",
            exception.message
        )
        assertEquals(
            database.userIdToUserAccount.get(userIdTest)?.getCurrentActivityTime()?.toInt(),
            activityTimeGoalTest.toInt()
        )
    }

    /**
     * Test if the activity time goal is correctly set
     */
    @Test
    fun setActivityTimeGoalValid() {
        val database = MockDataBase()
        database.setCurrentActivityTimeGoal(45.0).get()
        assertEquals(
            database.userIdToUserAccount.get(userIdTest)?.getCurrentActivityTime()?.toInt(), 45
        )
    }

    /**
     * Test if setting an invalid number of paths goal throw an error
     */
    @Test
    fun setNbOfPathsGoalInvalid() {
        val database = MockDataBase()
        val exception = Assert.assertThrows(java.util.concurrent.ExecutionException::class.java) {
            database.setCurrentNbOfPathsGoal(-1).get()
        }
        assertEquals(
            "java.lang.Error: The number of paths goal can't be less or equal than 0.",
            exception.message
        )
        assertEquals(
            database.userIdToUserAccount.get(userIdTest)?.getCurrentNumberOfPathsGoal(),
            nbOfPathsGoalTest
        )
    }

    /**
     * Test if the number of paths goal is correctly set
     */
    @Test
    fun setNbOfPathsGoalValid() {
        val database = MockDataBase()
        database.setCurrentNbOfPathsGoal(1).get()
        assertEquals(database.userIdToUserAccount.get(userIdTest)?.getCurrentNumberOfPathsGoal(), 1)
    }

    /**
     * Test if adding a friend to the friendsList that is not on the database throw an error
     */
    @Test
    fun addInvalidUserToFriendsList() {
        val database = MockDataBase()
        val exception = Assert.assertThrows(java.util.concurrent.ExecutionException::class.java) {
            database.addUserToFriendsList("faultId").get()
        }
        assertEquals(
            "java.lang.Exception: The user with faultId is not present on the database.",
            exception.message
        )
        assertEquals(
            database.userIdToUserAccount.get(userIdTest)?.getFriendList(),
            database.friendsListTest
        )
    }

    /**
     * Test if adding a friend to the friendsList is correctly added
     */
    @Test
    fun addValidUserToFriendsList() {
        val database = MockDataBase()
        database.addUserToFriendsList(database.userIdFriend2).get()

        val expectedList = database.friendsListTest.toMutableList()
        expectedList.add(database.userIdFriend2)
        assertEquals(database.userIdToUserAccount.get(userIdTest)?.getFriendList(), expectedList)
        assertEquals(
            database.userIdToUserAccount.get(database.userIdFriend2)?.getFriendList(),
            listOf(database.userIdTest)
        )
    }

    /**
     * Test if removing a friend to the friendsList is correctly removed
     */
    @Test
    fun removeValidUserToFriendsList() {
        val database = MockDataBase()
        database.addUserToFriendsList(database.userIdFriend2).get()
        //test if the user has been correctly added
        val expectedList = database.friendsListTest.toMutableList()
        expectedList.add(database.userIdFriend2)
        assertEquals(database.userIdToUserAccount.get(userIdTest)?.getFriendList(), expectedList)
        //test if the same user has been correctly removed
        database.removeUserFromFriendlist(database.userIdFriend2).get()
        assertEquals(
            database.userIdToUserAccount.get(userIdTest)?.getFriendList(),
            database.friendsListTest
        )
        assertEquals(
            database.userIdToUserAccount.get(database.userIdFriend2)?.getFriendList(),
            emptyList<String>()
        )
    }

    /**
     * Test if adding and removing runs from the history works and doesn't change the order (based on startTime)
     */
    @Test
    fun addingAndRemovingRunsWorksAndKeepsOrder() {
        val database = MockDataBase()

        //Add a run with starting after the one in database
        val newRun1StartTime = database.runTestStartTime + 1e7.toLong()
        val newRun1 = Run(
            Path(listOf(LatLng(2.0, 3.0), LatLng(3.0, 4.0), LatLng(4.0, 3.0))),
            newRun1StartTime,
            newRun1StartTime + 2e6.toLong()
        )
        database.addRunToHistory(newRun1).get()

        val expectedHistory = database.runsHistoryTest.toMutableList()
        expectedHistory.add(1, newRun1)

        assertEquals(
            database.userIdToUserAccount.get(userIdTest)?.getRunsHistory(),
            expectedHistory
        )

        //Add a run with starting time before the one in database
        val newRun2StartTime = database.runTestStartTime - 1e7.toLong()
        val newRun2 = Run(
            Path(listOf(LatLng(2.0, 3.0), LatLng(3.0, 4.0), LatLng(4.0, 3.0))),
            newRun2StartTime,
            newRun2StartTime + 2e6.toLong()
        )
        database.addRunToHistory(newRun2).get()

        expectedHistory.add(0, newRun2)

        assertEquals(
            database.userIdToUserAccount.get(userIdTest)?.getRunsHistory(),
            expectedHistory
        )

        //Remove original run
        database.removeRunFromHistory(database.runTest)

        val expectedHistoryAfterRemove = listOf(newRun2, newRun1)

        assertEquals(
            database.userIdToUserAccount.get(userIdTest)?.getRunsHistory(),
            expectedHistoryAfterRemove
        )
    }

    /**
     * Test if adding a run with a startTime equal to an already stored run replaces the run
     * This behavior is the one of the Firebase
     */
    @Test
    fun addingNewRunWithSameStartingTimeReplacesOldRun() {
        val database = MockDataBase()
        val newRun = Run(
            Path(listOf(LatLng(2.0, 3.0), LatLng(3.0, 4.0), LatLng(4.0, 3.0))),
            database.runTestStartTime,
            database.runTestStartTime + 2e6.toLong()
        )
        database.addRunToHistory(newRun)

        val expectedHistory = listOf(newRun)

        assertEquals(
            database.userIdToUserAccount.get(userIdTest)?.getRunsHistory(),
            expectedHistory
        )
    }

    /**
     * Test if removing a run which does not exist does nothing, as expected
     */
    @Test
    fun removingRunWithNonExistingStartTimeDoesNothing() {
        val database = MockDataBase()
        val nonExistingStartingTime = database.runTestStartTime + 1e7.toLong()
        val nonExistingRun = Run(
            Path(listOf(LatLng(2.0, 3.0), LatLng(3.0, 4.0), LatLng(4.0, 3.0))),
            nonExistingStartingTime,
            nonExistingStartingTime + 2e6.toLong()
        )
        database.removeRunFromHistory(nonExistingRun)

        val expectedHistory = database.runsHistoryTest

        assertEquals(
            database.userIdToUserAccount.get(userIdTest)?.getRunsHistory(),
            expectedHistory
        )
    }


    /**
     * Test if adding a dailyGoal in the database is correctly made
     */
    @Test
    fun addDailyGoalCorrect() {
        val database = MockDataBase()
        database.addDailyGoal(DailyGoal(25.0, 30.0, 2, 20.0, 120.0, 1, LocalDate.of(2010, 1, 1)))
            .get()

        //control the dailyGoal List
        val obtainedDailyGoalList =
            database.userIdToUserAccount.get(database.userIdTest)!!.getDailyGoalList()
        assertEquals(obtainedDailyGoalList.size, 2)

        //check the first daily goal
        assertEquals(obtainedDailyGoalList.get(0).date, database.dailyGoalListTest.get(0).date)
        assertEquals(
            obtainedDailyGoalList.get(0).distanceInKilometerGoal,
            database.dailyGoalListTest.get(0).distanceInKilometerGoal,
            0.001
        )
        assertEquals(
            obtainedDailyGoalList.get(0).activityTimeInMinutesGoal,
            database.dailyGoalListTest.get(0).activityTimeInMinutesGoal,
            0.001
        )
        assertEquals(
            obtainedDailyGoalList.get(0).nbOfPathsGoal,
            database.dailyGoalListTest.get(0).nbOfPathsGoal
        )
        assertEquals(
            obtainedDailyGoalList.get(0).distanceInKilometerProgress,
            database.dailyGoalListTest.get(0).distanceInKilometerProgress,
            0.001
        )
        assertEquals(
            obtainedDailyGoalList.get(0).activityTimeInMinutesProgress,
            database.dailyGoalListTest.get(0).activityTimeInMinutesProgress,
            0.001
        )
        assertEquals(
            obtainedDailyGoalList.get(0).nbOfPathsProgress,
            database.dailyGoalListTest.get(0).nbOfPathsProgress
        )

        //check the second daily goal
        assertEquals(obtainedDailyGoalList.get(1).date, LocalDate.of(2010, 1, 1))
        assertEquals(obtainedDailyGoalList.get(1).distanceInKilometerGoal, 25.0, 0.001)
        assertEquals(obtainedDailyGoalList.get(1).activityTimeInMinutesGoal, 30.0, 0.001)
        assertEquals(obtainedDailyGoalList.get(1).nbOfPathsGoal, 2)
        assertEquals(obtainedDailyGoalList.get(1).distanceInKilometerProgress, 20.0, 0.001)
        assertEquals(obtainedDailyGoalList.get(1).activityTimeInMinutesProgress, 120.0, 0.001)
        assertEquals(obtainedDailyGoalList.get(1).nbOfPathsProgress, 1)
    }

    /**
     * Test if the achievements are update correctly
     */
    @Test
    fun updateUserAchievementsCorrect() {
        val database = MockDataBase()
        database.updateUserAchievements(10.0, 50.0).get()

        val userAccount = database.userIdToUserAccount.get(database.userIdTest)!!
        assertEquals(userAccount.getTotalDistance(), database.totalDistanceTest + 10.0, 0.001)
        assertEquals(
            userAccount.getTotalActivityTime(),
            database.totalActivityTimeTest + 50.0,
            0.001
        )
        assertEquals(userAccount.getTotalNbOfPaths(), database.totalNbOfPathsTest + 1)
    }
}