# Building a Content Provider

## Form of Data - Tabular Data
A table, where each row is an item of the data, and each column is an "attribute" of the data.

## How do we access it?
1. Create a java **class that extends the abstract CP clasS**.
    - In that way the class will be recognized by the system as a valid CP.
    - Implement the _onCreate_ method.    
        Initialize anything needed in order to set up and access the associated data.
    - Implement the CRUD (Create, Replace, Update, Delete) methods needed to access and modify the data.    
    **Note**: each CP has to implement all the following methods: _onCreate_, _insert_, _query_, _update_, _delete_ and _getType_. The last one returns a string that contains the MIME type of the data being accessed.

2. Register the newly created CP to your Manifest file.
    - Just like you do with each new activity you create, you define it into the Manifest file, in order to make sure that the system will recognise it the right way.
    - **Reminder**: a CR can communicate with multiple CPs. In order to identify them, it uses the URI's authority, which is a unique field for each CP.
    ```java
    <provider android:name="fullPackageName.ClassName"
            android:authorities="fullPackageName"
            /* exported attribute indicated if other apps 
             * can access this CP, or not.
             */
            android:exported=["true" | "false"]
            . . . >
        . . .
    </provider>
    ```
    For more info about provider element look here https://developer.android.com/guide/topics/manifest/provider-element.html


3. Define the URIs within the Contract class. 
    - Those URIs identify your CP and all the data types that can return to the user of it.
    - URI gives the following information to the CRs:
        - Specify the associated CP
        - Specify what type of data we are refering to.
    ```java
    <scheme>//<authority>/<path>
    // content://fullPackageName/specificDirectoryOfData
    ```
    **Note**: if we need to access for example a specific row or a specific column of a table data, 
    then we can simple add this info into the URI by modifying the the path and add / and continue with either the item's id or the column name respectively. For cases like this, instead of defining all those deferent URIs, we use a wildcard character.    
    For example:   
    _...path/#_, which means that any number can take the place of the # character.  
    _...path/*_, which means that any string can take the place of the * character.  
    _...path/*/other/#_, which means that any string can take the place of the * character, then it is followed by other and then any number can take the place of the # character.   
    ... **in general** any other combination using the # or/and # wildcard.

    - You need to define those URIs into your Contract class, to make it easier to use them. A good thing is to define as a constant string the scheme and the authority, and then define a constant URI that will be the base URI (scheme and authority). Also add constnat strings of the paths, and finally inside the entry class define each URI.

4. Create a URI matcher.
    - Match a URI pattern to an integer.
    - In that way CP can seperate Uris into groups, and it responses correctly depending on the type of the URI.
    - The CP has to be able to recognise each URI and respond accordingly. To do this, We will use a **URI matcher**, which determines what kind of URI the CP recieved and then match it to an Integer constant.   
        - In order to create your URI matcher, simple declare the final int constants that you are going to use. Number of constants is equals obviously to the number of the different kind of URIs that the CP can recieve.     
        **Note**: as a convention when you define a int constant used for a whole table, then use round numbers. Also, when you define a in constant use for a part of a table, use a corresponding number to the table's round number. E.g. table: 100, table entry: 101.
        - Also declare an UriMatcher as a global variable to your class. 
        ```java
        private static final UriMatcher yourGlobalMathcer;
        ```
        - Create the URI matcher method, e.g. BuildUriMatcher, that is responsible to associate the int constants to the corresponding URI. Whithin this method make sure to create an empty URImatcher first, and then add the matches you desire, meaning the URI formats that is going to recognize and the associated int constants. When you have your Uri Matcher finished, then set it to the global variable you created during the previous step.
        ```java
            UriMatcher yourMatcher = new UriMatcher(UriMatcher.NO_MATCH);
            yourMatcher.addURI(uriAuthority, uriPath, intConstantToBeMatched);
            yourMatcher.addURI(uriAuthority, uriPath + "/#", intConstantToBeMatched);
            yourMatcher.addURI(uriAuthority, uriPath + "/*", intConstantToBeMatched);
            ...
        ```
        **Note**: above you can have any other combination of path and # or / and * symbols.
        **Note2**: don't forget to make use of String constants defined within the Contract class, instead of using hardcoded Strings.   
    - After you create your UriMatcher, you simple use a Switch statement to organise what it needs to be done depending on the URI recieved.
        - Simple use the UriMatch's *match(UriToBeChecked)* method on your UriMatcher object, in order to get the associated (with the passed uri) int constant.
        - You use this returned int constant to your Switch statement.
        - If the returned int doesn't match any of the Switch cases, it means that the given Uri can not be recognized by the Uri Matcher.

## Check the flow bellow
(PHOTO)

## CRUD Methods
All these methos are implemented, similar to the simple SQLite code version. The only difference is that the **CP implements them differently for each accepted Uri**, so different action is happening depending the Uri that is being passed in.    
To handle this situation, we use *URImatcher* and, once again, a *SWITCH statement*.
    1. You first get your reference to the underlying database.
    2. Then you use URImatcher - *yourUriMatcher.match(passedUri)*, to get the corresponding int code, that helps you indicate, which type of Uri was passed in, and what you should do.
    3. Finally, you use the Switch statement, to select which case of int codes (Uri types) is going to be executed.
    4. Inside each case's block you add the code that has to be executed when this type of Uri is being passed.   
Lastly, we simply need to "connect" them with the user's UI, to be triggered depending on how the user communicates with the app. To do this, you simply create a method that is going to be executed, f.e. when the user press a specific "insert" button. Within this method you call the CRUD method you want, using a CR.
```java
getContentResolver().selectedCRUDmethod(Contract.Entry.theUri, yourContentValuesObject);
```

**Notes**
- The UriMatcher's match method, returns -1, if an "unknown" Uri was passed as a parameter.
- Each time a change has occured to the database, you inform the CR in order to update any UI needed, by notifying him. (Note, observer can be null)
    ```java
    getContext().getContentResolver().notifyChange(uri, observer);
    ```
- Within **insert** and **delete** method, you only need to write / delete data to / from the underlying database, so you just need to use *getWritableDatabase()* on your dbHelper object.
- The **insert** method, returns the Uri of the newly inserted data.
- The **delete** method returns the number of rows deleted.
- Within **query** method, you only need to read - retrieve data from the underlying database, so you just need to use *getReadableDatabase()* on your dbHelper object.
- Also, within the **query** method, after "executing" the query on the underlying database, you have to notify the returned cursor, for which Uri it was created for.
    ```java
    yourCursor.setNotificationUri(getContext().getContentResolver(), uriPassedInQueryMethod);
    ```
- If you wanna take one of the Uri's path's parameters, you simply use:
    ```java
    uri.getPathSegments().get(index);
    /* where index start from zero and goes on depending the 
     * number of segments in the path
     */
    ```
- And last one, *finish()* method, informs the system that the current activity is over, in order to inform it that it should return back to the main activity. So you can use this for example after the user press the "insert" button (and you actually insert those data) while he is in a new activity that is responsible to let the user insert a new row of data.

## Load data
The best way to asynchronously load data from any CP is with a CursorLoader, which is a subclass of AsyncTaskLoader. It simply queries a CP, using a CR with a specific Uri, and returns a Cursor of desired data. As any AsyncTaskLoader, CursorLoader runs on a background thread. In that way we don't have any danger of maing the UI frozen while waiting for a response. You can set the CursorLoader monitor the given Uri for any changes in data, in order to update the UI needed automatically.    
The CursorLoader's constructors takes parameters similar to the SQLite query method. But it also has one more argument at the begining, which is the context.


**Side note**
1. In case you want to use where clause in your query, and not simply retrieve all the data - rows. Use selection and selectionArgas parameter. As we mentioned before, this follows the same idea as a regular SQL command, but you have the selection as a string that uses the collumn's name an operator and values. An ? symbol can be used, which indicates that is going to be replaced by the value in the corresponding cell of the selectionArgs's array. By corresponding I mean, the first ? found, is replaced by the value inside the first cell, the second ?, by the value inside the scond cell...
2. In order to insert a large ammount of data into the db, you can use bulkInsert, instead inserting one by one. In this case you have first, to begin a new transaction and in the end set it to successfull or not, and lastly end it. One of the reasons is to make sure that there won't be a way, to have more than one accesses - modifications to the db at the same time, thus no errors because of this will occur.
3. If you need to delete all the data of the table, but you need to return how many rows have been deleted then you should pass '1' ass a selection argument. Otherwise if you pass null, all rows will be deleted but you won't be able to retrieve how many rows have been deleted.