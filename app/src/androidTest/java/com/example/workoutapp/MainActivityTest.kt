package com.example.workoutapp

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    fun waitForLoadingBoxToDisappear(timeoutMillis: Long) {
        composeTestRule.waitUntil(timeoutMillis = timeoutMillis) {
            composeTestRule.onAllNodesWithTag("loadingBox").fetchSemanticsNodes().isEmpty()
        }
    }

    @Test
    fun testClickUseRealDataButton() {
        composeTestRule.onNodeWithTag("useRealDataButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("welcomeText")
            .assertIsDisplayed()
    }

    @Test
    fun testClickUseDummyDataButton() {
        composeTestRule.onNodeWithTag("useDummyDataButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("welcomeText")
            .assertIsDisplayed()
    }

    @Test
    fun testNavigateToExerciseListScreenViaCard() {
        composeTestRule.onNodeWithTag("useRealDataButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("exercisesCard")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("exercisesTitleText")
            .assertIsDisplayed()
    }

    @Test
    fun testNavigateToWorkoutListScreenViaCard() {
        composeTestRule.onNodeWithTag("useRealDataButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("workoutsCard")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("workoutsTitleText")
            .assertIsDisplayed()
    }

    @Test
    fun testNavigateToChallengeListScreenViaCard() {
        composeTestRule.onNodeWithTag("useRealDataButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("challengesCard")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("challengesTitleText")
            .assertIsDisplayed()
    }

    @Test
    fun testNavigateToExerciseListScreenViaBottomBar() {
        composeTestRule.onNodeWithTag("useRealDataButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("exerciseListBottomBarButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("exercisesTitleText")
            .assertIsDisplayed()
    }

    @Test
    fun testNavigateToWorkoutListScreenViaBottomBar() {
        composeTestRule.onNodeWithTag("useRealDataButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("workoutListBottomBarButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("workoutsTitleText")
            .assertIsDisplayed()
    }

    @Test
    fun testNavigateToChallengeListScreenViaBottomBar() {
        composeTestRule.onNodeWithTag("useRealDataButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("challengeListBottomBarButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("challengesTitleText")
            .assertIsDisplayed()
    }

    @Test
    fun testExerciseListScreenLoadingAnimation() {
        composeTestRule.onNodeWithTag("useRealDataButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        enableAirplaneMode()

        composeTestRule.onNodeWithTag("exerciseListBottomBarButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("loadingBox")
            .assertIsDisplayed()

        disableAirplaneMode()
    }

    @Test
    fun testWorkoutListScreenLoadingAnimation() {
        composeTestRule.onNodeWithTag("useRealDataButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        enableAirplaneMode()

        composeTestRule.onNodeWithTag("workoutListBottomBarButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("loadingBox")
            .assertIsDisplayed()

        disableAirplaneMode()
    }

    @Test
    fun testChallengeListScreenLoadingAnimation() {
        composeTestRule.onNodeWithTag("useRealDataButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        enableAirplaneMode()

        composeTestRule.onNodeWithTag("challengeListBottomBarButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("loadingBox")
            .assertIsDisplayed()

        disableAirplaneMode()
    }

    @Test
    fun testExercisesAreListed() {
        composeTestRule.onNodeWithTag("useDummyDataButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("exerciseListBottomBarButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        waitForLoadingBoxToDisappear(10000)

        composeTestRule.onNodeWithTag("1_exerciseCard")
            .assertIsDisplayed()
    }

    @Test
    fun testWorkoutsAreListed() {
        composeTestRule.onNodeWithTag("useDummyDataButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("workoutListBottomBarButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        waitForLoadingBoxToDisappear(10000)

        composeTestRule.onNodeWithTag("1_workoutCard")
            .assertIsDisplayed()
    }

    @Test
    fun testChallengesAreListed() {
        composeTestRule.onNodeWithTag("useDummyDataButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("challengeListBottomBarButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        waitForLoadingBoxToDisappear(10000)

        composeTestRule.onNodeWithTag("1_challengeCard")
            .assertIsDisplayed()
    }

    @Test
    fun testNavigateToExerciseDetailScreen() {
        composeTestRule.onNodeWithTag("useRealDataButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("exerciseListBottomBarButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        waitForLoadingBoxToDisappear(10000)

        val exerciseName = composeTestRule.onNodeWithTag("1_exerciseName", useUnmergedTree = true)
            .assertExists()
            .fetchSemanticsNode().config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text

        composeTestRule.onNodeWithTag("1_exerciseCard")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        waitForLoadingBoxToDisappear(10000)

        Thread.sleep(2000)

        composeTestRule.onNodeWithTag("exerciseDetailNameText")
            .assertTextEquals(exerciseName!!)
    }

    @Test
    fun testNavigateToWorkoutPlayerScreen() {
        composeTestRule.onNodeWithTag("useRealDataButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("workoutListBottomBarButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        waitForLoadingBoxToDisappear(10000)

        val workoutName = composeTestRule.onNodeWithTag("1_workoutName", useUnmergedTree = true)
            .assertExists()
            .fetchSemanticsNode().config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text

        composeTestRule.onNodeWithTag("1_workoutPlayButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        waitForLoadingBoxToDisappear(10000)

        Thread.sleep(5000)

        composeTestRule.onNodeWithTag("workoutPlayerNameText")
            .assertTextEquals(workoutName!!)
    }


    @Test
    fun testNavigateToChallengePlayerScreen() {
        composeTestRule.onNodeWithTag("useRealDataButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("challengeListBottomBarButton")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        waitForLoadingBoxToDisappear(10000)

        val challengeName = composeTestRule.onNodeWithTag("1_challengeName", useUnmergedTree = true)
            .assertExists()
            .fetchSemanticsNode().config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text

        composeTestRule.onNodeWithTag("1_challengeCard")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        waitForLoadingBoxToDisappear(10000)

        Thread.sleep(2000)

        composeTestRule.onNodeWithTag("challengePlayerNameText")
            .assertTextEquals(challengeName!!)
    }





    @Test
    fun testPrintHierarchy() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.openQuickSettings()
        Thread.sleep(2000) // Várunk, hogy betöltődjenek az elemek
        printUiHierarchy()
    }

}


fun setAirplaneMode(enabled: Boolean) {
    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    // Gyorsbeállítások megnyitása
    device.openQuickSettings()
    Thread.sleep(2000)

    // Keresd meg az Airplane Mode kapcsolót pontosan
    val airplaneModeSwitch = device.findObject(By.clazz("android.widget.Switch").desc("Airplane mode"))

    if (airplaneModeSwitch != null) {
        val isCurrentlyEnabled = airplaneModeSwitch.isChecked // Ellenőrizzük, be van-e kapcsolva

        if (enabled && !isCurrentlyEnabled) {
            airplaneModeSwitch.click() // Bekapcsolás
        } else if (!enabled && isCurrentlyEnabled) {
            airplaneModeSwitch.click() // Kikapcsolás
        }
    } else {
        println("❌ Nem található az Airplane Mode kapcsoló!")
    }

    // Gyorsbeállítások panel bezárása
    device.pressBack()
    device.pressBack()
}

// Airplane Mode bekapcsolása
fun enableAirplaneMode() {
    setAirplaneMode(true)
}

// Airplane Mode kikapcsolása
fun disableAirplaneMode() {
    setAirplaneMode(false)
}


fun printUiHierarchy() {
    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    println(device.dumpWindowHierarchy(System.out))
}


