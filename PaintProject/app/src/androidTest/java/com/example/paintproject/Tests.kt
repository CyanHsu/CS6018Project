package com.example.paintproject

//import androidx.test.ext.junit.rules.activityScenarioRule
import android.Manifest
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

//@RunWith(AndroidJUnit4::class)
class Tests {
    @get: Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val composeTestRule = createComposeRule()
    @get:Rule
    var mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE
        )


    @Test
    fun testNewDrawingBtn(){
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        composeTestRule.onNodeWithText("New Drawing").performClick()
        onView(withId(R.id.customView)).check(matches(isDisplayed()))

        var vm : SimpleViewModel? = null
        activityScenario.onActivity { activity ->
            vm = ViewModelProvider(activity)[SimpleViewModel::class.java]
        }
        Assert.assertEquals(vm?.shape, "circle")
        onView(withId(R.id.shapeBtn)).perform(click())
        onView(withId(R.id.rectBtn)).perform(click())
        Assert.assertEquals(vm?.shape, "rect")


    }
}


