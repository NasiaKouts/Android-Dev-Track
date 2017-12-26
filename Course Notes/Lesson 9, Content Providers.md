# Content Providers
**Content providers**, provides us an easy way to access from or write data to the user’s _contacts, documents and calendar_. In fact Content Provider is a class, that is “between” an app and its data source, with which all data requests can be made, and its responsible for providing to apps an easy way to “communicate” with the data source. Its a black box mechanism. In order to store data, you just pass them to the Content Provider associated with the spesific data source. You don't need to know how that CP deals with it, nor what data source type is being used. So it makes your life easier, and saves you plenty of time, since you only need to modify few lines of code and few permissions.

_Here we should **mention** that, usually data sources are private, and can be used (accessed) only by the app that created them. That’s happening mainly for security purposes._

## Why should we use this “in between” class? 
For the following reasons: 
- **Make your app's data source accessible by other developers**, but in the same time keeping your data source security safe, since you create your own personalized Content Provider and in that way you can protect your day in any way you want to.

- It provides an extra level of **abstraction**.

    Lets take a minute and think of java in general. If you use a method in your code, lets say _method1_, then you don't care about the implementation. You just wanna know its input parameters and what the method does in an abstract level, and what it returns. So if another programmer change the implementation, but keep the method's signature the same, you don't need to know about the change.

    In the same way, the Content Providers makes it possible to _change the data source type, without the need to change the apps_ using this data source. Since the "signature", meaning the Content Provider stays the same.

- In order to **use Android Classes, that use Content Providers**, such as Loaders.

---

Take a momment and remember the example mention during the lesson. If you want your app to take a picture of an image that contains contanct info, and after processing you want your app to save the contact info. Then you should save the contact info to the same source that all contacts info used by the _contact app_ are stored. (Otherwise, if you store the info in a separate database that you created, then the default contact app wouldn't be able to access this data.) But as we mentioned above, each app's data source is private. Thats why content providers are essentials.

---

## Access Data using CP
Steps in order to use CP
1. Add the associated **permission**.

    Each provider's app has specified permissions, that other apps have to have in order to access the data. That's happening in order to make the other app request the permissiom by adding the _uses permission_ element in your _manifest_ file. In that way, the user will be informed, when installing the app, about the data that it will may access. Then the user will decide if he or she will implicity grand the request or not. 
    This is all happening because a CP ensures security safety.
    - To retrieve data, your app needs _read access permission_ for the provider.
    - To write data, your app needs _write access permission_ for the provider.

    **Note**: if there are no permissions specified by the provider, then any other app has no access to the data.

    **Note 2**: To find the exact name of the access permissions used by the provider, look in the provider's documentation

    #### Code Template
    ```java
    <uses-permission android:name=permissionName>
    ```
2. Get the **Content Resolver**.

    Content Resolver is a class between your app and the CP. Without CR you cannot use a CP. So you have to get the system's CR within your app's context, in order to communicate with the CP as a client. 
    
    That means that, in your code you have one CPObject and one CRObject, that are object of a class that implements CP and a CR object respectively. Those objects communicate, the CPObject recieves data requests from its clients (in our case, the CRObject), then the CPObjects execute the requesteds action, and finally it returns the result to the corresponding client.

    _**But why do we need a Content Resolver?**_

    In each user's device, there are multiple CPs and multiple running apps. So there may be more than one app that wants to use a specific CP in parallel. So if there was no Content Resolver, then there would be a mess. (Imagine in C, if you have 2 threads, and the one reads from a file and the other writes to that file. Yous should have some kind of "organizing" the access) 
    
    Thus, a Content Resolver is nessecary! It manages the whole "communication" between CP and apps that want to access its data, and at the same time keeps all the data in sync. Like **CP is a middleman between an app and a data source**, in the same way **CR is an intermediary between an app and the CP or CPs**.

    So CP handles inter-process communication and keeps everything in sync and running smoothly.

    #### Code Template
    ```java
    ContentResolver cr = getContentResolver();
    ```
3. **Pick one of four basic actions** on the data: 
    - **query**: read from the data
    - **insert**: add a row / rows to the data
    - **update**: update the data
    - **delete**: delete a row / rows from the data.

    ---
    
    Lets take a short look to the above methods and their params.
    - **Query**
    
        ```java
        Cursor query (Uri uri, 
                String[] projection, 
                String selection, 
                String[] selectionArgs, 
                String sortOrder)

        /* where:
         * 1. URI uses content:// prefix 
         * (see the section URIS * below) 
         * and should NEVER be null!
         * 
         * 2. projection is the list of columns to be returned.
         * If you pass null, then all the columns of the table
         * in the database will be returned.
         * Make sure to set projection, in order to
         * avoid reading useless data.
         *
         * 3. selection (just like in SQLite),
         * formated as an SQL WHERE clause
         * excluding the WHERE itself.
         * If you pass null,
         * the result will contain all the data rows
         * in the table of the database.
         *
         * 4. selectnArgs: If you include ?s in selection,
         * they will be replaced by the values here.
         *
         * 5. sortOrder (just like in SQLite),
         * formated as an SQL ORDER BY clause
         * excluding the ORDER BY itself.
         * If you pass null,
         * the result will be unsorted. 
         */
        ```
        For overloaded version of this method check: https://goo.gl/iPx34G

    - **Insert**
        ```java
        Uri insert (Uri url, 
                ContentValues values)

        /* where:
         * 1. URI uses content:// prefix 
         * (see the section URIS * below) 
         * and should NEVER be null!
         *
         * 2. values the values of the fields
         * of the new data - new row.
         * Passed as an CV pair,
         * where the key of each entry is the column name.
         * NOTE: passing an empty CV object
         * will create an empty row.
         */
        ```

    - **Update**

        ```java
        int update (Uri uri, 
                ContentValues values, 
                String where, 
                String[] selectionArgs)

        /* where:
        * 1. URI uses content:// prefix 
        * (see the section URIS * below) 
        * and should NEVER be null!
        * 
        * 2. values the values of the fields
        * of the new data.
        * Passed as an CV pair,
        * where the key of each entry is the column name.
        * NOTE: if there is a null value
        * then the old value of the spesific field
        * will be removed.
        *
        * 3. where (just like in SQLite),
        * formated as an SQL WHERE clause
        * excluding the WHERE itself.
        *
        * 4. selectionArgs: If you include ?s in where,
        * they will be replaced by the values here.
        *
        * 5. Returns the number of rows updated.
        */
        ```

    - **Delete**
        ```java
        int delete (Uri url, 
                String where, 
                String[] selectionArgs)

        /* where:
         * 1. URI uses content:// prefix 
         * (see the section URIS * below) 
         * and should NEVER be null!
         *
         * 2. where (just like in SQLite),
         * formated as an SQL WHERE clause
         * excluding the WHERE itself.
         *
         * 3. selectionArgs: If you include ?s in where,
         * they will be replaced by the values here.
         *
         * Returns the number of rows deleted.
         */
        ```
    ---

    In order to pick an action of the above, you simple _call the associated method on the CRObject_. Just like in SQLite, we use Cursor object to store the result, if for example we use query action.

    #### Code template
    ```java
    Cursor c = CRObject.selectedAction(theCorrespondingParams,...);
    ```
4. **Identify the data** you are wanna access **using a URI** - Uniform Resource Identifier


    An URI identifies or give the location of some data in a CP. 
    
    It has the following parts:
    - _**Content Provider Prefix** - scheme: content://_
    - _**Content Authority** - tells the CRObject which CP to use: cpLocation_
    - _**Path** - string that identifies which data in the CP you wanna access, e.g. table name: specificData_

    The CR object parses out the URI's authority, and uses it to find the corresponding provider by comparing the authority to a system table of known providers. The CR can then pass the request action to the correct provider.

    **Note**: The structure of the above path is specific to the CP. Each CP has a Contract class, in which there are URI fields for each case, for example the CONTENT_URI field.

    **Note 2**: URLs are subset of URIs, that identify network locations, with the prefix: http://

    **Note 3**: In many providers you can access a single row in a table by appending an ID value to the end of the URI. E.g., to retrieve a row whose _ID is 4, you can use this content URI: _ContentUris.withAppendedId(UserDictionary.Words.CONTENT_URI,4)_ Usually you use this when you've retrieved a set of rows and then want to update or delete one of them.

    #### Code Template
    ```java
    Cursor c = CRObject.selectedAction(CP_Contract_Class_Name.Table_Name.CONTENT_URI, restCorrespondingParams,...);

    //where URI is: content://CPAuthority/Path
    ```
  
5. In the case of **reading from the ContentProvider**, display the information in the UI.

    Database operations, just like network calls, may take a really long time to be done. So you must avoid having them in your main thread, in order to keep your app from "freezing". Thus, you should use a backgound thread. Do this in any way you desire, like AsyncTask, AsyncTakLoader etc.

    However you want to access the result from your main thread in order to use them, e.g. display the info in the UI.

https://developer.android.com/guide/topics/providers/content-provider-basics.html#Permissions