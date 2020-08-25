package com.wilchrist.poegraph;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class FirebaseActivityTest {
    static ArrayList<Poem> poems;

     @BeforeClass
     public static void ClassSetUp(){
         poems=FirebaseUtility.mPoems;
     }
    @Rule
    public ActivityTestRule<ListActivity> mActivityTestRule = new ActivityTestRule<>(ListActivity.class);

    public void signIn(){
        ViewInteraction supportVectorDrawablesButton = onView(
                allOf(withId(R.id.email_button), withText("Sign in with email"),
                        childAtPosition(
                                allOf(withId(R.id.btn_holder),
                                        childAtPosition(
                                                withId(R.id.container),
                                                0)),
                                0)));
        supportVectorDrawablesButton.perform(scrollTo(), click());


        ViewInteraction emailInputEditText = onView(
                allOf(withId(R.id.email),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.email_layout),
                                        0),
                                0)));
        emailInputEditText.perform(scrollTo(), typeText("norafob567@frost2d.net"), closeSoftKeyboard());

        ViewInteraction nextButton = onView(
                allOf(withId(R.id.button_next), withText("Next"),
                        childAtPosition(
                                allOf(withId(R.id.email_top_layout),
                                        childAtPosition(
                                                withClassName(is("android.widget.ScrollView")),
                                                0)),
                                2)));
        nextButton.perform(scrollTo(), click());

        ViewInteraction passwordInputEditText = onView(
                allOf(withId(R.id.password),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.password_layout),
                                        0),
                                0)));
        passwordInputEditText.perform(scrollTo(), replaceText("&é\"'(-_ç"), closeSoftKeyboard());

        ViewInteraction signInButton = onView(
                allOf(withId(R.id.button_done), withText("Sign in"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                4)));
        signInButton.perform(scrollTo(), click());
    }
    public void logout(){
        ViewInteraction logoutMenuItemView = onView(
                allOf(withId(R.id.logout_menu), withContentDescription("Logout"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        1),
                                1),
                        isDisplayed()));
        logoutMenuItemView.perform(click());
    }
    @Test
    public void a0_signInWithEmailTest() {
        signIn();

        logout();
    }

    @Test
    public void a1_createNewPoemTest(){
        signIn();
        onView(withId(R.id.insert_menu)).perform(click());
        onView(withId(R.id.txtTitle)).perform(typeText("Test Poem Title"), closeSoftKeyboard());
        onView(withId(R.id.txtContent)).perform(click());
        onView(withId(R.id.txtContent)).perform(typeText("Test Poem Content. It has to be a little little longer. Yeah it's good like that!"), closeSoftKeyboard());
        onView(withId(R.id.save_menu)).perform(click());
        logout();
    }
    @Test
    public void a2_editLastNewCreatedPoemTest(){
        signIn();
        poems = FirebaseUtility.mPoems;
        onView(
                allOf(withId(R.id.RVPoems),
                        childAtPosition(
                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                0)))
                .perform(actionOnItemAtPosition(poems.size()-1, click()));
        onView(withId(R.id.txtTitle)).perform(replaceText("Test Poem Title Edited"), closeSoftKeyboard());
        onView(withId(R.id.save_menu)).perform(click());
        logout();
    }
    @Test
    public void a3_deleteLastPoemCreatedTest() {
        signIn();
        poems = FirebaseUtility.mPoems;

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.RVPoems),
                        childAtPosition(
                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(poems.size()-1, click()));

        ViewInteraction deleteMenuItemView = onView(
                allOf(withId(R.id.delete_menu), withContentDescription("Delete"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        1),
                                1),
                        isDisplayed()));
        deleteMenuItemView.perform(click());

        logout();
    }
    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
