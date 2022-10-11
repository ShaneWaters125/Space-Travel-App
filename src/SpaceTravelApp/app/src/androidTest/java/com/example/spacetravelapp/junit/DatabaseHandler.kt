package com.example.spacetravelapp.junit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import com.example.spacetravelapp.MainActivity
import com.example.spacetravelapp.data.DatabaseHandler
import junit.framework.Assert
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DatabaseHandler {

    val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @JvmField @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun addNewUser(){
        val database = DatabaseHandler(appContext)
        database.emptyTable()
        database.addUser("Shane")
        val users = database.viewUsers()
        assertEquals("Shane", users[0].userName)
        database.emptyTable()
    }

    @Test
    fun updateUser(){
        val database = DatabaseHandler(appContext)
        database.emptyTable()
        database.addUser("Shane")
        val favourites: ArrayList<String> = ArrayList()
        val journeys: ArrayList<ArrayList<String>> = ArrayList()

        database.updateUser("1", "Testing", favourites, journeys)

        val users = database.viewUsers()

        assertEquals("Testing", users[0].userName)
        database.emptyTable()
    }

    @Test
    fun emptyTable(){
        val database = DatabaseHandler(appContext)
        database.emptyTable()
        database.addUser("Shane")
        database.emptyTable()
        assertEquals(ArrayList<String>(), database.viewUsers())
    }
}