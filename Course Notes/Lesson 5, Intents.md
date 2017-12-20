# *Intents*

### *When you know the destination activity*

#### *EXPLICIT intent:*

*Inside the parent activity*

```java
Intent intent = new Intent(context, destinationActivity);  
intent.putExtras(Intent.EXTRA_TEXT, "Text I Want To Pass");  
startActivity(intent);
```

*Inside the destinationActivity:*

```java
Intent intentStartedThisActivity = getIntent();
if(intentStartedThisActivity.hasExtras(Intent.EXTRA_TEXT)){
    String msg = intentStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
}
```

NOTE: extras can be whichever extra you need.

### *When you don’t know the destination, or you don’t care about it, you just need an action to be done*

#### *IMPLICIT intent*

```java
Uri uri = Uri.parse(uriInString);  
Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
if(intent.resolveActivity(getPackageManager()) != null){
    startActivity(intent);
}
```

##### *Share intent*

We use ShareCompat and its an inner class IntentBuilder, opens a chooser dialog on the bottom of the screen showing all available apps on the mobile, able to handle this type of intent.

```java
private void shareText(String s){  
    String mineType = "text/plain";  
    ShareCompat.IntentBuilder.from(this)  
        .setChooserTitle("Select an app")  
        .setType(mineType)  
        .setText(s)  
        .startChooser();  
}
```

### *Return result to the parent activity*

#### *On parent activity:*  
1. Create your intent as usual, but instead of using startActivity() use:

```java
startActivityForResult(yourIntent, intIndicatingTheRequestCode);
```

2. Override *onActivityResult* method

```java
public void onActivityResult(int requestCode, int resultCode, Intent intent)
{
    super.onActivityResult(requestCode, resultCode, intent);
    if (requestCode == intIndicatingTheRequestCode) {
        if(resultCode == RESULT_OK) {
            //do whatever you want with your returned result
            If(intent.hasExtras(Intent.EXTRA_TEXT)){  
                String msg = intent.getStringExtra(Intent.EXTRA_TEXT);
            }
        }
    }
}
```

#### *On child activity:*

Simple override *onBackPressed()* method

```java
Intent intent = new Intent();  
// if your result is string for example  
intent.putExtras(Intent.EXTRA_TEXT, result);  
setResult(RESULT_OK, intent);  
finish();
```



## Intent Filters 

(thanks _dagger_ , who suggested to add this section)

Intent filters, indicate that the associated app's component has the ability to execute a specific action, thus accept a spesific implicit intent. 

It is important to define intent filters on your **Manifest** file. And use different intent filters for each specific action. You **define them using the intent-filter** tag, inside of the associated app component, e.g 
```java
<activity android:name="MainActivity">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```
The above example, is sth used regularly, and shows that we defined an intent-filter to our MainActivity ( = associated component). Inside the intent filter tag we use more tags to provide info about the filter, which indicates the intent type related. Above we specify the action and the category of the intent filter, that are Main and Launcher respectively. 

Lets see now, which **tags we can use inside the intent filter tag**.
- the **action** tag. 
Declares the intent action accepted, in the name attribute. The value must be the literal string value of an action.
- the **data** tag.
Declares the type of data accepted, using one or more attributes that specify various aspects of the data URI (scheme, host, port, path) and MIME type.
- the **category** tag.
Declares the intent category accepted, in the name attribute. The value must be the literal string value of an action.

Here it's important to make the following notes:

- **note (1)**: you can use more than one instance of the above tags within your intent filter. In that way you define that your app component associated with this intent filter can handle any and all combinations of those filter elements.
- **note (2)**: If you want to handle multiple kinds of intents, but only in specific combinations of action, data, and category type, then you need to create multiple intent filters, and use only one instance of triple group tag (action data category) in each of them.
- **note (3)** in order to actually receive implicit intents, you must include the CATEGORY_DEFAULT category in the intent filter. That's because the methods startActivity() and startActivityForResult() treat all intents as if they declared the CATEGORY_DEFAULT category. So if you do not declare this category in your intent filter, no implicit intents will resolve to your activity.

Here is an example to understand the above:
```java
<activity android:name="ShareActivity">
    <intent-filter>
        <action android:name="android.intent.action.SEND"/>
        <category android:name="android.intent.category.DEFAULT"/>
        <data android:mimeType="text/plain"/>
    </intent-filter>
    <intent-filter>
        <action android:name="android.intent.action.SEND"/>
        <action android:name="android.intent.action.SEND_MULTIPLE"/>
        <category android:name="android.intent.category.DEFAULT"/>
        <data android:mimeType="application/vnd.google.panorama360+jpg"/>
        <data android:mimeType="image/*"/>
        <data android:mimeType="video/*"/>
    </intent-filter>
</activity>
```
In the example above, we have defined 2 intent filter associated with the SharedActivity. 
- At the first, we have used one instance of all of 3 tags we can use within an intent filter. And defined that our activity is going to accept intents with the action SEND if the data used is plain text.
- At the second, we have used one all of 3 tags we can use within an intent filter, but this time, we used multiple instances of the name and the data tag. In that way we define that our activity is going to accept intents with **any of the combinations** created using as **action** either *SEND* or *SEND_MULTIPLE*, and using as **data** either of the mimeTypes mentioned. 

Defining **intent filters is really important** cause thanks to them, the the system will deliver an inplicit intent to your app component, only if this intent is one of the intents you have defined that the component can receive - meaning if this intent pass through at least one of your component's intent filters. 

*But how this "pass" test is working?*

Each time an implicit intent is being created, the system tries to find a component to send the intent. To do this, it tests it against every intent filter by comparing the intent to each of the three tags - elements (meaning action, data, category). To be delivered to the component, the intent must pass all three tests. Otherwise, the Android system won't deliver the intent to the component. *However*, since we can define more than one intent filter to each component, an intent may fail to pass on of the intent filters, but pass another intent filter of the component. If so, the component will recieve the intent.

Closing this section, we have to **mention** that, if you don't want your app component to be started by any other app but yours, then you should not use intent filter and impicit intents to start your component, but use explicit intents. Also set the *exported* tag within your component's tag to *false*. 