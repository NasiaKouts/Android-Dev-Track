# Background Tasks

## Service
Any network transaction, that "talks" to a db, shouldn't be done within an activity, since it has nothing to do with the UI.
For this reason, we have **Services**, which is another component of the Android Framework, used to run any background task that doesn't need visual component, thus a service doesn't provide any UI. 

### Register your service within Manifest file
```java
<service
    android:name=".yourService"
    android:exported="false"/>
    /* exported attribute is similar to the one on CP, and shows   * if other apps can use this service
     */
```

### How do you start a Service?
*note that since all components of Android Framework, are created in the main thread, you still need to creat a background thread withing your service*

1. Manually start a service   
    - simply call *startService(anIntent)* method
    - the service's lifecycle begins when the above method is called. Then the service's *onCreate()* method is triggered, followed by the *onStartCommand()* method, in which you should code what the service should do. When the task you wanna do, is completed, simply call *stopSelf()* method to send the corresponding signal, and in that way the *onDestroy()* method will be triggered.   
    - **Note** the service doesn't communicate back the the component started it

2. Schedule a service    
    If you want to start a service at a specific time in the future, or when some conditions are met, use a JobService.
    - Create your JobService
    - Define when it should start, using a Scheduler. You can define a complex schedule, and the scheduler will do the work for you.
3. Bind to a service   
    - Offers a client (component binded to the service) - server (the service itself) interface    
    - To bind a component to a service simply call *bindService()* method
    - **Note** in this way, the service and the binded to it components, can communicate back and forth
    More: https://developer.android.com/guide/components/bound-services.html

---
### Intent Service
**Note** there is also **IntentService**, which is a service that runs completly on a seperate background thread, in contrast with the service class above. Here are the steps required to **create an IntentService class** yourself.
- Set as your class's superclass the IntentService
- Within your class override the *onHandleIntent()* method, in which you should code what you want your service to do on the background.   

**In order to call it**, you simply create an intent in which you pass your intentService class as a parameter, and then call the *startService()* method, passing in the intent you just created.

**Template**
```java
public class YourIntentService extends IntentService{
    @Override
    protected void onHandleIntent(Intent intent){
        /* Note everything in here runs on 
         * a seperate background thread!
         *
         * Code here what task this service should do.
         *
         * You can also have if or switch statement, 
         * and according to the data the was passed via intent,
         * to perform a different task.
         */
    }
}

// within the component that you want to start this service
Intent yourIntent = new Intent(this, YourIntentService.class);
startService(yourIntent);
```


---
### Deference between service and loader
| Service                         | Loader                       |
| :-----------------------------: |:----------------------------:|
| no connection w/ the activity's lifecycle | tied to activity's lifecycle |
| no "connection" with the UI     | easier UI updates            |

*So when should you use a service and when a loader?* Here are some **Tips**
1. If the info that are about to be loaded, are going to use only by a specific activity -- **Loader**
2. If the background task has no "connection" with the Ui -- **Service**
-----


## Broadcast Receiver

## Jobs
The way android handles background tasks.

## Notifications
In order to notify user when something important happens while your app runs on background, you need to send notification to the him/her.    

Notifications nowdays:
- can contain intents, thus they can be performed on data contained in the notification.
- the name of the app created them is always shown.
- the user can give an inline reply.
- each notification is part of a notification channel. (Android 0reo and above) You must create different notification channel for different notification types, in order to avoid user disabling all your app's notifications. In that way the user can select which types / categories he wants to disable. You must always create notification channels, and for each notification you create you have to set its notification channel, otherwise your notification will never show up. **Note** you can also create groups.
```java
NotificationManager mNotificationManager = (NotificationManager) 
                        getSystemService(Context.NOTIFICATION_SERVICE);

if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
    // ----- CREATE THE CHANNEL -------
    /* importanceInt can take the following values:
     * IMPORTANCE_UNSPECIFIED
     * IMPORTANCE_NONE 
     * IMPORTANCE_MIN 
     * IMPORTANCE_LOW 
     * IMPORTANCE_DEFAULT
     * IMPORTANCE_HIGH
     */
    NotificationChannel yourChannel = new NotificationChannel
                        (uniqueStrId, strCategoryName, importanceInt);

    // here you can edit the default setting of the channel. 
    // Like color, vibration etc.

    // And finally you submit the channel you just created,
    // to the channel manager.
    mNotificationManager.createNotificationChannel(yourChannel);
}


NotificationCompat.Builder nBuilder = new 
            NotificationCompat.Builder(context, yourChannelId)          
                        // Various setters here

                        // Note that the .addAction(notifAction)
                        // can be called up to 3 times.

                        // In the end you should use the
                        // setAutoCancel, if you want your
                        // notification to disappear after
                        // the user clicks on it.
                        .setAutoCancel(true);

// notification id can be used in order to cancel the notification

// notify the notification manager
notificationManager.notify(notificationId, nBuilder.build());
```

Read more:    
https://material.io/guidelines/patterns/notifications.html
https://developer.android.com/guide/topics/ui/notifiers/notifications.html

Notification Samples   
https://github.com/googlesamples/android-NotificationChannels


### Pending Intents
Each notification is showned due to a System service, called NotificationManager. If you want, when the user clicks your notification, the associated activity of you app to open up, you need to give NotificationManager the permission to launch the activity. To do so, you need pending intent, which is a wrapper of a regular intent, and is designed to be used by other application, in order to perform the associated action as if you were the one doing it.   

You can create pending intents by any of the following static methods of PendingIntent class, depending the type of intent:
- getActivity(Context, int, Intent, int)
- getActivities(Context, int, Intent[], int)
- getBroadcast(Context, int, Intent, int)
- getService(Context, int, Intent, int)  

The parameters are the same for all four of them.
- context the context where the component should be started in
- int unique code that identifies the pending intent, in order to cancel it if needed
- the intent we wanna pass to the other application, in order to lauch it
- flag option, in order to create multiple PendingIntents for the same intent

**Note** if you make your notification open up a component of your app, make sure to modify your manifest file, and add launchMode="singleTop" in order to avoid relaunching if the app is currently running already. This helps improve the performance, cause it will just bring the up back on the foreground instead of creating a new launch.

#### Side notes

**Foreground Service**, service that the user is constnatly aware of, and appears as a notification that cannot be dismissed. The user may do some fundamental interaction with your app. We usually use this kind of services, in order to show to the user the real time progress of a time consuming task.

check this and learn more about the plurals mechanism, in order to let your app choose which string option should select to return when string plurals is used. https://developer.android.com/guide/topics/resources/string-resource.html#Plurals   

check badges documentation https://developer.android.com/guide/topics/ui/notifiers/notifications.html#Badges   

check this notification code lab     
https://codelabs.developers.google.com/codelabs/notification-channels-java/index.html#0

more about notification's images    
https://developer.android.com/training/notify-user/expanded.html
https://developer.android.com/reference/android/app/Notification.BigPictureStyle.html