# Preferences & Settings

**Why do we need them? And why to use them?** 

Well it is always a plus to give our users the opportunity to personalize a little bit the app themselves. That way the connect more and the probably are going to love this more if they add their personal style in it. So in any case we should provide a settings screen to our app, in order to give the users the ability to modify some parameters of our application. But remember, don't overdo it. Don't present many options and many settings, cause that may confuse the users. Keep it simple. _E.g. different units_

---

**But how do we create them? And how do we update our app in order to change everytime a preference has changed?**

_Android provides us with tools in order to save data and create starting screen of the app. But before jumping into preferences lets have a summary and a short explanation of the different ways that we can achieve data persistence on android._

---
## Data Persistence

By data persistence we refer to the act of saving some data.

There are _5 different ways_ to achieve this:

1. _**SaveInstanceState**_ method: It uses bundle, thus save data in **key-value (complex)** mechanism. We save the state of the views we desire to save. Used in activity's recreation cases, and it's just a **temporary save**. _That means that if the user e.g. restarts the device or even close the app the data saved using this way, will be lost._ So we use this only if the user still uses the app actively.
2. **Shared preferences** clause: Data is being saved again as **key-value pairs (primitive)**, but this time into a specified file. In that way **data is persisted** even between device's and app's restarts. _**Note:**_ keys here are always _strings_, while the values are _primitives types_. Used when you need to save a single string or numerical value about the user, e.g. his name in a game app, other user's preferences and app settings. *
3. **SQL lite**: Relational database. The data we want to save, in contrast with the previous option, is **complex**. And _using key-value pairs would create a mess_. The number of info needed to save may change while the user uses the app, and each data has complex sub-data needed to be saved as well. Like in the previous option, **data is persisted** even between device's and app's restarts.
4. Save files using **Internal storage** (devices hard drive) & using **external storage** (e.g. memory card): Used to save  multimedia and large amounts of text. Like in the 2 previous options, **data is persisted** even between device's and app's restarts.
5. **Database on server or Service** (e.g. Google's Firebase): data has to be _accessed by multiple devices_. That's why we store the data in the cloud instead of on the device like we do when using the previous options. Once again, **data is persisted** even between device's and app's restarts.

**To sum up**, I will put a screenshot from the associated lesson below:

![image1|690x323](upload://34xPKEOFPL7GRZIa96ImNyz8AzL.jpg)

_A quick note about Shared Preferences option._

 In order to use Shared preferences, you also need a PreferenceFragment, which is part of the android framework, and it is responsible for creating a UI for the settings activities. This populates itself with preferences define in an XML file. This XML file has as a root tag (outer tag) the <PreferenceScreen>. If the user makes a change on the settings, the value of the associated key-value pair automatically gets changed. So in reality, in a very abstract explanation, in your code you just have to get notified whenever such change occurs, and then be able to identify the change and reset - update the activity associated with the change.

_Here I will leave another screenshot of the lesson, just as a reminder of the visual example during the lesson, in order to remember and understand the idea to the fullest._

![image2|690x331](upload://pvZcYS9hNVQ2hHE2J5b2M4G0g8q.png)

---
* Fragment: class that represents a modular and reusable piece of an Activity. Remember the example of using 2 fragments, on mobile each fragment fills up the whole screen at one time, however on tablet, since its screen is larger, you may make those 2 fragments appear at the same time on the screen.
* Activity: a class responsible for a single focused thing that the user can do.

---

_So now that we talked about data persistence, lets go back to preferences._

## STEPS in order to use preferences in your app!
### GUIDE (following lesson's instructions)

1. **Create an activity named _SettingsActivity_.**

    * This will extend  _AppCompatActivity_.
    * In order to navigate to this activity, you need to create a menu, with a settings button within it and use it in the activity (lets call it Activity1) that you want to navigate from to the SettingsActivity. When the user presses this button – menu item – the settingsActivity should appear on screen. 
    * Also, don't forget to set the navigation from your Settings activity, back to the one that "opened" it. Use _Up Navigation_.
      1. In order to do that, just modify your _manifest_ file, in order to implement Up navigation. You should set your SettingsActivity's parent the Activity1.
      
          **Note:** According to which android versions your app supports you may need to use only the _android:parentActivityName_ attribute (if your app supports Android 4.1 and later). Otherwise if your app also supports Android 4.0 and lower then you also need to add the following _meta-data_:

                  
              <meta-data
                  android:name="android.support.PARENT\_ACTIVITY"
                  android:value="com.example.yourApp.Activity1"/>

      2. Also allow the Up navigation by adding the following to your settingsActivity's _onCreate_ method:

          ```java
          if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
          }
          ```

          This actually, creates a menu item on the left side of the action bar, and when the user press this menu item - button the _onOpntionsItemSelected_ is being called, with this menu item as parameter, which has _android.R.id.home_ as its id value.

      3. Finally, _override_ the _onOptionsItemSelected_ method, and check if the user presses the home button, to do the following: 
          ```java 
          NavUtils.navigateUpFromSameTask(this)
          ``` 
          Using this method, you navigate the user up, since the method finishes the current activity appearing on screen and starts (or resumes) the appropriate parent activity.

    _INFO_: Set the launch mode of the parent activity (avtivity1) as _singleTop_.
    **Note** : by doing this when the user navigate back from the settings activity to the parent activity, the parent activity won't be remade.
    _I will place here an explanation from android documentation (same link used in classroom), just for extra clarificasion between the differences of using as launch mode "singleTop" or the default mode "standar"._

    - If the parent activity has launch mode  [&lt;singleTop&gt;](https://developer.android.com/guide/topics/manifest/activity-element.html#lmode), or the up intent contains  [FLAG\_ACTIVITY\_CLEAR\_TOP](https://developer.android.com/reference/android/content/Intent.html#FLAG_ACTIVITY_CLEAR_TOP), the parent activity is brought to the top of the stack, and receives the intent through its  [onNewIntent()](https://developer.android.com/reference/android/app/Activity.html#onNewIntent(android.content.Intent)) method.
    - If the parent activity has launch mode  [&lt;standard&gt;](https://developer.android.com/guide/topics/manifest/activity-element.html#lmode), and the up intent does not contain  [FLAG\_ACTIVITY\_CLEAR\_TOP](https://developer.android.com/reference/android/content/Intent.html#FLAG_ACTIVITY_CLEAR_TOP), the parent activity is popped off the stack, and a new instance of that activity is created on top of the stack to receive the intent.

    Found on: https://developer.android.com/training/implementing-navigation/ancestral.html

    **GENERAL INFO** : _as you can see if you open the link provided above, at the end of the topic, there is a section mentioning that if you are creating an Up navigation in a general activity, and your activity provides any intent filters that allow other apps to start it, you should check if another app launched the activity instead of your own. In case another app launched it, you should make sure that your app creates new task with the appropriate back stack before navigating up, like it would normally did if your own app had launched the activity. Check the link above for the associated code. :D_

    **More about launch mode check out this: https://inthecheesefactory.com/blog/understand-android-activity-launchmode/en**

2. **Add a _preference fragment_ in this activity.**

    * You need to add the accordingly dependency on _build.gradle(Module: app)_ file.
      ```
      'com.android.support:preference-v7:25.1.0'
      ```
    * Create a _XML preference_ file, which will define what the Fragment will include in it.
    **Note:** this file should be in _res > xml dir_. Also note that every XML preference file, has as an outer tag the _&lt;PreferenceScreen&gt; &lt;/PreferneceScreen&gt;_ tag. Inside the &lt;PreferenceScreen&gt; tag we add whichever preference tag we want, and add the attributes we need, setting their default values. Usually, we need label, default value, and a key.

      _Side notes: When we use nested &lt;PreferenceScreen&gt; tags, we call it "_ **Nested Hierarchy of Preferences** _". By this, we refer to the fact that when you open the Settings Activity, if you click on a specific element, a new screen with more settings associated with the element selected appears.
      Another "grouping" of settings can be achieved by using the &lt;PreferenceCategory&gt; tag. By doing this, a divider with a heading, in which the value of the title attribute inside the **&lt;PreferenceCategory&gt;** tag will appear. And each group of preference objects inside this &lt;PreferenceCategory&gt; tag will appear below this divider. So if you use multiple &lt;PreferenceCategory&gt; tags, you will have your settings grouped by dividers, and each devider will "contain" bellow it the children preferences of the associated &lt;PreferenceCategory&gt;._

    * Create a new class and give it an appropriate name. _E.g. SettingsFragment_. This class extends the _PreferenceFragmentCompat_ class.
    Whithin its *onCreatePreferences* method, call the 
      ```java
      addPreferencesFromResource(R.xml.your\_file)
      ```
      , in order to fill the fragment using this xml preference file you created during the previous step.
    * Finally, in order to add the newly created fragment into the SettingsActivity, you just have to replace everything in your _activity_settings.xml_ file with a fragment tag, which you will "connect" with your SettingsFragment, using the name attribute.

    **Note**: Since our fragment, extends the PreferenceFragmentCompat, we have to add a preference theme to our styles. Otherwise, trying to access the settings from your app, will cause it to crush.

3. **Implement a "response" whenever the user changes his preferences. ("Refresh" the UI accordingly)**

    - Create a new method in which you are going to handle _Shared Preferences_, and call it within the _onCreate_ method of your Activity1 – parent activity of the SettingsActivity). The method will include the 2 following steps:
      1. Most times you use _PreferenceManager.getDefaultSharedPreferences_ method in order to get a _SharedPreference_ object. Used when you define only one preference file. 
          
          For other cases check: [https://developer.android.com/reference/android/preference/PreferenceManager.html](https://developer.android.com/reference/android/preference/PreferenceManager.html) 
      2. Then by this _SharedPrefernce_ object you call the _Get_ method that is suitable to the type of data we want to retrieve. Each get method takes _2 parameters_. The first one is the Key of the Key-Value stored pair. The second one is the default value of this Key. 
          
          **Note:** there is no type check, so if you use the method which returns different type of data than the true type of data there may occur an error.
    - Have your Activity1 – parent activity of the settings – _implement_ the _onSharedPrefernceChangeListener interface_. Because of this implementation, you also need to implement the _onSharedPreferenceChange(SharedPreferenceObject, Key)_ method.
    - Register the listener to your _sharedPreferenceObject_, the one that you want to trigger change. In order to this, simple go to your _onCreate_ method within your Activity1 (alternatively go to method called by onCreate), and after the line where you created the _sharedReferenceObject_, lets call it _sharedPref_, use this call: 
      ```java
      sharedPref.registerOnSharedPreferenceChangeListener(listenerObject)
      ```
    - When the Activity1 gets destroy you want to release - unregister your listener, to avoid memory leaks. So simply add this inside its onDestroy method.
        ```java
        sharedPref.unregisterOnSharedPreferenceChangeListener(listenerObject)
        ```

    By doing the above, whenever the user changes a setting – preference, the _onSharedPreferenceChange_ method will be triggered. This method is responsible for updating the UI, according to the current user's preferences.

    **Note:** since the Activity1 implements the needed listener, you can pass as a parameter this, at register/unregister onSharedPreferenceChangeListener call.


4. **Add _preference summaries_** 

    Informs the user what are the results if _e.g the checkbox is checked or unchecked_. 
    * If you have a static summary: Add _android:summaryOff_ and _android:summaryOn_ attributes to the preference's tag in xml.
    * If not, and the summary is more related to the user's input you can update the settings summary by your settings fragment. The idea of this is similar to the one used to have your UI updated each time a preference changed.
      * Again your class will _implement_ the _onSharedPreferenceChangeListener_ class, consequently you will _override_ the _onSharedPreferenceChange_ method, and you will _register / unregister the listener_. 
      * The difference is on how you get the SharedReference object and how you get the desired preference. **First**, in order to get the _SharedPreference_ object you have to use the following:
        ```java
        SharedPreferences sharedPrefObject = getPreferenceScreen().getSharedPreferences();
        ```
      call instead of the one used when trying to access the SharedPreference object within the parent activity of the SettingsActivity. **Second** you will also need to know how many preferences there are in your preference screen, in order to iterate through them, to be able to change the values of the ones you want to. To do that, you use:
      ```java
      int numOfPrefernces = getPreferenceScreen().getPreferenceCount();
      ```
      In order to iterate to those preferences, you just do a loop like you would do if you had a simple int array. And inside you just get the preference using the following:
      ```java 
      Preference prefObject = getPreferenceScreen().getPreference(indexOfLoop)'
      ```
      **Third**, in order to respond correspondingly to the preference each time, use the java operator _instanceof_. **Lastly** Simple use setSummary on the prefObject to set the desired summary.

    **Note:** as mention during the lesson, in our example we wanted to change the summary of the _listpreference_. ListPreference has _array of labels_ and _array of values_. Those arrays should be **parallel**. By parallel I mean, that if your label in the first cell of your array is “Desired color is red”, then the first cell of the values’ array has to contain the red color value. This is needed not only because the listPreferences works that way automatically when the UI gets updated, so if your arrays don’t follow this rule the wrong color will appear. It is also needed in order to be able to set the summary dynamically like we describe above. In your code, at the place where you update the color, you will do sth like the following:
    ```java
    listPrefObject.setSummary(
          listPrefObject.getEntries()[ 
            listPrefObject.findIndexOfValue(
              sharedPrefObject.getString(listPrefObject.getKey(), “”)]
    ```
      The above part of code obviously lacks of checkings, but its just a reminder of the general idea.
      For anyone who may is confused, I will try to explain it the way I see it. You first get the value that is currently selected on the list preference. Then you find the place – index of this value into the values’ array. And then using this index you get the corresponding label from the labels’ array and set it as a summary. Hence, this is why your arrays (labels and values) must be “parallel”. Otherwise, your summary would show the wrong label.

---

## Valid input check

Don't forget to check if the input the user gave is valid. By this, I mean that if you have a EditText, where you want your user for example sets his age, then you should obviously check if the user gave you a number, and that number is larger than a certain one, and obviously larger than at least 5 (?).

In order to do this check the link bellow, it's from the classroom lesson. _I decided not to write anything about this on my own, cause it's already written, and not a video, and pretty clear I think, so I wouldn't have anything to add or offer more. But still if anyone is confused I would be happy to help if I can. So don't hesitate to ask_ :D

https://classroom.udacity.com/courses/ud851-emea/lessons/1392b674-18b6-4636-b36b-da7d37a319e3/concepts/7156d056-641e-491c-9b86-49f5310de0b0


---

_**In general**_ keep your code cleaner and more maintainable by using **resources**. Instead of literals create constants on XML files and use those values. You can access them using _@string/name\_of\_your\_string_ within an XML file, and using _R.string.name\_of\_your\_string_ from java. **Note** that the last one returns int value, so if you need the string you need to us getString(R.string.name\_of\_your\_string). In the same idea, if you need the Boolean value of a bool constant declared on XML, you need to use getResources().getBoolean(R.bool.name\_of\_your\_bool).

You can read more about setings material here https://material.io/guidelines/patterns/settings.html#