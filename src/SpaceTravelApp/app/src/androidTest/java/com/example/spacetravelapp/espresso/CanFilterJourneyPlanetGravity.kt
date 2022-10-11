package com.example.spacetravelapp.espresso


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.example.spacetravelapp.MainActivity
import com.example.spacetravelapp.R
import com.example.spacetravelapp.data.DatabaseHandler
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CanFilterJourneyPlanetGravity {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun canFilterJourneyPlanetGravity() {

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val databasehandler: DatabaseHandler = DatabaseHandler(appContext)
        databasehandler.emptyTable()

        val appCompatImageButton = Espresso.onView(
                Matchers.allOf(
                        ViewMatchers.withContentDescription("Open navigation drawer"),
                        childAtPosition(
                                Matchers.allOf(
                                        ViewMatchers.withId(R.id.toolbar),
                                        childAtPosition(
                                                ViewMatchers.withClassName(Matchers.`is`("com.google.android.material.appbar.AppBarLayout")),
                                                0
                                        )
                                ),
                                1
                        ),
                        ViewMatchers.isDisplayed()
                )
        )
        appCompatImageButton.perform(ViewActions.click())

        val appCompatImageButton2 = Espresso.onView(
                Matchers.allOf(
                        ViewMatchers.withId(R.id.ibChangeUser), ViewMatchers.withContentDescription("Changes the users account"),
                        childAtPosition(
                                childAtPosition(
                                        ViewMatchers.withId(R.id.navigation_header_container),
                                        0
                                ),
                                2
                        ),
                        ViewMatchers.isDisplayed()
                )
        )
        appCompatImageButton2.perform(ViewActions.click())

        val materialButton = Espresso.onView(
                Matchers.allOf(
                        ViewMatchers.withId(R.id.btnCreateProfile), ViewMatchers.withText("Create new profile"),
                        childAtPosition(
                                childAtPosition(
                                        ViewMatchers.withClassName(Matchers.`is`("androidx.coordinatorlayout.widget.CoordinatorLayout")),
                                        1
                                ),
                                1
                        ),
                        ViewMatchers.isDisplayed()
                )
        )
        materialButton.perform(ViewActions.click())

        val appCompatEditText = Espresso.onView(
                Matchers.allOf(
                        ViewMatchers.withId(R.id.etUsername),
                        childAtPosition(
                                childAtPosition(
                                        ViewMatchers.withId(android.R.id.content),
                                        0
                                ),
                                1
                        ),
                        ViewMatchers.isDisplayed()
                )
        )
        appCompatEditText.perform(ViewActions.replaceText("Test"), ViewActions.closeSoftKeyboard())

        val materialButton2 = Espresso.onView(
                Matchers.allOf(
                        ViewMatchers.withId(R.id.btnSaveProfile), ViewMatchers.withText("Save Profile"),
                        childAtPosition(
                                childAtPosition(
                                        ViewMatchers.withId(android.R.id.content),
                                        0
                                ),
                                2
                        ),
                        ViewMatchers.isDisplayed()
                )
        )
        materialButton2.perform(ViewActions.click())

        val recyclerView = Espresso.onView(
                Matchers.allOf(
                        ViewMatchers.withId(R.id.recycleviewer_profile),
                        childAtPosition(
                                ViewMatchers.withClassName(Matchers.`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                                0
                        )
                )
        )
        recyclerView.perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, ViewActions.click()))

        val appCompatImageButton3 = Espresso.onView(
                Matchers.allOf(
                        ViewMatchers.withContentDescription("Open navigation drawer"),
                        childAtPosition(
                                Matchers.allOf(
                                        ViewMatchers.withId(R.id.toolbar),
                                        childAtPosition(
                                                ViewMatchers.withClassName(Matchers.`is`("com.google.android.material.appbar.AppBarLayout")),
                                                0
                                        )
                                ),
                                1
                        ),
                        ViewMatchers.isDisplayed()
                )
        )
        appCompatImageButton3.perform(ViewActions.click())

        val navigationMenuItemView = Espresso.onView(
                Matchers.allOf(
                        ViewMatchers.withId(R.id.nav_archive),
                        childAtPosition(
                                Matchers.allOf(
                                        ViewMatchers.withId(R.id.design_navigation_view),
                                        childAtPosition(
                                                ViewMatchers.withId(R.id.nav_view),
                                                0
                                        )
                                ),
                                2
                        ),
                        ViewMatchers.isDisplayed()
                )
        )
        navigationMenuItemView.perform(ViewActions.click())

        val appCompatImageButton4 = Espresso.onView(
                Matchers.allOf(
                        ViewMatchers.withId(R.id.ibFilter), ViewMatchers.withContentDescription("Change Filters"),
                        childAtPosition(
                                Matchers.allOf(
                                        ViewMatchers.withId(R.id.constraintLayout),
                                        childAtPosition(
                                                ViewMatchers.withClassName(Matchers.`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0
                                        )
                                ),
                                1
                        ),
                        ViewMatchers.isDisplayed()
                )
        )
        appCompatImageButton4.perform(ViewActions.click())

        val appCompatEditText2 = Espresso.onView(
                Matchers.allOf(
                        ViewMatchers.withId(R.id.etMinTemperature),
                        childAtPosition(
                                childAtPosition(
                                        ViewMatchers.withId(android.R.id.content),
                                        0
                                ),
                                6
                        ),
                        ViewMatchers.isDisplayed()
                )
        )
        appCompatEditText2.perform(ViewActions.replaceText("-100"), ViewActions.closeSoftKeyboard())

        val appCompatEditText3 = Espresso.onView(
                Matchers.allOf(
                        ViewMatchers.withId(R.id.etMaxTemperature),
                        childAtPosition(
                                childAtPosition(
                                        ViewMatchers.withId(android.R.id.content),
                                        0
                                ),
                                7
                        ),
                        ViewMatchers.isDisplayed()
                )
        )
        appCompatEditText3.perform(ViewActions.replaceText("100"), ViewActions.closeSoftKeyboard())

        val materialButton3 = Espresso.onView(
                Matchers.allOf(
                        ViewMatchers.withId(R.id.btnApplyFilters), ViewMatchers.withText("Apply Filters"),
                        childAtPosition(
                                childAtPosition(
                                        ViewMatchers.withId(android.R.id.content),
                                        0
                                ),
                                13
                        ),
                        ViewMatchers.isDisplayed()
                )
        )
        materialButton3.perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.recycleview_planets)).perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(
                        R.id.ibFavourite
                )))

        Espresso.onView(ViewMatchers.withId(R.id.recycleview_planets)).perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, clickOnViewChild(
                        R.id.ibFavourite
                )))

        Espresso.onView(ViewMatchers.withId(R.id.recycleview_planets)).perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(2, clickOnViewChild(
                        R.id.ibFavourite
                )))

        Espresso.onView(ViewMatchers.withId(R.id.recycleview_planets)).perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(3, clickOnViewChild(
                        R.id.ibFavourite
                )))

        Espresso.onView(ViewMatchers.withId(R.id.recycleview_planets)).perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(4, clickOnViewChild(
                        R.id.ibFavourite
                )))

        val appCompatImageButton10 = Espresso.onView(
                Matchers.allOf(
                        ViewMatchers.withContentDescription("Open navigation drawer"),
                        childAtPosition(
                                Matchers.allOf(
                                        ViewMatchers.withId(R.id.toolbar),
                                        childAtPosition(
                                                ViewMatchers.withClassName(Matchers.`is`("com.google.android.material.appbar.AppBarLayout")),
                                                0
                                        )
                                ),
                                1
                        ),
                        ViewMatchers.isDisplayed()
                )
        )
        appCompatImageButton10.perform(ViewActions.click())

        val navigationMenuItemView2 = Espresso.onView(
                Matchers.allOf(
                        ViewMatchers.withId(R.id.nav_journeyPlanner),
                        childAtPosition(
                                Matchers.allOf(
                                        ViewMatchers.withId(R.id.design_navigation_view),
                                        childAtPosition(
                                                ViewMatchers.withId(R.id.nav_view),
                                                0
                                        )
                                ),
                                3
                        ),
                        ViewMatchers.isDisplayed()
                )
        )
        navigationMenuItemView2.perform(ViewActions.click())

        val appCompatSpinner = Espresso.onView(
                Matchers.allOf(ViewMatchers.withId(R.id.spnJourneyGravity),
                        childAtPosition(
                                childAtPosition(
                                        ViewMatchers.withClassName(Matchers.`is`("android.widget.LinearLayout")),
                                        2),
                                1),
                        ViewMatchers.isDisplayed()))
        appCompatSpinner.perform(ViewActions.click())

        Espresso.onData(Matchers.equalTo("Earth-like")).inRoot(RootMatchers.isPlatformPopup()).perform(ViewActions.click())

        val appCompatImageButton11 = Espresso.onView(
                Matchers.allOf(ViewMatchers.withId(R.id.ibJourneyFilter), ViewMatchers.withContentDescription("Apply Filters"),
                        childAtPosition(
                                childAtPosition(
                                        ViewMatchers.withClassName(Matchers.`is`("android.widget.LinearLayout")),
                                        3),
                                0),
                        ViewMatchers.isDisplayed()))
        appCompatImageButton11.perform(ViewActions.click())

        val textView = Espresso.onView(
                Matchers.allOf(ViewMatchers.withId(R.id.tvJourneyPlanetName), ViewMatchers.withText("EPIC 248847494 b"),
                        ViewMatchers.withParent(ViewMatchers.withParent(ViewMatchers.withId(R.id.cardviewJourney))),
                        ViewMatchers.isDisplayed()))
        textView.check(ViewAssertions.matches(ViewMatchers.withText("EPIC 248847494 b")))
    }

    fun clickOnViewChild(viewId: Int) = object : ViewAction {
        override fun getConstraints() = null

        override fun getDescription() = "Click on a child view with specified id."

        override fun perform(uiController: UiController, view: View) = ViewActions.click().perform(uiController, view.findViewById<View>(viewId))
    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
