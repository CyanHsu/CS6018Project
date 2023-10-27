package com.example.paintproject

import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StorageTest {

    private lateinit var scenario: FragmentScenario<DrawFragment>

    @Before
    fun setUp() {
        scenario = FragmentScenario.launchInContainer(DrawFragment::class.java)
    }

    @Test
    fun testFragmentInitialization() {
        scenario.onFragment {   fragment ->
            fragment.activity?.runOnUiThread {
                onView(withId(R.id.optionsBtn)).check(matches(isDisplayed()))
            }
        }
    }

}