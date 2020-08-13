package com.wilchrist.poegraph;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;

@RunWith(AndroidJUnit4.class)
public class PoemCreationTest {
    @Rule
    public ActivityTestRule<ListActivity> mListActivityRule=new ActivityTestRule<>(ListActivity.class);

    @Test
    public void createNewPoem(){
        onView(withId(R.id.insert_menu)).perform(click());
        onView(withId(R.id.txtTitle)).perform(typeText("Test Poem Title"), closeSoftKeyboard());
        onView(withId(R.id.txtContent)).perform(click());
        onView(withId(R.id.txtContent)).perform(typeText("Test Poem Content. It has to be a little little longer. Yeah it's good like that!"), closeSoftKeyboard());
        onView(withId(R.id.save_menu)).perform(click());
    }

    @Test
    public void DeleteLastNewPoem(){
        onView(withId(R.id.RVPoems)).perform(click());
    }
}