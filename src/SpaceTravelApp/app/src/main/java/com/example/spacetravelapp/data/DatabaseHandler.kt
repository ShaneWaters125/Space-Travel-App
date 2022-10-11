package com.example.spacetravelapp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

/**
 * The DatabaseHandler class for the app.
 * Is responsible for handling all the SQL for the database.
 *
 * @author Shane Waters
 * @version 1.0 (24/04/2021)
 */
class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    /**
     * Creating constants
     */
    companion object {
        private const val DATABASE_VERSION = 6
        private const val DATABASE_NAME = "PlanetDatabase"
        private const val TABLE_USERS = "UsersTable"

        private const val KEY_ID = "_id"
        private const val KEY_USER = "userName"
        private const val KEY_FAVOURITE = "favourite"
        private const val KEY_JOURNEYS = "journeys"
    }

    /**
     * Creates the table.
     */
    override fun onCreate(db: SQLiteDatabase?) {
        //Creating table with fields 'id', 'userName'
        val CREATE_USER_TABLE = ("CREATE TABLE " + TABLE_USERS + " ("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER + " TEXT," + KEY_FAVOURITE + " TEXT," + KEY_JOURNEYS + " TEXT" + ")")
        db?.execSQL(CREATE_USER_TABLE)
    }

    /**
     * Updates database version when emptying table.
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS)
        onCreate(db)
    }

    /**
     * Adds a new record into the database.
     * @param userName The user to be added into the table.
     */
    fun addUser(userName: String): Long{
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_USER, userName)
        contentValues.put(KEY_FAVOURITE, "")
        contentValues.put(KEY_JOURNEYS, "")

        //Insert new word data into the table
        val success = db.insert(TABLE_USERS, null, contentValues)

        db.close()
        return success
    }

    /**
     * Updates the users record using the ID provided.
     * @param userId Users ID.
     * @param userName Users name.
     * @param favouritePlanets Users favourited planets.
     * @param journeys Users saved journeys.
     */
    fun updateUser(userId: String, userName: String, favouritePlanets: ArrayList<String>, journeys: ArrayList<ArrayList<String>>): Int {
        val db = this.writableDatabase

        //The ContentValues is a data type which holds multiple types of data which are submitted into the database.
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, userId)
        contentValues.put(KEY_USER, userName)

        //Parsing the users favourited planets into a string separated by ',' for each planet.
        var stringBuffer: String = ""
        for((index, planet) in favouritePlanets.withIndex()){
            if(index == favouritePlanets.size-1){
                stringBuffer = stringBuffer + planet
            } else{
                stringBuffer = stringBuffer + planet + ","
            }
        }

        contentValues.put(KEY_FAVOURITE, stringBuffer)
        //Parsing the users saved journeys into a string separated by ',' for each planet and '/' for each journey.
        stringBuffer = ""
        for(journey in journeys){
            if(journey.size != 0){
                for((index, planet) in journey.withIndex()){
                    stringBuffer = stringBuffer + planet
                    if(index == journey.size-1){
                        stringBuffer = stringBuffer + "/"
                    } else{
                        stringBuffer = stringBuffer + ","
                    }
                }
            }
        }
        contentValues.put(KEY_JOURNEYS, stringBuffer)

        val success = db.update(TABLE_USERS, contentValues, "_id = ?", arrayOf(userId))

        db.close()
        return success
    }

    /**
     * Retrieves all the profiles within the users table.
     * @return An arraylist which contains all the profiles within the table.
     */
    fun viewUsers(): ArrayList<User>{
        val userList: ArrayList<User> = ArrayList<User>()

        val selectQuery = "SELECT  * FROM $TABLE_USERS"

        val db = this.readableDatabase
        var cursor: Cursor? = null

        //Runs the SQL and returns a cursor over the results set.
        try{
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException){
            //If no cursor can be returned then just run the select query.
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var userName: String
        var favourites: String
        var journeys: String

        //Use the cursor to iterate over all the words and fill up the users with user objects.
        if(cursor.moveToFirst()){
            do{
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                userName = cursor.getString(cursor.getColumnIndex(KEY_USER))
                favourites = cursor.getString(cursor.getColumnIndex(KEY_FAVOURITE))
                journeys = cursor.getString(cursor.getColumnIndex(KEY_JOURNEYS))
                val user = User(id = id, userName = userName, favourites = favourites, journeys = journeys)
                userList.add(user)
            } while(cursor.moveToNext())
        }

        return userList
    }

    /**
     * Completely empties the table, deletes all records within the table.
     */
    fun emptyTable(){
        val db = this.writableDatabase
        val EMPTY_TABLE = ("DELETE FROM " + TABLE_USERS)
        db?.execSQL(EMPTY_TABLE)
    }

}