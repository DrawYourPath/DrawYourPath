package com.epfl.drawyourpath.database

import android.content.Context
import android.graphics.Bitmap
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import com.epfl.drawyourpath.R
import com.epfl.drawyourpath.authentication.MockAuth
import com.epfl.drawyourpath.authentication.User
import com.epfl.drawyourpath.challenge.dailygoal.DailyGoal
import com.epfl.drawyourpath.challenge.milestone.MilestoneEnum
import com.epfl.drawyourpath.challenge.trophy.Trophy
import com.epfl.drawyourpath.chat.Message
import com.epfl.drawyourpath.chat.MessageContent
import com.epfl.drawyourpath.community.Tournament
import com.epfl.drawyourpath.path.Path
import com.epfl.drawyourpath.path.Run
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import kotlin.streams.toList

class MockDatabaseTest {
    private val mockDatabase = MockDatabase()

    private val userIdTest: String = MockAuth.MOCK_USER.getUid()
    private val userAuthTest: User = MockAuth.MOCK_USER
    private val usernameTest: String = MockDatabase.mockUser.username!!
    private val distanceGoalTest: Double = MockDatabase.mockUser.goals!!.distance!!
    private val activityTimeGoalTest: Double = MockDatabase.mockUser.goals!!.activityTime!!.toDouble()
    private val nbOfPathsGoalTest: Int = MockDatabase.mockUser.goals!!.paths!!.toInt()
    private val firstnameTest = MockDatabase.mockUser.firstname!!
    private val surnameTest = MockDatabase.mockUser.surname!!
    private val takenUsername = mockDatabase.MOCK_USERS[1].username!!
    private val dateOfBirthTest = MockDatabase.mockUser.birthDate!!
    private val runHistoryTest = MockDatabase.mockUser.runs!!

    @get:Rule
    val executorRule = CountingTaskExecutorRule()

    /**
     * wait for all thread to be done or throw a timeout error
     */
    private fun waitUntilAllThreadAreDone() {
        executorRule.drainTasks(2, TimeUnit.SECONDS)
        Thread.sleep(100)
    }

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
        val test = database.isTournamentInDatabase(database.mockTournament.value!!.id).get()
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
    fun setUserDataForInvalidUserThrows() {
        val database = MockDatabase()
        assertThrows(Throwable::class.java) {
            database.setUserData("NOT_EXISTING_USER", UserData()).get()
        }
    }

    @Test
    fun setGoalsForInvalidUserThrows() {
        val database = MockDatabase()
        assertThrows(Throwable::class.java) {
            database.setGoals("NOT_EXISTING_USER", UserGoals()).get()
        }
        assertThrows(Throwable::class.java) {
            database.addDailyGoal("NOT_EXISTING_USER", DailyGoal(0.0, 0.0, 1)).get()
        }
    }

    @Test
    fun setProfilePhotoForInvalidUserThrows() {
        val database = MockDatabase()
        assertThrows(Throwable::class.java) {
            database.setProfilePhoto("NOT_EXISTING_USER", Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)).get()
        }
    }

    @Test
    fun runOperationsForInvalidUserThrows() {
        val database = MockDatabase()
        assertThrows(Throwable::class.java) {
            database.addRunToHistory(
                "NOT_EXISTING_USER",
                Run(Path(emptyList()), startTime = 1000, duration = 1000, endTime = 2000),
            ).get()
        }
        assertThrows(Throwable::class.java) {
            database.removeRunFromHistory(
                "NOT_EXISTING_USER",
                Run(Path(emptyList()), startTime = 1000, duration = 1000, endTime = 2000),
            ).get()
        }
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
                activityTime = 15.0,
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
                activityTime = 15.0,
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
        assertEquals(user.goals?.activityTime ?: 0.0, activityTimeGoalTest, 0.001)
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
                activityTime = activityTimeGoalTest,
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
        assertEquals(database.users[userIdTest]?.trophies, user.trophies)
        assertEquals(database.users[userIdTest]?.milestones, user.milestones)
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
            database.setGoals(userIdTest, UserGoals(activityTime = -1.0)).get()
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
        database.setGoals(userIdTest, UserGoals(activityTime = 45.0)).get()
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
     * Test that adding a trophy to incorrect user throw an error
     */
    @Test
    fun addTrophyIncorrectUserId() {
        val database = MockDatabase()

        assertThrows(Exception::class.java) {
            database.addTrophy(
                "incorrect",
                Trophy("12", "name", "description", LocalDate.of(2000, 2, 21), 2),
            ).get()
        }
    }

    /**
     * Test that adding a trophy is correctly added
     */
    @Test
    fun addTrophyCorrectly() {
        val database = MockDatabase()
        val trophy = Trophy("12", "name", "description", LocalDate.of(2000, 2, 21), 2)
        val userId = database.MOCK_USERS[0].userId!!
        database.addTrophy(userId, trophy).get()
        assertEquals(listOf(trophy), database.users[userId]?.trophies)
    }

    /**
     * Test that adding a milestone to incorrect user throw an error
     */
    @Test
    fun addMilestoneIncorrectUserId() {
        val database = MockDatabase()

        assertThrows(Exception::class.java) {
            database.addMilestone(
                "incorrect",
                MilestoneEnum.HUNDRED_KILOMETERS,
                LocalDate.of(2000, 2, 21),
            ).get()
        }
    }

    /**
     * Test that adding a milestone is correctly added
     */
    @Test
    fun addMilestoneCorrectly() {
        val database = MockDatabase()
        val userId = database.MOCK_USERS[0].userId!!
        database.addMilestone(userId, MilestoneEnum.HUNDRED_KILOMETERS, LocalDate.of(2000, 2, 21)).get()
        assertEquals(listOf(MilestoneData(MilestoneEnum.HUNDRED_KILOMETERS, LocalDate.of(2000, 2, 21))), database.users[userId]?.milestones)
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
            Path(listOf(listOf(LatLng(2.0, 3.0), LatLng(3.0, 4.0), LatLng(4.0, 3.0)))),
            startTime = newRun1StartTime,
            duration = 2e6.toLong(),
            endTime = newRun1StartTime + 2e6.toLong(),
        )
        val expectedHistory =
            arrayListOf(newRun1).also { it.addAll(database.users[userIdTest]!!.runs!!) }.also { runs -> runs.sortBy { it.getStartTime() } }

        database.addRunToHistory(userIdTest, newRun1).get()

        assertEquals(
            expectedHistory,
            database.users[userIdTest]?.runs,
        )

        // Add a run with starting time before the one in database
        val newRun2StartTime = 10 - 1e7.toLong()
        val newRun2 = Run(
            Path(listOf(listOf(LatLng(2.0, 3.0), LatLng(3.0, 4.0), LatLng(4.0, 3.0)))),
            startTime = newRun2StartTime,
            duration = 2e6.toLong(),
            endTime = newRun2StartTime + 2e6.toLong(),
        )
        database.addRunToHistory(userIdTest, newRun2).get()

        expectedHistory.add(0, newRun2)

        assertEquals(
            expectedHistory,
            database.users[userIdTest]?.runs,
        )

        // Remove original run
        database.removeRunFromHistory(userIdTest, newRun1)

        expectedHistory.remove(newRun1)

        assertEquals(
            expectedHistory,
            database.users[userIdTest]?.runs,
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
            Path(listOf(listOf(LatLng(2.0, 3.0), LatLng(3.0, 4.0), LatLng(4.0, 3.0)))),
            10,
            1e6.toLong(),
            1e6.toLong() + 10,
        )

        val expectedHistory =
            database.users[userIdTest]!!.runs!!.filter { it.getStartTime() != newRun.getStartTime() }.toMutableList().also { it.add(newRun) }
                .also { runs -> runs.sortBy { it.getStartTime() } }

        database.addRunToHistory(userIdTest, newRun)

        assertEquals(
            expectedHistory,
            database.users[userIdTest]?.runs,
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
            Path(listOf(listOf(LatLng(2.0, 3.0), LatLng(3.0, 4.0), LatLng(4.0, 3.0)))),
            startTime = nonExistingStartingTime,
            duration = 2e6.toLong(),
            endTime = nonExistingStartingTime + 2e6.toLong(),
        )
        database.removeRunFromHistory(userIdTest, nonExistingRun)

        assertEquals(
            runHistoryTest.size,
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
        assertEquals(dailyGoals.size, 3)

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
     * Test if getTournamentUID() returns a new ID "every time" (by incrementing it by 1 for the mockDB)
     */
    @Test
    fun getTournamentUIDReturnsANewID() {
        val database = MockDatabase()
        var currUID = database.getTournamentUniqueId()
        for (i: Int in 0..9) {
            val newID = database.getTournamentUniqueId()
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
            id = database.getTournamentUniqueId(),
            name = "testName",
            description = "testDesc",
            creatorId = "testCreator",
            startDate = LocalDateTime.now().plusDays(2L),
            endDate = LocalDateTime.now().plusDays(3L),
        )
        // check that the tournament is not stored beforehand
        assertTrue(!database.tournaments.containsKey(newTournament.id))
        // add the tournament
        database.addTournament(newTournament).get()
        waitUntilAllThreadAreDone()
        // check that the tournament is stored with its id as key
        assertTrue(database.tournaments.containsKey(newTournament.id))
        assertEquals(newTournament, database.tournaments[newTournament.id]!!.value)
    }

    /**
     * Test if addTournament() replaces a tournament with same id (should never happen)
     */
    @Test
    fun addNewTournamentWithExistingIdReplacesTournament() {
        val database = MockDatabase()
        val newTournament = Tournament(
            id = database.mockTournament.value!!.id,
            name = "testName",
            description = "testDesc",
            creatorId = "testCreator",
            startDate = LocalDateTime.now().plusDays(2L),
            endDate = LocalDateTime.now().plusDays(3L),
        )
        // check that the tournament is not the same beforehand
        assertNotEquals(newTournament, database.tournaments[database.mockTournament.value!!.id]!!.value)
        // add  the tournament
        database.addTournament(newTournament).get()
        waitUntilAllThreadAreDone()
        // check that the tournament has been replaced
        assertEquals(newTournament, database.tournaments[database.mockTournament.value!!.id]!!.value)
    }

    /**
     * Test if removeTournament() removes correctly the tournament from the tournaments list
     * and from the users' tournaments lists.
     */
    @Test
    fun removeTournamentsRemovesTournamentFromGeneralAndUsersTournamentsFiles() {
        val database = MockDatabase()
        val tournamentToRemove = database.mockTournament.value!!.id
        val tournamentsToRemoveParticipants = database.mockTournament.value!!.participants
        // check that the tournament is in general list of tournaments
        assertTrue(database.tournaments.containsKey(tournamentToRemove))
        assertEquals(database.mockTournament, database.tournaments[tournamentToRemove])
        // check that the participants have the tournament in their tournaments list
        for (participantId in tournamentsToRemoveParticipants) {
            assertTrue(database.users[participantId]!!.tournaments!!.contains(tournamentToRemove))
        }
        // remove the tournament
        database.removeTournament(tournamentToRemove).get()
        // check that the tournament is not in general list of tournaments
        assertTrue(!database.tournaments.containsKey(tournamentToRemove))
        // check that the participants don't have the tournament in their tournaments list
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
        // remove tournament
        database.removeTournament(tournamentToRemove).get()
        assertEquals(tournamentNumber, database.tournaments.size)
    }

    /**
     * Test if adding a non-existing user to an existing tournament throws an error.
     */
    @Test
    fun addingNonExistingUserToTournamentThrows() {
        val database = MockDatabase()
        val tournamentId = database.mockTournament.value!!.id
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
        val userId = MockDatabase.mockUser.userId!!
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
        val tournamentId = database.MOCK_TOURNAMENTS[1].value!!.id
        val userId = MockDatabase.mockUser.userId!!
        // check that user is not in tournament's participants
        assertTrue(!database.tournaments[tournamentId]!!.value!!.participants.contains(userId))
        // check that tournament is not in user's tournaments list
        assertTrue(!database.users[userId]!!.tournaments!!.contains(tournamentId))
        // add user to tournament
        database.addUserToTournament(userId, tournamentId).get()
        waitUntilAllThreadAreDone()
        // check that user is in tournament's participants
        assertTrue(database.tournaments[tournamentId]!!.value!!.participants.contains(userId))
        // check that tournament is in user's tournaments list
        assertTrue(database.users[userId]!!.tournaments!!.contains(tournamentId))
    }

    /**
     * Test if adding a user to a tournament in which it already participates does nothing (and does not throw) as expected.
     */
    @Test
    fun addingUserAlreadyInTournamentDoesNothing() {
        val database = MockDatabase()
        val tournamentId = database.MOCK_TOURNAMENTS[0].value!!.id
        val numberParticipants = database.MOCK_TOURNAMENTS[0].value!!.participants.size
        val userId = MockDatabase.mockUser.userId!!
        val numberTournamentsOfUser = MockDatabase.mockUser.tournaments!!.size
        // try to add user
        database.addUserToTournament(userId, tournamentId).get()
        // does not add a participant
        assertEquals(numberParticipants, database.tournaments[tournamentId]!!.value!!.participants.size)
        // does not add a tournament to the user
        assertEquals(numberTournamentsOfUser, database.users[userId]!!.tournaments!!.size)
    }

    /**
     * Test if removing a user from a tournament removes the user from the tournament's list of participants and
     * removes the tournament from the user's tournaments list.
     */
    @Test
    fun removingExistingUserFromExistingTournamentWorks() {
        val database = MockDatabase()
        val tournamentId = database.mockTournament.value!!.id
        val userId = MockDatabase.mockUser.userId!!
        // check that user is in tournament's participants
        assertTrue(database.tournaments[tournamentId]!!.value!!.participants.contains(userId))
        // check that tournament is in user's tournaments list
        assertTrue(database.users[userId]!!.tournaments!!.contains(tournamentId))
        // remove user from tournament
        database.removeUserFromTournament(userId, tournamentId)
        waitUntilAllThreadAreDone()
        // check that user is not in tournament's participants
        assertTrue(!database.tournaments[tournamentId]!!.value!!.participants.contains(userId))
        // check that tournament is not in user's tournaments list
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

    /**
     * Test if trying to retrieve an non-existing tournament from the database throws.
     */
    @Test
    fun getTournamentThatDoesNotExistThrows() {
        val database = MockDatabase()
        val tournamentId = "NotAnID"
        assertThrows(Throwable::class.java) {
            database.getTournament(tournamentId)
        }
    }

    /**
     * Test if retrieving an existing tournament from the database returns the tournament.
     */
    @Test
    fun getTournamentThatExistsReturnsTheTournament() {
        val database = MockDatabase()
        val expectedTournament = database.mockTournament.value!!.copy()
        val expectedTournamentId = expectedTournament.id
        val obtainTournament = database.getTournament(expectedTournamentId)
        waitUntilAllThreadAreDone()
        assertEquals(expectedTournament, obtainTournament.value)
    }

    /**
     * Test if trying to retrieve the info an non-existing tournament from the database throws.
     */
    @Test
    fun getTournamentInfoThatDoesNotExistThrows() {
        val database = MockDatabase()
        val tournamentId = "NotAnID"
        assertThrows(Throwable::class.java) {
            database.getTournamentInfo(tournamentId)
        }
    }

    /**
     * Test if retrieving the info of an existing tournament from the database returns the tournament.
     */
    @Test
    fun getTournamentInfoThatExistsReturnsTheTournament() {
        val database = MockDatabase()
        val expectedTournament = database.mockTournament.value!!.copy(participants = emptyList(), posts = emptyList())
        val expectedTournamentId = expectedTournament.id
        val obtainTournament = database.getTournamentInfo(expectedTournamentId)
        waitUntilAllThreadAreDone()
        assertEquals(expectedTournament, obtainTournament.value)
    }

    /**
     * Test if trying to retrieve the posts an non-existing tournament from the database throws.
     */
    @Test
    fun getTournamentPostsThatDoesNotExistThrows() {
        val database = MockDatabase()
        val tournamentId = "NotAnID"
        assertThrows(Throwable::class.java) {
            database.getTournamentPosts(tournamentId)
        }
    }

    /**
     * Test if retrieving the posts of an existing tournament from the database returns the tournament.
     */
    @Test
    fun getTournamentPostsThatExistsReturnsTheTournament() {
        val database = MockDatabase()
        val expectedPost = database.mockTournament.value!!.posts
        val expectedTournamentId = database.mockTournament.value!!.id
        val obtainPost = database.getTournamentPosts(expectedTournamentId)
        waitUntilAllThreadAreDone()
        assertEquals(expectedPost, obtainPost.value)
    }

    /**
     * Check that getTournamentsId return the correct list of tournaments Id
     */
    @Test
    fun getTournamentIdCorrectly() {
        val database = MockDatabase()
        val expectedList = database.MOCK_TOURNAMENTS_ID.value!!.toList()
        val obtainList = database.getAllTournamentsId()
        waitUntilAllThreadAreDone()
        assertEquals(expectedList, obtainList.value)
    }

    /**
     * Test if adding a post to a non-existing tournament throws
     */
    @Test
    fun addPostToTournamentThatDoesNotExistThrows() {
        val database = MockDatabase()
        val tournamentId = "NotAnID"
        assertThrows(Throwable::class.java) {
            database.addPostToTournament(tournamentId, database.mockPost).get()
        }
    }

    /**
     * Check that adding a post to a tournament adds correctly the post.
     */
    @Test
    fun addPostToTournamentWoksCorrectly() {
        val database = MockDatabase()
        val tournamentId = database.mockTournament.value!!.id
        val newPost = database.MOCK_POSTS[1]
        database.addPostToTournament(tournamentId, newPost)
        waitUntilAllThreadAreDone()
        assertEquals(database.tournaments[tournamentId]!!.value!!.posts.last(), newPost)
    }

    /**
     * Check that voting for a post with a non-existing user id throws.
     */
    @Test
    fun voteOnPostWithNonExistingUserIdThrows() {
        val database = MockDatabase()
        val userId = "NotAnId"
        val tournamentId = database.mockTournament.value!!.id
        val postId = database.mockTournament.value!!.posts[0].postId
        assertThrows(Throwable::class.java) {
            database.voteOnPost(userId, tournamentId, postId, 0).get()
        }
    }

    /**
     * Check that voting for a post in a non-existing tournament throws.
     */
    @Test
    fun voteOnPostWithNonExistingTournamentIdThrows() {
        val database = MockDatabase()
        val userId = database.MOCK_USERS[2].userId!!
        val tournamentId = "NotAnId"
        val postId = database.mockPost.postId
        assertThrows(Throwable::class.java) {
            database.voteOnPost(userId, tournamentId, postId, 0).get()
        }
    }

    /**
     * Check that voting for a post with non-existing post ID throws.
     */
    @Test
    fun voteOnPostWithNonExistingPostIdThrows() {
        val database = MockDatabase()
        val userId = database.MOCK_USERS[2].userId!!
        val tournamentId = database.mockTournament.value!!.id
        val postId = "NotAnId"
        assertThrows(Throwable::class.java) {
            database.voteOnPost(userId, tournamentId, postId, 0).get()
        }
    }

    /**
     * Check that voting on a post changes the votes accordingly.
     */
    @Test
    fun voteOnPostWorksCorrectly() {
        val database = MockDatabase()
        val userId = database.MOCK_USERS[2].userId!!
        val tournamentId = database.mockTournament.value!!.id
        val postId = database.mockPost.postId
        val oldUsersVotes = database.mockPost.getUsersVotes()
        // Check that the user has not voted yet
        assertEquals(oldUsersVotes[userId], null)
        // Vote
        database.voteOnPost(userId, tournamentId, postId, 1)
        waitUntilAllThreadAreDone()
        var newUsersVotes = database.tournaments[tournamentId]!!.value!!.posts.first {
            it.postId == postId
        }.getUsersVotes()
        // Check that the user has voted
        assertEquals(newUsersVotes[userId], 1)
        // Change vote
        database.voteOnPost(userId, tournamentId, postId, -1)
        waitUntilAllThreadAreDone()
        newUsersVotes = database.tournaments[tournamentId]!!.value!!.posts.first {
            it.postId == postId
        }.getUsersVotes()
        // Check that the user's vote has been changed
        assertEquals(newUsersVotes[userId], -1)
    }

    /**
     * Test if a conversation was correctly created
     */
    @Test
    fun createConversationCorrectly() {
        val database = MockDatabase()
        val convName = "New conversation"
        val targetContext: Context = ApplicationProvider.getApplicationContext()
        val welcomeMessage: String =
            targetContext.resources.getString(R.string.welcome_chat_message)
                .format(convName)
        val members: List<String> = listOf(
            database.MOCK_USERS[0].userId!!,
            database.MOCK_USERS[1].userId!!,
            database.MOCK_USERS[2].userId!!,
        )
        val creator = database.MOCK_USERS[0].userId!!

        // past database before executing the function
        val pastChatPreview = database.MOCK_CHAT_PREVIEWS
        val pastChatMembers = database.MOCK_CHAT_MEMBERS
        val pastChatMessages = database.MOCK_CHAT_MESSAGES
        val pastUser0Chat = database.MOCK_USERS[0].chatList
        val userId0 = database.MOCK_USERS[0].userId!!
        val pastUser1Chat = database.MOCK_USERS[1].chatList
        val userId1 = database.MOCK_USERS[1].userId!!
        val pastUser2Chat = database.MOCK_USERS[2].chatList
        val userId2 = database.MOCK_USERS[2].userId!!

        val date = LocalDate.now().atTime(LocalTime.now()).toEpochSecond(ZoneOffset.UTC)

        database.createChatConversation(convName, members, creator, welcomeMessage)

        // test the chat previews
        val newPreview = ChatPreview(
            conversationId = "1",
            title = convName,
            lastMessage = Message.createTextMessage(creator, welcomeMessage, date),
        )
        assertEquals(pastChatPreview + newPreview, database.chatPreviews.values.toList())
        // test the chat members
        val newMembers = ChatMembers(conversationId = "1", membersList = members)
        assertEquals(pastChatMembers + newMembers, database.chatMembers.values.toList())
        // test the chat messages
        val newMessage = ChatMessages(
            conversationId = "1",
            chat = MutableLiveData(
                listOf(
                    Message(
                        id = date,
                        content = MessageContent.Text(welcomeMessage),
                        senderId = creator,
                        timestamp = date,
                    ),
                ),
            ),
        )
        val newExpectedMessages = pastChatMessages.map { it.chat!!.value } + newMessage.chat!!.value
        val obtainMessages = database.chatMessages.values.map { it.chat!!.value }
        // wait for the live data
        waitUntilAllThreadAreDone()
        assertEquals(newExpectedMessages, obtainMessages)
        // test the chat list of the different members of the group
        assertEquals(
            if (pastUser0Chat == null) {
                listOf("1")
            } else {
                pastUser0Chat + "1"
            },
            database.users[userId0]!!.chatList,
        )
        assertEquals(
            if (pastUser1Chat == null) {
                listOf("1")
            } else {
                pastUser1Chat + "1"
            },
            database.users[userId1]!!.chatList,
        )
        assertEquals(
            if (pastUser2Chat == null) {
                listOf("1")
            } else {
                pastUser2Chat + "1"
            },
            database.users[userId2]!!.chatList,
        )
    }

    /**
     * Test if the correct chat preview was return
     */
    @Test
    fun getCorrectChatPreview() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_PREVIEWS[0].conversationId!!
        val chatPreview = database.getChatPreview(conversationId)
        assertEquals(database.MOCK_CHAT_PREVIEWS[0], chatPreview.value)
    }

    /**
     * Test if modifying a title of a conversation is correctly made
     */
    @Test
    fun modifyCorrectlyTitleChat() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_PREVIEWS[0].conversationId!!
        database.setChatTitle(conversationId, "new test name")
        val expectedPreview = database.MOCK_CHAT_PREVIEWS[0].copy(title = "new test name")
        assertEquals(expectedPreview, database.chatPreviews[conversationId])
    }

    /**
     * Test if the correct chat members list was return
     */
    @Test
    fun getCorrectChatMembersList() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_PREVIEWS[0].conversationId!!
        val members = database.getChatMemberList(conversationId).get()
        assertEquals(database.MOCK_CHAT_MEMBERS[0].membersList, members)
    }

    /**
     * Test if a member can be added to the memberList of a chat correctly
     */
    @Test
    fun addMemberToChatMemberListCorrectly() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_MEMBERS[0].conversationId!!
        val memberId = database.MOCK_USERS[2].userId!!
        database.addChatMember(memberId, conversationId)
        val expectedMemberList =
            listOf(memberId) + (database.MOCK_CHAT_MEMBERS[0].membersList ?: emptyList())
        // test the member list
        assertEquals(expectedMemberList, database.chatMembers[conversationId]!!.membersList)
        // test the chat list of the user
        assertEquals(
            listOf(conversationId) + (database.MOCK_USERS[2].chatList ?: emptyList()),
            database.users[memberId]?.chatList,
        )
    }

    /**
     * Test if a member can be removed to the memberList of a chat correctly
     */
    @Test
    fun removeMemberToChatMemberListCorrectly() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_MEMBERS[0].conversationId!!
        val removeUser = database.MOCK_USERS[1].userId!!
        // check the user is present before delete it
        assertEquals(
            true,
            database.chatMembers[conversationId]?.membersList?.contains(removeUser) ?: false,
        )
        // check the member list of the chat
        database.removeChatMember(database.MOCK_USERS[1].userId!!, conversationId)
        val expectedMemberList = (database.MOCK_CHAT_MEMBERS[0].membersList ?: emptyList()).stream()
            .filter { it != removeUser }.toList()
        assertEquals(expectedMemberList, database.chatMembers[conversationId]!!.membersList)
        // test the chat list of the user
        assertEquals(
            (database.MOCK_USERS[1].chatList ?: emptyList()).stream()
                .filter { it != conversationId }.toList(),
            database.users[removeUser]!!.chatList,
        )
    }

    /**
     * Test that the correct list is return for a given conversation.
     */
    @Test
    fun getChatMessagesCorrectly() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_MESSAGES[0].conversationId!!
        val expected = database.MOCK_CHAT_MESSAGES[0].copy().chat!!.value
        val messages = database.getChatMessages(conversationId)
        assertEquals(expected, messages.value)
    }

    /**
     * Test that a text message is correctly added to a conversation
     */
    @Test
    fun addChatTextMessageCorrectly() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_MESSAGES[0].conversationId!!
        val senderId = database.MOCK_USERS[0].userId!!
        val date = LocalDate.now().atTime(LocalTime.now()).toEpochSecond(ZoneOffset.UTC)
        val messageSent = Message.createTextMessage(senderId, "Message Sent!", date)
        database.addChatMessage(conversationId, messageSent)
        val expected = listOf(messageSent) + (database.MOCK_CHAT_MESSAGES[0].copy().chat?.value ?: emptyList())
        // wait for the live data
        waitUntilAllThreadAreDone()
        // check the chat messages list
        assertEquals(
            expected,
            database.chatMessages[conversationId]!!.chat!!.value,
        )
        // check the chat preview
        assertEquals(
            database.MOCK_CHAT_PREVIEWS[0].copy(lastMessage = messageSent),
            database.chatPreviews[conversationId],
        )
    }

    /**
     * Test that a run message is correctly added to a conversation
     */
    @Test
    fun addChatRunMessageCorrectly() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_MESSAGES[0].conversationId!!
        val senderId = database.MOCK_USERS[0].userId!!
        val date = LocalDate.now().atTime(LocalTime.now()).toEpochSecond(ZoneOffset.UTC)
        val messageSent = Message.createRunPathMessage(
            senderId,
            Run(
                startTime = 10,
                endTime = 20,
                path = Path(),
                duration = 10,
            ),
            date,
        )
        val expected = listOf(messageSent) + (database.MOCK_CHAT_MESSAGES[0].copy().chat?.value ?: emptyList())
        database.addChatMessage(conversationId, messageSent)
        // wait for the livedata
        waitUntilAllThreadAreDone()
        // check the chat messages list
        assertEquals(
            expected,
            database.chatMessages[conversationId]!!.chat!!.value,
        )
        // check the chat preview
        assertEquals(
            database.MOCK_CHAT_PREVIEWS[0].copy(lastMessage = messageSent),
            database.chatPreviews[conversationId],
        )
    }

    /**
     * Test that a message not in the preview is correctly deleted
     */
    @Test
    fun removeChatMessageNotPreviewCorrectly() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_MESSAGES[0].conversationId!!
        // val senderId = database.MOCK_USERS[0].userId!!
        val timestamp = database.MOCK_CHAT_MESSAGES[0].chat!!.value!!.get(1).timestamp
        database.removeChatMessage(conversationId, timestamp)
        val expected = (database.MOCK_CHAT_MESSAGES[0].copy().chat?.value ?: emptyList()).stream().filter { it.timestamp != timestamp }.toList()
        // wait for the live data
        waitUntilAllThreadAreDone()
        // check the messages list
        assertEquals(
            expected,
            database.chatMessages[conversationId]!!.chat!!.value,
        )
        // check the preview
        assertEquals(database.MOCK_CHAT_PREVIEWS[0], database.chatPreviews[conversationId])
    }

    /**
     * Test that a message in the conversation preview is correctly deleted
     */
    @Test
    fun removeChatMessageInPreviewCorrectly() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_MESSAGES[0].conversationId!!
        // val senderId = database.MOCK_USERS[0].userId!!
        val timestamp = database.MOCK_CHAT_MESSAGES[0].chat!!.value!!.get(0).timestamp
        database.removeChatMessage(conversationId, timestamp)
        // wait for the live data
        waitUntilAllThreadAreDone()
        // check the messages list
        assertEquals(
            (database.MOCK_CHAT_MESSAGES[0].chat?.value ?: emptyList()).stream().filter { it.timestamp != timestamp }.toList(),
            database.chatMessages[conversationId]!!.chat!!.value,
        )
        // check the preview
        assertEquals(
            database.MOCK_CHAT_PREVIEWS[0].copy(
                lastMessage = database.MOCK_CHAT_PREVIEWS[0].lastMessage!!.copy(
                    content = MessageContent.Text(
                        database.DELETE_MESSAGE_STR,
                    ),
                ),
            ),
            database.chatPreviews[conversationId],
        )
    }

    /**
     * Test that modify a given message in a given conversation is correctly made(present in the preview)
     */
    @Test
    fun modifyChatTextMessageInPreviewCorrectly() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_MESSAGES[0].conversationId!!
        // val senderId = database.MOCK_USERS[0].userId!!
        val timestamp = database.MOCK_CHAT_MESSAGES[0].chat!!.value!!.get(0).timestamp
        val newMessage = "edited message"
        database.modifyChatTextMessage(conversationId, timestamp, newMessage)
        val expected = (database.MOCK_CHAT_MESSAGES[0].chat?.value ?: emptyList()).stream()
            .map { if (it.timestamp == timestamp) it.copy(content = MessageContent.Text(newMessage)) else it }.toList()
        // wait for the live data
        waitUntilAllThreadAreDone()
        // check the messages list
        assertEquals(
            expected,
            database.chatMessages[conversationId]!!.chat!!.value,
        )
        // check the preview
        assertEquals(
            database.MOCK_CHAT_PREVIEWS[0].copy(
                lastMessage = database.MOCK_CHAT_PREVIEWS[0].lastMessage!!.copy(
                    content = MessageContent.Text(
                        newMessage,
                    ),
                ),
            ),
            database.chatPreviews[conversationId],
        )
    }

    /**
     * Test that modify a given message in a given conversation is correctly made( not present in the preview)
     */
    @Test
    fun modifyChatTextMessageNotPreviewCorrectly() {
        val database = MockDatabase()
        val conversationId = database.MOCK_CHAT_MESSAGES[0].conversationId!!
        // val senderId = database.MOCK_USERS[0].userId!!
        val timestamp = database.MOCK_CHAT_MESSAGES[0].chat!!.value!!.get(1).timestamp
        val newMessage = "edited message"
        val expected = (database.MOCK_CHAT_MESSAGES[0].chat?.value ?: emptyList()).stream()
            .map { if (it.timestamp == timestamp) it.copy(content = MessageContent.Text(newMessage)) else it }.toList()
        database.modifyChatTextMessage(conversationId, timestamp, newMessage)
        // wait for the live data
        waitUntilAllThreadAreDone()
        // check the messages list
        assertEquals(
            expected,
            database.chatMessages[conversationId]!!.chat!!.value,
        )
        // check the preview
        assertEquals(database.MOCK_CHAT_PREVIEWS[0], database.chatPreviews[conversationId])
    }

    /**
     * Test if the correct list of conversation id is returned
     */
    @Test
    fun getChatsListCorrectly() {
        val database = MockDatabase()
        val listConversationId = database.getChatList(database.MOCK_USERS[0].userId!!)
        // wait for the live data
        waitUntilAllThreadAreDone()
        assertEquals(database.MOCK_USERS[0].chatList, listConversationId.value)
    }

    /**
     * Test that the correct live data of the friend list is return
     */
    @Test
    fun getFriendsListCorrectly() {
        val database = MockDatabase()
        val friendsList = database.getFriendsList(database.MOCK_USERS[0].userId!!)
        // wait for the live data
        waitUntilAllThreadAreDone()
        assertEquals(database.MOCK_USERS[0].friendList, friendsList.value)
    }
}

/**
 * Get current date and time in epoch seconds
 * @return current dte and time in epoch seconds
 */
fun getCurrentDateTimeInEpochSeconds(): Long {
    return LocalDate.now().atTime(LocalTime.now()).toEpochSecond(ZoneOffset.UTC)
}
