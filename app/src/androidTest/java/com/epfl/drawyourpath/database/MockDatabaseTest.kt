package com.epfl.drawyourpath.database

import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.authentication.User
import com.epfl.drawyourpath.community.Tournament
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.epfl.drawyourpath.userProfile.dailygoal.DailyGoal
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class MockDatabaseTest {
    val mockDatabase = MockDatabase()

    val userIdTest: String = MockAuth.MOCK_USER.getUid()
    val userAuthTest: User = MockAuth.MOCK_USER
    val usernameTest: String = mockDatabase.mockUser.username!!
    val distanceGoalTest: Double = mockDatabase.mockUser.goals!!.distance!!
    val activityTimeGoalTest: Double = mockDatabase.mockUser.goals!!.activityTime!!.toDouble()
    val nbOfPathsGoalTest: Int = mockDatabase.mockUser.goals!!.paths!!.toInt()
    val firstnameTest = mockDatabase.mockUser.firstname!!
    val surnameTest = mockDatabase.mockUser.surname!!
    val takenUsername = mockDatabase.MOCK_USERS[1].username!!
    val dateOfBirthTest = mockDatabase.mockUser.birthDate!!

    /**
     * Test if userId present in the database is given has present
     */
    @Test
    fun isUserStoredInDatabasePresent() {
        val database = MockDatabase()
        val test = database.isUserInDatabase(userIdTest).get()
        assertEquals(test, true)
    }

    /**
     * Test if a userId not present in the database is given has not present
     */
    @Test
    fun isUserStoredInDatabaseNotPresent() {
        val database = MockDatabase()
        val test = database.isUserInDatabase("ex").get()
        assertEquals(test, false)
    }

    /**
     * Test if a tournamentId present in the database is returned as present
     */
    @Test
    fun isTournamentInDatabasePresent() {
        val database = MockDatabase()
        val test = database.isTournamentInDatabase(database.mockTournament.id).get()
        assertEquals(true, test)
    }

    /**
     * Test is a tournamentId not present in the database is returned as not present.
     */
    @Test
    fun isTournamentInDatabaseNotPresent() {
        val database = MockDatabase()
        val test = database.isTournamentInDatabase("NotAnId").get()
        assertEquals(false, test)
    }

    /**
     * Test if we obtain the correct username with a given userId present on the database
     */
    @Test
    fun getUsernameFromUserIdPresent() {
        val database = MockDatabase()
        val username = database.getUsername(userIdTest).get()
        assertEquals(username, usernameTest)
    }

    /**
     * Test if we obtain an null username with a given userId not present on the database
     */
    @Test
    fun getUsernameFromUserIdNotPresent() {
        val database = MockDatabase()
        assertThrows(Throwable::class.java) { database.getUsername("testinvalid").get() }
    }

    /**
     * Test if we obtain the correct userId with a given username present on the database
     */
    @Test
    fun getUserIdFromUsernamePresent() {
        val database = MockDatabase()
        val userId = database.getUserIdFromUsername(usernameTest).get()
        assertEquals(userId, userIdTest)
    }

    /**
     * Test if we obtain a null userId with a given username not present on the database
     */
    @Test
    fun getUserIdFromUsernameNotPresent() {
        val database = MockDatabase()
        assertThrows(Throwable::class.java) { database.getUserIdFromUsername("test").get() }
    }

    /**
     * Test if a given username present on the database is return as unavailable
     */
    @Test
    fun isUsernameAvailablePresent() {
        val database = MockDatabase()
        val availability = database.isUsernameAvailable(usernameTest).get()
        assertEquals(availability, false)
    }

    /**
     * Test if a given username not present on the database is return as available
     */
    @Test
    fun isUsernameAvailableNotPresent() {
        val database = MockDatabase()
        val availability = database.isUsernameAvailable("test").get()
        assertEquals(availability, true)
    }

    /**
     * Test if a given available username is correctly updated in the database
     */
    @Test
    fun updateUsernameAvailable() {
        val database = MockDatabase()
        database.setUsername(userIdTest, "test").get()
        assertEquals(database.unameToUid.contains(usernameTest), false)
        assertEquals(database.unameToUid["test"], userIdTest)
        assertEquals(database.users[userIdTest]!!.username, "test")
    }

    @Test
    fun setUserDataSetsUserData() {
        val newUserData = UserData(
            userId = "foobar",
            surname = "Michel",
            firstname = "Machel",
            email = "mochel@mochel.mochel",
            username = "Muchel",
            goals = UserGoals(
                distance = 12.0,
                activityTime = 15,
                paths = 14,
            ),
        )
        val database = MockDatabase()
        database.createUser(newUserData.userId!!, UserData(username = newUserData.username)).get()
        database.setUserData(newUserData.userId!!, newUserData).get()

        val userData = database.getUserData(newUserData.userId!!).get()

        assertEquals(newUserData.email, userData.email)
        assertEquals(newUserData.username, userData.username)
        assertEquals(newUserData.surname, userData.surname)
        assertEquals(newUserData.firstname, userData.firstname)
        assertEquals(newUserData.goals?.paths, userData.goals?.paths)
        assertEquals(newUserData.goals?.distance, userData.goals?.distance)
        assertEquals(newUserData.goals?.activityTime, userData.goals?.activityTime)
    }

    @Test
    fun createUserCreatesUser() {
        val newUserData = UserData(
            userId = "foobar",
            surname = "Michel",
            firstname = "Machel",
            email = "mochel@mochel.mochel",
            username = "Muchel",
            goals = UserGoals(
                distance = 12.0,
                activityTime = 15,
                paths = 14,
            ),
        )
        val database = MockDatabase()
        database.createUser(newUserData.userId!!, newUserData).get()

        val userData = database.getUserData(newUserData.userId!!).get()

        assertEquals(newUserData.email, userData.email)
        assertEquals(newUserData.username, userData.username)
        assertEquals(newUserData.surname, userData.surname)
        assertEquals(newUserData.firstname, userData.firstname)
        assertEquals(newUserData.goals?.paths, userData.goals?.paths)
        assertEquals(newUserData.goals?.distance, userData.goals?.distance)
        assertEquals(newUserData.goals?.activityTime, userData.goals?.activityTime)
    }

    /**
     * Test if a given unavailable username is not set in the database
     */
    @Test
    fun updateUsernameNotAvailable() {
        val database = MockDatabase()

        assertThrows(Throwable::class.java) {
            database.setUsername(userIdTest, takenUsername).get()
        }
        assertEquals(userIdTest, database.unameToUid[usernameTest])
        assertEquals(usernameTest, database.users[userIdTest]?.username)
        assertNotNull(database.unameToUid[takenUsername])
    }

    /**
     * Test that setting an available username is correctly set
     */
    @Test
    fun setUsernameAvailable() {
        val database = MockDatabase()
        database.setUsername(userIdTest, "albert").get()
        assertEquals(database.users[userIdTest]?.username, "albert")
        assertEquals(database.unameToUid["albert"], userIdTest)
    }

    /**
     * Test that setting an unavailable username is not set into the database
     */
    @Test
    fun setUsernameNotAvailable() {
        val database = MockDatabase()
        assertThrows(Throwable::class.java) {
            database.setUsername(userIdTest, takenUsername).get()
        }
    }

    /**
     * Test if the user account with a given userId is correctly given
     */
    @Test
    fun getUserAccountCorrectly() {
        val database = MockDatabase()
        val user = database.getUserData(userIdTest).get()
        assertEquals(user.userId, userIdTest)
        assertEquals(user.username, usernameTest)
        assertEquals(user.email, userAuthTest.getEmail())
        assertEquals(user.firstname, firstnameTest)
        assertEquals(user.surname, surnameTest)
        assertEquals(user.birthDate, dateOfBirthTest)
        assertEquals(user.goals?.distance ?: 0.0, distanceGoalTest, 0.001)
        assertEquals(user.goals?.activityTime?.toDouble() ?: 0.0, activityTimeGoalTest, 0.001)
        assertEquals(user.goals?.paths ?: 0, nbOfPathsGoalTest.toLong())
    }

    /**
     * Test if the user account legged in the app is correctly given
     */
    @Test
    fun getLoggedUserAccountCorrectly() {
        val database = MockDatabase()
        database.users[userIdTest] = UserData(
            username = usernameTest,
            userId = userIdTest,
            email = userAuthTest.getEmail(),
            firstname = firstnameTest,
            surname = surnameTest,
            birthDate = dateOfBirthTest,
            goals = UserGoals(
                distance = distanceGoalTest,
                activityTime = activityTimeGoalTest.toLong(),
                paths = nbOfPathsGoalTest.toLong(),
            ),
        )
        val user = database.getUserData(userIdTest).get()
        assertEquals(user.userId, userIdTest)
        assertEquals(user.username, usernameTest)
        assertEquals(user.email, userAuthTest.getEmail())
        assertEquals(user.firstname, firstnameTest)
        assertEquals(user.surname, surnameTest)
        assertEquals(user.birthDate, dateOfBirthTest)
        assertEquals(user.goals?.distance ?: 0.0, distanceGoalTest, 0.001)
        assertEquals((user.goals?.activityTime ?: 0).toDouble(), activityTimeGoalTest, 0.001)
        assertEquals((user.goals?.paths ?: 0).toInt(), nbOfPathsGoalTest)
    }

    /**
     * Test if setting an invalid distance goal throw an error
     */
    @Test
    fun setDistanceGoalInvalid() {
        val database = MockDatabase()
        assertThrows(java.util.concurrent.ExecutionException::class.java) {
            database.setGoals(userIdTest, UserGoals(distance = -1.00)).get()
        }
        assertEquals(
            database.users[userIdTest]?.goals?.distance?.toInt(),
            distanceGoalTest.toInt(),
        )
    }

    /**
     * Test if the distance goal is correctly set
     */
    @Test
    fun setDistanceGoalValid() {
        val database = MockDatabase()
        database.setGoals(userIdTest, UserGoals(distance = 13.0)).get()
        assertEquals(
            database.users[userIdTest]?.goals?.distance?.toInt(),
            13,
        )
    }

    /**
     * Test if setting an invalid activity time goal throw an error
     */
    @Test
    fun setActivityTimeGoalInvalid() {
        val database = MockDatabase()
        assertThrows(Throwable::class.java) {
            database.setGoals(userIdTest, UserGoals(activityTime = -1)).get()
        }
        assertEquals(
            database.users[userIdTest]?.goals?.activityTime?.toInt(),
            activityTimeGoalTest.toInt(),
        )
    }

    /**
     * Test if the activity time goal is correctly set
     */
    @Test
    fun setActivityTimeGoalValid() {
        val database = MockDatabase()
        database.setGoals(userIdTest, UserGoals(activityTime = 45)).get()
        assertEquals(
            45,
            database.users[userIdTest]?.goals?.activityTime?.toInt(),
        )
    }

    /**
     * Test if setting an invalid number of paths goal throw an error
     */
    @Test
    fun setNbOfPathsGoalInvalid() {
        val database = MockDatabase()
        assertThrows(Throwable::class.java) {
            database.setGoals(userIdTest, UserGoals(paths = -1)).get()
        }
    }

    /**
     * Test if the number of paths goal is correctly set
     */
    @Test
    fun setNbOfPathsGoalValid() {
        val database = MockDatabase()
        database.setGoals(userIdTest, UserGoals(paths = 1)).get()
        assertEquals(database.users[userIdTest]?.goals?.paths, 1L)
    }

    /**
     * Test if adding a friend to the friendsList that is not on the database throw an error
     */
    @Test
    fun addInvalidUserToFriendsList() {
        val database = MockDatabase()

        val friendCount = database.users[userIdTest]?.friendList?.size ?: 0
        assertThrows(Throwable::class.java) {
            database.addFriend(userIdTest, "faultId").get()
        }
        assertEquals(
            database.users[userIdTest]?.friendList?.size ?: 0,
            friendCount,
        )
    }

    /**
     * Test if adding a friend to the friendsList is correctly added
     */
    @Test
    fun addValidUserToFriendsList() {
        val database = MockDatabase()
        database.addFriend(userIdTest, mockDatabase.MOCK_USERS[0].userId!!).get()

        assertEquals(
            database.users[userIdTest]?.friendList?.contains(mockDatabase.MOCK_USERS[0].userId!!),
            true,
        )

        assertEquals(
            database.users[mockDatabase.MOCK_USERS[0].userId!!]?.friendList?.contains(userIdTest),
            true,
        )
    }

    /**
     * Test if removing a friend to the friendsList is correctly removed
     */
    @Test
    fun removeValidUserToFriendsList() {
        val database = MockDatabase()

        database.addFriend(userIdTest, database.MOCK_USERS[3].userId!!).get()

        val friendCount = database.users[userIdTest]?.friendList?.size ?: 0

        // test if the user has been correctly added
        // test if the same user has been correctly removed
        database.removeFriend(userIdTest, database.MOCK_USERS[3].userId!!).get()

        assertEquals(
            database.users[userIdTest]?.friendList?.size,
            friendCount - 1,
        )
    }

    /**
     * Test if adding and removing runs from the history works and doesn't change the order (based on startTime)
     */
    @Test
    fun addingAndRemovingRunsWorksAndKeepsOrder() {
        val database = MockDatabase()

        // Add a run with starting after the one in database
        val newRun1StartTime = 10 + 1e7.toLong()
        val newRun1 = Run(
            Path(listOf(LatLng(2.0, 3.0), LatLng(3.0, 4.0), LatLng(4.0, 3.0))),
            newRun1StartTime,
            newRun1StartTime + 2e6.toLong(),
        )
        database.addRunToHistory(userIdTest, newRun1).get()

        val expectedHistory = arrayListOf(newRun1)

        assertEquals(
            database.users[userIdTest]?.runs,
            expectedHistory,
        )

        // Add a run with starting time before the one in database
        val newRun2StartTime = 10 - 1e7.toLong()
        val newRun2 = Run(
            Path(listOf(LatLng(2.0, 3.0), LatLng(3.0, 4.0), LatLng(4.0, 3.0))),
            newRun2StartTime,
            newRun2StartTime + 2e6.toLong(),
        )
        database.addRunToHistory(userIdTest, newRun2).get()

        expectedHistory.add(0, newRun2)

        assertEquals(
            database.users[userIdTest]?.runs,
            expectedHistory,
        )

        // Remove original run
        database.removeRunFromHistory(userIdTest, newRun1)

        val expectedHistoryAfterRemove = listOf(newRun2)

        assertEquals(
            database.users[userIdTest]?.runs,
            expectedHistoryAfterRemove,
        )
    }

    /**
     * Test if adding a run with a startTime equal to an already stored run replaces the run
     * This behavior is the one of the Firebase
     */
    @Test
    fun addingNewRunWithSameStartingTimeReplacesOldRun() {
        val database = MockDatabase()
        val newRun = Run(
            Path(listOf(LatLng(2.0, 3.0), LatLng(3.0, 4.0), LatLng(4.0, 3.0))),
            10,
            10 + 2e6.toLong(),
        )
        database.addRunToHistory(userIdTest, newRun)

        val expectedHistory = listOf(newRun)

        assertEquals(
            database.users[userIdTest]?.runs,
            expectedHistory,
        )
    }

    /**
     * Test if removing a run which does not exist does nothing, as expected
     */
    @Test
    fun removingRunWithNonExistingStartTimeDoesNothing() {
        val database = MockDatabase()
        val nonExistingStartingTime = 10 + 1e7.toLong()
        val nonExistingRun = Run(
            Path(listOf(LatLng(2.0, 3.0), LatLng(3.0, 4.0), LatLng(4.0, 3.0))),
            nonExistingStartingTime,
            nonExistingStartingTime + 2e6.toLong(),
        )
        database.removeRunFromHistory(userIdTest, nonExistingRun)

        val expectedHistory = listOf<Run>()

        assertEquals(
            expectedHistory.size,
            database.users[userIdTest]?.runs?.size,
        )
    }

    /**
     * Test if adding a dailyGoal in the database is correctly made
     */
    @Test
    fun addDailyGoalCorrect() {
        val database = MockDatabase()
        database.addDailyGoal(
            userIdTest,
            DailyGoal(
                25.0,
                30.0,
                2,
                20.0,
                120.0,
                1,
                LocalDate.of(2010, 1, 1),
            ),
        ).get()

        // control the dailyGoal List
        val dailyGoals =
            database.users[userIdTest]!!.dailyGoals!!
        assertEquals(dailyGoals.size, 2)

        val added = dailyGoals.find { it.date.year == 2010 && it.date.month.value == 1 }!!

        // check the first daily goal
        assertEquals(added.date, LocalDate.of(2010, 1, 1))
        assertEquals(
            added.expectedDistance,
            25.0,
            0.001,
        )
        assertEquals(
            added.expectedTime,
            30.0,
            0.001,
        )
        assertEquals(
            added.expectedPaths,
            2,
        )
        assertEquals(
            added.distance,
            20.0,
            0.001,
        )
        assertEquals(
            added.time,
            120.0,
            0.001,
        )
        assertEquals(
            added.paths,
            1,
        )
    }

    /**
     * Test if the achievements are update correctly
     */
    @Test
    fun updateUserAchievementsCorrect() {
        val database = MockDatabase()
        /*
        database.updateUserAchievements(userIdTest, 10.0, 50.0).get()
        val userAccount = database.users[database.userIdTest]!!
        assertEquals(userAccount.getTotalDistance(), database.totalDistanceTest + 10.0, 0.001)
        assertEquals(
            userAccount.getTotalActivityTime(),
            database.totalActivityTimeTest + 50.0,
            0.001,
        )
        assertEquals(userAccount.getTotalNbOfPaths(), database.totalNbOfPathsTest + 1)
        */
    }

    /**
     * Test if getTournamentUID() returns a new ID "every time" (by incrementing it by 1 for the mockDB)
     */
    @Test
    fun getTournamentUIDReturnsANewID() {
        val database = MockDatabase()
        var currUID = database.getTournamentUID()
        for (i: Int in 0..9) {
            val newID = database.getTournamentUID()
            assertEquals(currUID.toInt() + 1, newID.toInt())
            currUID = newID
        }
    }

    /**
     * Test if addTournament() adds the tournament to the database.
     */
    @Test
    fun addTournamentAddsNewTournamentToDatabase() {
        val database = MockDatabase()
        val newTournament = Tournament(
            id = database.getTournamentUID(),
            name = "testName",
            description = "testDesc",
            creatorId = "testCreator",
            startDate = LocalDateTime.now().plusDays(2L),
            endDate = LocalDateTime.now().plusDays(3L),
        )
        //check that the tournament is not stored beforehand
        assertTrue(!database.tournaments.containsKey(newTournament.id))
        //add the tournament
        database.addTournament(newTournament).get()
        //check that the tournament is stored with its id as key
        assertTrue(database.tournaments.containsKey(newTournament.id))
        assertEquals(newTournament, database.tournaments[newTournament.id])
    }

    /**
     * Test if addTournament() replaces a tournament with same id (should never happen)
     */
    @Test
    fun addNewTournamentWithExistingIdReplacesTournament() {
        val database = MockDatabase()
        val newTournament = Tournament(
            id = database.mockTournament.id,
            name = "testName",
            description = "testDesc",
            creatorId = "testCreator",
            startDate = LocalDateTime.now().plusDays(2L),
            endDate = LocalDateTime.now().plusDays(3L),
        )
        //check that the tournament is not the same beforehand
        assertNotEquals(newTournament, database.tournaments[database.mockTournament.id])
        //add  the tournament
        database.addTournament(newTournament).get()
        //check that the tournament has been replaced
        assertEquals(newTournament, database.tournaments[database.mockTournament.id])
    }

    /**
     * Test if removeTournament() removes correctly the tournament from the tournaments list
     * and from the users' tournaments lists.
     */
    @Test
    fun removeTournamentsRemovesTournamentFromGeneralAndUsersTournamentsFiles() {
        val database = MockDatabase()
        val tournamentToRemove = database.mockTournament.id
        val tournamentsToRemoveParticipants = database.mockTournament.participants
        //check that the tournament is in general list of tournaments
        assertTrue(database.tournaments.containsKey(tournamentToRemove))
        assertEquals(database.mockTournament, database.tournaments[tournamentToRemove])
        //check that the participants have the tournament in their tournaments list
        for (participantId in tournamentsToRemoveParticipants) {
            assertTrue(database.users[participantId]!!.tournaments!!.contains(tournamentToRemove))
        }
        //remove the tournament
        database.removeTournament(tournamentToRemove).get()
        //check that the tournament is not in general list of tournaments
        assertTrue(!database.tournaments.containsKey(tournamentToRemove))
        //check that the participants don't have the tournament in their tournaments list
        for (participantId in tournamentsToRemoveParticipants) {
            assertTrue(!database.users[participantId]!!.tournaments!!.contains(tournamentToRemove))
        }
    }

    /**
     * Test if removeTournament() does nothing with non-existing tournamentID.
     */
    @Test
    fun removeNonExistingTournamentDoesNothing() {
        val database = MockDatabase()
        val tournamentNumber = database.tournaments.size
        val tournamentToRemove = "NotAnID"
        //remove tournament
        database.removeTournament(tournamentToRemove).get()
        assertEquals(tournamentNumber, database.tournaments.size)
    }

    /**
     * Test if adding a non-existing user to an existing tournament throws an error.
     */
    @Test
    fun addingNonExistingUserToTournamentThrows() {
        val database = MockDatabase()
        val tournamentId = database.mockTournament.id
        val userId = "NotAnID"
        assertThrows(Throwable::class.java) {
            database.addUserToTournament(userId, tournamentId).get()
        }
    }

    /**
     * Test if adding an existing user to a non-existing tournament throws an error.
     */
    @Test
    fun addingUserToNonExistingTournamentThrows() {
        val database = MockDatabase()
        val tournamentId = "NotAnID"
        val userId = database.mockUser.userId!!
        assertThrows(Throwable::class.java) {
            database.addUserToTournament(userId, tournamentId).get()
        }
    }

    /**
     * Test if adding a user to a tournament adds the user to the participants list of the tournament
     * and adds the tournament to the tournaments' list of the user.
     */
    @Test
    fun addingExistingUserToExistingTournamentWorks() {
        val database = MockDatabase()
        val tournamentId = database.MOCK_TOURNAMENTS[1].id
        val userId = database.mockUser.userId!!
        //check that user is not in tournament's participants
        assertTrue(!database.tournaments[tournamentId]!!.participants.contains(userId))
        //check that tournament is not in user's tournaments list
        assertTrue(!database.users[userId]!!.tournaments!!.contains(tournamentId))
        //add user to tournament
        database.addUserToTournament(userId, tournamentId).get()
        //check that user is in tournament's participants
        assertTrue(database.tournaments[tournamentId]!!.participants.contains(userId))
        //check that tournament is in user's tournaments list
        assertTrue(database.users[userId]!!.tournaments!!.contains(tournamentId))
    }

    /**
     * Test if adding a user to a tournament in which it already participates does nothing (and does not throw) as expected.
     */
    @Test
    fun addingUserAlreadyInTournamentDoesNothing() {
        val database = MockDatabase()
        val tournamentId = database.MOCK_TOURNAMENTS[0].id
        val numberParticipants = database.MOCK_TOURNAMENTS[0].participants.size
        val userId = database.mockUser.userId!!
        val numberTournamentsOfUser = database.mockUser.tournaments!!.size
        //try to add user
        database.addUserToTournament(userId, tournamentId).get()
        //does not add a participant
        assertEquals(numberParticipants, database.tournaments[tournamentId]!!.participants.size)
        //does not add a tournament to the user
        assertEquals(numberTournamentsOfUser, database.users[userId]!!.tournaments!!.size)
    }

    /**
     * Test if removing a user from a tournament removes the user from the tournament's list of participants and
     * removes the tournament from the user's tournaments list.
     */
    @Test
    fun removingExistingUserFromExistingTournamentWorks() {
        val database = MockDatabase()
        val tournamentId = database.mockTournament.id
        val userId = database.mockUser.userId!!
        //check that user is in tournament's participants
        assertTrue(database.tournaments[tournamentId]!!.participants.contains(userId))
        //check that tournament is in user's tournaments list
        assertTrue(database.users[userId]!!.tournaments!!.contains(tournamentId))
        //remove user from tournament
        database.removeUserFromTournament(userId, tournamentId)
        //check that user is not in tournament's participants
        assertTrue(!database.tournaments[tournamentId]!!.participants.contains(userId))
        //check that tournament is not in user's tournaments list
        assertTrue(!database.users[userId]!!.tournaments!!.contains(tournamentId))
    }

    /**
     * Test if trying to remove a non-existing user from a non-existing tournament does not throw.
     */
    @Test
    fun removingNonExistingUserFromNonExistingTournamentDoesNotThrow() {
        val database = MockDatabase()
        val tournamentId = "NotAnID"
        val userId = "NotAnID"
        database.removeUserFromTournament(userId, tournamentId).get()
    }

}
