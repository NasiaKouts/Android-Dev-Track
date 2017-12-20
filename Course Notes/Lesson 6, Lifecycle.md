# Lifecycle

Device has limited resources so we shouldn’t have many apps in the background. We don’t have to deal with killing apps. You are not in control of your app’s lifecycle. Android kills low priority apps, the user hasn’t used for a while, when higher priority apps need their resources.

## Activity Lifecycle

![](/uploads/default/original/4X/0/3/0/03076229e5df24219016fc74e3c33e6bfe74d919.png)

### Active
On foreground and on focus, actively receives input from user events, no other activity obscures it.

### Visible
Partially obscured. Example: pop up menu.

### Background
Fully obscured.  
  
## How to deal with it?

We want efficient use of limited resources, so your app has to use these signals and adjust his resources use. NOTE: OnPause and OnStop are signals that your app can be killed soon. Thus you need to clean up any resources, e.g. close any open connection. Note: when your activity is not active UI update should be paused. But if it is visible you should not pause any processes that are drawing the UI.

*Rotation* of the device causes the activity to be destroyed and then to be recreated, because you may create different layout resources for different device configurations (screen size, pixel density, device orientation). So by default whenever a device configuration changes, the above happens.

When an activity is destroyed, the data into TextViews, and in general the data into any view that gets populated dynamically, are not saved automatically. NOTE: EditText data are preserved automatically by android framework. In order to keep your data saved, to be able to show them again immediately when the activity is recreated, you have to override the *onSaveInstanceState* method. In that way you will be able to persist your app’s views’ data during the whole process of being destroyed and recreated. It’s parameter is a bundle, which is a key-value store mechanism. We use this to store the data we want to be saved. However, it supports limited set of types. If you wish to store a complex object, you first have to have it implement the parselable interface.

#### In order to save the data we do the following:

```java
private static final String MY_KEY = "Data we want to save";  

@Override
protected void onSaveInstanceState(Bundle state){  
    super.onSaveInstanceState(state);  
    // if we wish to store the text of a textview  
    bundle.putString( MY_KEY, myTextView.getText().toString);
}
```

#### In order to restore the data we do the following within *onCreate()* method:

```java
if(saveInstanceState != null){  
    if( saveInstanceState.containsKey(MY_KEY)){  
        myTextView.setText(saveInstanceState.getString(MY_KEY));  
    }
}
```

## Loaders

When the above happens, an activity gets destroyed and recreated, the app still runs, meaning all its threads are still running, and that may lead for a result to be returned to a zombie activity. To solve this we use loaders. Loaders provide a framework to perform asynchronous loading data. They are registered by id with a component called LoaderManager, which allows them to live beyond the lifecycle of the activity that they are associated with, preventing duplicate loads from happening in paraller, like udacitdy's sunshine project before using loader. In that way we now use AsyncTaskLoader instead of AsyncTask. *AsyncTaskLoader* implements the same functionality with AsyncTask but it has different lifecycle since it’s a loader.

Each loader has a unique Id. We get a loader using myLoaderManager.getLoader(myLoaderId). The whole idea is that if the loaderManager returns a loader with the id we requested, we say to the loaderManager to use this loader. However if the loaderManager doesn’t return a loader, this means it hasn’t been created one with this id, then we say to the loaderManager to create the loader. Note: restartLoader will create the loader
if it doesn’t exist, but we use the above idea, to make our code more readable.

### Benefits when using Loader:

* Loaders run on separate threads to prevent janky or unresponsive UI.
* Loaders simplify thread management by providing callback methods when events occur.
* Loaders persist and cache results across configuration changes to prevent duplicate queries.
* Loaders can implement an observer to monitor for changes in the underlying data source. 

You typically initialize a loader within the activity's onCreate() method:

```java
// Prepare the loader.  Either re-connect with an existing one,  
// or start a new one.  
getSupportLoaderManager().initLoader(0, null, this);
```

The *initLoader()* method takes the following parameters:

* A unique ID that identifies the loader. In this example, the ID is 0.
* Optional arguments to supply to the loader at construction (null in this example).
* A LoaderManager.LoaderCallbacks implementation, which the LoaderManager calls to report loader events. In this example, the local class implements the LoaderManager.LoaderCallbacks interface, so it passes a reference to itself, this.

The *initLoader()* call ensures that a loader is initialized and active. It has two possible outcomes:

* If the loader specified by the ID already exists, the last created loader is reused.
* If the loader specified by the ID does *not* exist, the method triggers the LoaderManager.LoaderCallbacks method onCreateLoader(). This is where you implement the code to instantiate and return a new loader. For more discussion, see the section [onCreateLoader](https://developer.android.com/guide/components/loaders.html#onCreateLoader).

### Template

```java
public class MyActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private static final int *UNIQUE_LOADER_ID* = 1;

    private static final String *MY_BUNDLE_KEY* = "randomKey";
    ...
}
```

#### Whithin onCreate method:

```java
getSupportLoaderManager().initLoader(*UNIQUE_LOADER_ID,* myBundle, this);
```

In the place you would have called an async task if you weren’t using loader:

```java
Bundle currentBundle = new Bundle();  
queryBundle.putString(*MY_BUNDLE_KEY*, dataToSave);  
  
LoaderManager loaderManager = getSupportLoaderManager();

Loader<String> githubSearchLoader = loaderManager.getLoader(UNIQUE_LOADER_ID) 
if (githubSearchLoader == null) {  
    loaderManager.initLoader(UNIQUE_LOADER_ID, currentBundle, this);  
} else {  
    loaderManager.restartLoader(currentBundle, currentBundle, this);  
}
```

#### Instantiate and return a new loader for the given ID.

```java
@Override  
public Loader<String> onCreateLoader(int id, final Bundle args) {  
    return new AsyncTaskLoader<String>(this) {
        // similar idea to the preExecute on AsyncTask  
        @Override  
        protected void onStartLoading() {  
        }  
        
        // similar idea to the doinbackground on asyncTask  
        @Override  
        public String loadInBackground() {  
        }  
    };  
}

//similar idea to the onpostexecute on asyncTask  
@Override  
public void onLoadFinished(Loader<String> loader, String data) {  
}

@Override  
public void onLoaderReset(Loader<String> loader) {  
}
```