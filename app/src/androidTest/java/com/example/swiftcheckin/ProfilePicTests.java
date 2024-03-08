package com.example.swiftcheckin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.core.content.ContextCompat;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.action.ViewActions.click;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.anything;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import java.util.List;


/**
 * This class tests the profile picture related functionality
 * As this component requires cross app interaction, we use UIAutomator to handle the permission dialog and to click on the camera and gallery options
 * Used as reference and adjusted for Java
 * https://developer.android.com/training/testing/other-components/ui-automator#kotlin_1
 */
@RunWith(AndroidJUnit4.class)
public class ProfilePicTests {

    private UiDevice device;
    @Rule
    public ActivityScenarioRule<ProfileActivity> scenario = new
            ActivityScenarioRule<ProfileActivity>(ProfileActivity.class);


    @Before
    public void setUp() {
          device = UiDevice.getInstance(getInstrumentation());
    }



    @Test
    public void profilePicCameraAccess() throws InterruptedException {

        // click if the avatar image is displayed (user has added some details )
        onView(withId(R.id.avatarImage)).check(matches(isDisplayed()));
        // Click on the add button to start the process of adding a profile picture
        onView(withId(R.id.edit_photo_button)).perform(click());

        // Handle permission dialog, if it appears
        UiObject2 allowButton = device.wait(Until.findObject(By.text("While using the app")), 5000);
        if (allowButton != null) {
            allowButton.click();
        }


        UiObject2 chooseFromGalleryOption = device.wait(Until.findObject(By.text("Take Photo")), 5000);
        if (chooseFromGalleryOption != null) {
            chooseFromGalleryOption.click();
        } else {
            throw new AssertionError("Camera Error ");
        }

        // close camera and go back
        device.pressBack();

    }


    /**
     * This test checks if the user is able to access the gallery and select a photo
     * Pre condition is that the user should have atleast some photo in their gallery to be able to select it.
     */
    @Test
    public void profilePicGalleryAccess()  {

        // click if the avatar image is displayed (user has added some details )
        onView(withId(R.id.avatarImage)).check(matches(isDisplayed()));
        // Click on the add button to start the process of adding a profile picture
        onView(withId(R.id.edit_photo_button)).perform(click());

        // Handle permission dialog, if it appears
        UiObject2 allowButton = device.wait(Until.findObject(By.text("While using the app")), 5000);
        if (allowButton != null) {
            allowButton.click();
        }


        UiObject2 chooseFromGalleryOption = device.wait(Until.findObject(By.text("Choose from Gallery")), 5000);
        if (chooseFromGalleryOption != null) {
            chooseFromGalleryOption.click();
        } else {
            throw new AssertionError("Gallery could not be opened");
        }

        // go to pictures then

        UiObject2 pictures = device.wait(Until.findObject(By.text("Pictures")), 5000);
        if (pictures != null) {
            pictures.click();
        } else {
            throw new AssertionError("Pictures could not be opened");
        }


        // close Gallery and go back
        device.pressBack();

    }



    @Test
    public void testRemoveProfileandAvatarGenerationPostThat() {
        // click remove button
        onView(withId(R.id.removeButton)).perform(click());
        // check the photo is removed and avatar is displayed
        onView(withId(R.id.avatarImage)).check(matches(isDisplayed()));


    }


}
