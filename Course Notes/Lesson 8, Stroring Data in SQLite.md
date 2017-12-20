# Storing Data in SQLite

SQLite is a relational database. As all relational databaseσ, the data we store,
is stored in tables.

## SQL Reminder

*Rows*: each row represent a different “object” - entry, and contains all of its
info.
*Columns*: each column represents a certain attribute (which is the title of the
column), and consequently contains all the values of this attribute that appear
into our data.

A table can be thought as an entity of our data. For example, if you had a
program about a local school, maybe one Table was the “Subjects”. Attributes may
have been the subject’s id, title, classes’ taught, book’s id associated with it
etc.

Some of the action you can do in SQLite are:

1. Create, Truncate or Drop(delete) a table.

2. Add or remove a column, or entry.

3. Access the data and modify it.

4. Retrieve (read) the data.

In order to do any of the above, you need to use SQL query.

### 1. Create a table

```sql
CREATE TABLE table_name(
   column1 datatype,
   column2 datatype,
   column3 datatype,
   ....
);
```

Next to each DataType you can add NOT NULL to indicate that this attribute
cannot take NULL as value. You can also add UNIQUE, to indicate that the values’
of this attribute are unique and they are not exist in any other row. Also, you
can add PRIMARY KEY, to only one by table, in order to indicate that this is the
table’s primary key, which is the attribute used to define each row as unique. A
primary key is both NOT NULL and UNIQUE.

### 2. Retrieve data

```sql
SELECT column1, column2, ...
    FROM table_name
[WHERE condition]
[GROUP BY column1]
[HAVING condition2]
[ORDER BY column1]
```

After Select you write the column’s name you want to retrieve their data. If you
want all table’s column you simple use \* . If you don’t want to receive
duplicate values, use DISTINCT after Select and before the columns you want to
receive. The WHERE clause specifies which record(s) should be retrieved. It’s a
condition, either simple either complex. Its form has a columns compared with
hardcoded values or even the values of other column. You can use GROUP BY, when
you need the data returned to be grouped into separate groups. For example check
bellow. Also you can use ORDER BY, in order to get the values returned sorted by
the values of the column you add after the order by.  
HAVING applies a condition to the data that has succeeded the where condition.

Note: When WHERE, GROUP BY and HAVING clause are used together in a SELECT query
with aggregate function, WHERE clause is applied first on individual rows and
only rows which pass the condition is included for creating groups. Once groups
are created, HAVING clause is used to filter groups based
upon condition specified.

### 3. Remove – Delete data

```sql
DELETE
    FROM table_name
WHERE condition;
```

Note: WHERE clause specifies which record(s) that should be deleted. If you omit
the WHERE clause, all records in the table will be deleted. Condition usually
has the following form:
*columnName arithmeticOperator value*
The result is that each row – entry for which the condition is true, gets
deleted.

Although, there are more you can do with those commands, but for now, let’s keep
it simple.

*Check the following example for clarification.*

### 4. Examples

Let's say we have the following table:

| Employees |
|-----------|

| Id | Salary |
|-----------|------|
| Employee1 | 1500 |
| Employee2 | 2000 |
| Employee3 | 1500 |
| Employee4 | 1200 |
| Employee5 | 500  |
| Employee6 | 1500 |
| Employee7 | 2000 |
| Employee8 | 1800 |

```sql
SELECT COUNT(Id), Salary
    FROM Employees
WHERE Salary > 1000
GROUP BY Salary;
```

This is going to have the following result:

| TimesFound | Salary|
|---|------|
| 1 | 1200 |
| 3 | 1500 |
| 1 | 1800 |
| 2 | 2000 |

**Note** that COUNT is an aggregate function that counts how many rows match the
condition. And since we use group by, it counts the rows for each different
salary value. Here are all the aggregate functions:

- AVG – calculates the average of a set of values.

- COUNT – counts rows in a specified table or view.

- MIN – gets the minimum value in a set of values.

- MAX – gets the maximum value in a set of values.

- SUM – calculates the sum of values.

Also if you do the following:

```sql
DELETE
    FROM Employees
WHERE *salary > 1500*;
```

After the deletion the table will look like this:

| Employees |
|-----------|

| Id | Salary |
|-----------|------|
| Employee1 | 1500 |
| Employee3 | 1500 |
| Employee4 | 1200 |
| Employee5 | 500  |
| Employee6 | 1500 |

for more about the main SQL commands used during the lesson check the cheatsheet
provided in classroom <https://goo.gl/ta13is>

*On android, when using an SQLite, there is a file created and stored inside
your android device, representing the database and its data.*

## SQLite Android Implementation

### CONTRACT CLASS

Defines our database design. It is actually a class containing only static
constants that provides info about the database and the names of the tables and
their columns.
We can create for each table a static final inner class that implements the
BaseColumns, and define into their body the name of the table and the name of
the columns as a static final String.
Also note the we can simple create a private default – empty – constructor to
the contract class, since everything inside it, is static, so there is no need
to create an object of the class to access them. We simple access by using the
ContractClassName.neededComponent. Note that BaseColumns interface, contains an
static constant called \_ID, which is going to be the table’s primary key.

#### Code template

```java
public class myContract {
    public static final class TableClass implements BaseColumns {
        public static final String TABLE_NAME = "sampleTableName";
        public static final String COLUMN_SAMPLE_1 = "sampleName";
        \---
    }
}
```

### CREATING THE DATABASE – DBHelper Class extends SQLiteOpenHelper

The SQLiteOpenHelper’s provides 2 significant methods.

- The onCreate method which is responsible for creating the database for the first time.

- The onUpgrade method which is responsible to check if an update is needed, thus if the scheme has changed. If there is, it updates the database, without causing any lost to the user’s data.

DBHelper is responsible to give other parts of the app a reference to the
database in order to be able to execute query’s on the database.

- You need to specify the name of your database as a static constant String inside the DBHelper class. Note that is needs to have the .db postfix.

- Also you need to specify the version of your database, as a static constant int. Each database version starts as 1. Every time you update the database’s scheme, you increase this value by 1. In that way, the users’ database will be updated if the current version is different from their database already created (,when you release a new version of the app, where there has been a database scheme change).

- Override both onCreate and onUpgrade methods of SQLiteOpenHelper. Inside those method use the static constant strings defined inside the Contract class. You do this for the same reason as why you use string resources throughout all your code instead of hardcoded strings.

  - As for onCreate method, you just need to create the string that describes the SQL create table command.  
  **Note 1**: AUTOINCREMENT means that the values of the attribute is going to be generated automatically and increased by one each time a new entry is being inserted into the table.  
  **Note 2**: If you wanna set a default value of the attribute, which means if you don’t give a value while inserting a new entry into the table, it will take this default value, you should simply add next to your attribute’s type the following: DEFAULT yourDefaultValue. After creating the SQL create table string, you simply use yourDatabaseObject.execSQL(yourCreateTableString) in order to execute it.

  - As for onUpgrate method, note that its get called only when the user’s database is older version than the current. Note: if you have version 1, then in this method you can simply just drop the table and create it again from the scratch.

#### Code template

```java
public class *MyDbHelper* extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "*myDbName*.db";

    private static final int DATABASE_VERSION = 1;

    public MyDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " +
            MyContract.TableClass.TABLE_NAME + "(" +\
            MyContract.TableClass._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MyContract.TableClass.COLUMN_SAMPLE_1 + "type additional_info (e.g. TEXT NOT NULL)" +
            ...
            ");";

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        final String SQL_DROP_WAITLIST_TABLE = "DROP TABLE IF EXISTS " +
            MyContract.TableClass.TABLE_NAME;

        sqLiteDatabase.execSQL(SQL_DROP_WAITLIST_TABLE);

        onCreate(sqLiteDatabase);
    }
}
```

### INSERT / RETRIEVE DATA “SETTING UP”

- Create as a member variable of SQLiteDatabase type, to store your database’s reference.

    ```java
    SQLiteDatabase myDb;
    ```

- Create an object of your DBHelper class, within the onCreate method of the class that you want to retrieve data. The “connection” with the database is happening due to DBHelper class. So in order to either add data either retrieve data you need an object of this class. Then store into your SQLiteDatabase variable, already created, the database associated with your app by using calling the getWritableDatabase method on the DBhelper oject created to the previous step. (If you only need to retrieve data and not insert new one, use the getReadableDatabase method instead). So within the onCreate method:

    ```java
    MyDbHelper myDbHelperObject = new MyDbHelper(currentContext);
    myDb = myDbHelperObject.getWritableDatabase();
    ```

### INSERT DATA TO THE DATABASE – NEW ENTRY – ContentValues object

- In order to insert data into your database, you need to use a ContentValues object that is maping – like key-value pairs – a column name with the value to be inserted. So you are going to pass this ContentValues object as a parameter to the insert method, called on your databaseObject.

#### Code Template

```java
ContentValues contentValuesObject = new ContentValues();
contentValuesObject.put(MyContract.TableClass.COLUMN_SAMPLE_1, valueToBeAdded);
...
int id = myDb.insert((MyContract.TableClass.TABLE_NAME, null, contentValuesObject);
```

Lets make few notes about the insert method

- It takes 3 parameters. The **first** one is the table’s name in which we want to insert the new entry. The **second** is a “nullColumnHack string”. It is optional so you can set it to null. But if you decide to use it, you will set it to a table’s column name that null is accepted as a value, in order to set this column value to the new entry null, if your third parameter is empty. And that’s because SQLite doesn’t allow an empty insertion. The **third** is a map of the columns’ name as a key and the associated value of the entry in this column.

- It returns the id of the newly created – inserted entry. In order to be able to identify uniquely.

- **Note:** you don’t need to put into your conentValuesObject a pair for the id of the entry. That’s because it is automatically genereated and increased by 1 every time a new insertion occurs. And this happens because on our “CREATE TABLE” command (see previous section) we declare the \_ID as primary key and autoincrement.

### RETRIEVING DATA FROM THE DATABASE – DBHelper Class

- Run your query in order to retrieve the data needed. The example bellow is not practical, just for the matters of this notes, and it returns the whole table. Note the query’s result is always returned as a table, with columns the projection list of columns and the data associated.

    ```java
    Cursor result = myDb.query(MyContract.TableClass.TABLE_NAME, null, null, null, null, null, null, null);
    ```

    **Lets look query() method in more detail**.

  - Returned type is: Cursor
    Cursor type, is actually a format used to store a SQL query’s result, that provides an easy way to navigate within the returned data. Think of cursor as a pointer to a particular row of the resulted table. So in order to get the info you want to, you simple move the cursor to the row you need, and then extract the value of the desired attribute – column.

    e.g.:
    ```java
    if(result.moveToPosition(rowIndex)){
        result.getString(columnIndex);
    }
    ```

    **Note 1**: above as an example we used *getString*, by assuming that
    the type of data stored to the specific column is string. You use the
    corresponding to the data’s type *get* method.

    **Note 2**: you should always check if the *moveToPosition* doesn’t
    return *false* before trying to call get.. on its result. It returns
    false when it gets out of bounds or when there is not data stored at the
    pointed row.

    **Note 3**: *moveToPosition* takes as parameter an integer, that refers
    to the absolute position you want your cursor to point at. There is also
    a method called *move*, which also takes as parameter an integer, but
    that refers to an offset, meaning a relative movement from the row that
    the cursor points at currently.

    **Note 4**: you can take the columnIndex by calling

    ```java
    result.getColumnIndex(MyContract.TableClass.COLUMN_SAMPLE_1);
    ```

    This returns -1 if there is no column with the given parameter as a name
    to our table – result.
  - Query parameters in order:

    1. **String table**: the name of the table you wanna run the query

    2. **String[] columns**: the list of the table’s columns you want to get their data, called projection list. If you want all the table’s columns ( select \* ), just pass null.

    3. **String selection**: the SQL WHERE clause of your query, excluding the WHERE word itself. If you don’t have any conditions, just pass null.

    4. **String[] selectionArgs**: the parameters used into the where clause. You may include ?s in selection parameter, which will be replaced by the values from selectionArgs, in order that they appear in the selection. Similar to the way printf method works in C.

    5. **String groupBy**: the SQL GROUP BY clause of your query, excluding the GROUP BY words themselves. Its a table’s column by which you want the results to be grouped by. This creates a result of row groups, and you need to use aggregate function. If you don’t want the result to be grouped, just pass null.

    6. **String Having**: the SQL HAVING clause of your query, excluding the HAVING word itself. You can’t have a HAVING clause without GROUP BY clause. So if null is passed as a group by value, then null have to be passed as a having value as well. If you have group by clause, then having is a condition that selects which row groups created using group by will included to the query’s result. If you want all the row groups created to be included, just pass null.

    7. **String orderBy**: the SQL ORDER BY clause of your query, excluding the ORDER BY words themselves. You use it in order to sort the results, and you pass the table’s column by which you want the results to be ordered by. If you don’t want to sort the result, just pass null, and the result will be returned the way it has been stored.

    8. **String limit**: set a limit to the number of rows to be included into the result. If you want no limit, just pass null.

**Note**, there is an **overloaded** version of this method, which has one more
parameter at the beginning, of *Boolean type*. If you pass true then its like
you use **distinct** keyword in your SQL select, which means you want to
eliminate duplicates values. If you pass false, you do not use the distinct
keyword, so the result will be exactly the same as if using the previous version
of query method.

Also **Note** that there are also another methods you can use to run a query on
your database. For example the rawQuery method. This method takes two string
parameters, the first is the SQL select query, and the seconds is the where
clause of this query. The results are the same but you may prefer to use query
method cause it protect you from SQL injections. Also, another plus of query
method is that it builds the query for you (remember the idea similar to the URL
builder). So you don’t have to create the select query yourself and in that way
your code is more readable and less error prone.

### REMEMBER TO UPDATE A CURSOR, AFTER INSERTION OF NEW DATA

In order to update the cursor – query result – to contain the correct data after
the insertion of new data (after the cursor being created in the first place)
you have to close your old cursor to avoid leaks, and then set it to new cursor,
by running again the query. 

**Note**: make sure that the cursor is not null before trying to close it.

### DELETING DATA FROM THE DATABASE – REMOVE ENTRY

In order to delete data from the database, you simple call delete on your db
object, passing as parameters the table’s name and the condition, that has to
meet the row – entry in order to be removed.

#### Code Template

```java
String condition = MyContract.TableClass.COLUMN_SAMPLE_1 + "operator" + (hardcodedValue | MyContract.TableClass.COLUMN_SAMPLE_2);
int numOfDeletedRows = mDb.delete(MyContract.TableClass.TABLE_NAME, condition, null);
```

**Note 1:** delete method returns the number of rows that have been deleted. If
none has deleted it will return 0.

**Note 2:** the third parameter is a array of string used when you have more
complex condition, in this case you may include ?s in the where clause, which
will be replaced by the values from whereArgs.

## Implement Wipe Off

You need to use **ItemTouchHelper**.

Its constructor need a ItemTouchHelper.*Callback*. Lets see how it works with
ItemTouchHelper.*SimpleCallback*, which is a subclass of the
ItemTouchHelper.*Callback*.

There are defined the following direction flags of int type: LEFT, RIGHT, START,
END, UP, DOWN.

The **ItemTouchHelper.SimpleCallback** constructror takes 2 parameters:

- *int dragDirections*: Binary OR of direction flags in which the Views can be dragged.

- *int swipeDirections*: Binary OR of direction flags in which the Views can be swiped.

**Note**: if you don’t want to provide the ability to either drag or swipe, you pass 0 to the corresponding parameter.

The **SimpleCallback**, requires you override the *onMove* and the *onSwipe*
method, that will be called if a drag or swipe has been occurred relatively.
So inside those methods you simply do whatever it has to be done!

Finally, simple attatch it to your recycler view using the following:
*.attachToRecyclerView(yourRecyclerView)* on the **ItemTouchHelper** object.

### Resources (apart frrom the entire Udacity's lesson)

shuklaxyz.blogspot.gr

[www.zentut.com](http://www.zentut.com)

[www.w3schools.com](http://www.w3schools.com)

<https://developer.android.com>

<https://stackoverflow.com/>