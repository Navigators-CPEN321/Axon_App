package domain.tiger.axon;


import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;


import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class OverallTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void overallTest() {

try {
 Thread.sleep(1000);
 } catch (InterruptedException e) {
 e.printStackTrace();
 }

    //enter email for login

        ViewInteraction appCompatEditText = onView(
allOf(withId(R.id.editTextEmail), withText("Enter email"),
childAtPosition(
childAtPosition(
withId(android.R.id.content),
0),
1),
isDisplayed()));
        appCompatEditText.perform(click());
        
        ViewInteraction appCompatEditText2 = onView(
allOf(withId(R.id.editTextEmail), withText("Enter email"),
childAtPosition(
childAtPosition(
withId(android.R.id.content),
0),
1),
isDisplayed()));
        appCompatEditText2.perform(replaceText("bob@ubc.ca"), closeSoftKeyboard());
        

        

try {
 Thread.sleep(1000);
 } catch (InterruptedException e) {
 e.printStackTrace();
 }


    //enter password for login

        ViewInteraction appCompatEditText8 = onView(
allOf(withId(R.id.editTextPassword), withText("Enter password"),
childAtPosition(
childAtPosition(
withId(android.R.id.content),
0),
2),
isDisplayed()));
        appCompatEditText8.perform(replaceText("123456"), closeSoftKeyboard());

        Espresso.pressBack();
        //pressBack();


       //click login and wait

        ViewInteraction appCompatButton = onView(
allOf(withId(R.id.btnLogin), withText("Log in"),
childAtPosition(
childAtPosition(
withId(android.R.id.content),
0),
3),
isDisplayed()));
        appCompatButton.perform(click());
        
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
try {
 Thread.sleep(7000);
 } catch (InterruptedException e) {
 e.printStackTrace();
 }

        //test during a group UI

        ViewInteraction appCompatButton2 = onView(
allOf(withId(R.id.btnGroupJoin), withText("JOIN A GROUP"),
childAtPosition(
allOf(withId(R.id.container),
childAtPosition(
withId(android.R.id.content),
0)),
2),
isDisplayed()));
        appCompatButton2.perform(click());
        
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
try {
 Thread.sleep(7000);
 } catch (InterruptedException e) {
 e.printStackTrace();
 }

        //try during a group

        ViewInteraction appCompatButton3 = onView(
allOf(withId(R.id.btnJoin), withText("Join"),
childAtPosition(
withParent(withId(R.id.listAvailableGroups)),
2),
isDisplayed()));
        appCompatButton3.perform(click());


        //test viewing the groups available
        ViewInteraction appCompatButton4 = onView(
allOf(withId(R.id.btnGroupView), withText("VIEW YOUR GROUPS"),
childAtPosition(
allOf(withId(R.id.container),
childAtPosition(
withId(android.R.id.content),
0)),
3),
isDisplayed()));
        appCompatButton4.perform(click());
        

try {
 Thread.sleep(7000);
 } catch (InterruptedException e) {
 e.printStackTrace();
 }
        
        Espresso.pressBack();
        

try {
 Thread.sleep(7000);
 } catch (InterruptedException e) {
 e.printStackTrace();
 }

        //test UI for create group
        ViewInteraction appCompatButton5 = onView(
allOf(withId(R.id.btnGroupCreate), withText("CREATE A GROUP"),
childAtPosition(
allOf(withId(R.id.container),
childAtPosition(
withId(android.R.id.content),
0)),
1),
isDisplayed()));
        appCompatButton5.perform(click());
        
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
try {
 Thread.sleep(7000);
 } catch (InterruptedException e) {
 e.printStackTrace();
 }
        
        Espresso.pressBack();
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
                        && view.equals(((ViewGroup)parent).getChildAt(position));
            }
        };
    }
    }
