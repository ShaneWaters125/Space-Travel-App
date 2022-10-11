package com.example.spacetravelapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * The MainActivity class for the app. The first activity to be loaded once the app is started.
 * Is responsible for initialising navigation between the ui fragments and copying the exoplanet database to the users external storage.
 *
 * @author Shane Waters
 * @version 1.0 (24/04/2021)
 */
class MainActivity : AppCompatActivity() {

    /**
     * Global variables.
     */
    companion object {
        //Profile Data
        var userId: String = ""
        var userName: String = ""
        var userFavourites: ArrayList<String> = ArrayList()
        var userJourneys: ArrayList<ArrayList<String>> = ArrayList()
        //External storage file directory.
        lateinit var fileDirectory: String
        lateinit var drawerNaviation: DrawerLayout

        /**
         * Is used by other activities to close the navigation drawer as the object is private to this activity.
         */
        @SuppressLint("RtlHardcoded")
        fun closeDrawer(){
            val drawerLayout: DrawerLayout = drawerNaviation
            drawerLayout.closeDrawer(Gravity.LEFT)
        }
    }

    private lateinit var appBarConfiguration: AppBarConfiguration

    /**
     * Initialises the Activity and navigation.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        fileDirectory = filesDir.toString()
        copyFileToStorage(R.raw.planetsdata, "planetsdata.csv")

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerNaviation = drawerLayout
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_archive, R.id.nav_journeyPlanner, R.id.nav_savedJourneys), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    /**
     * Makes R.menu.main layout be displayed in the action bar.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    /**
     * Initialises the navigation.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        loadProfiles()
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /**
     * Initialises the select a profile button and displays the profiles name.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadProfiles(){
        //Set the profile name on the side navigation.
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        //If there user has selected a profile, assign their name.
        if(userName != ""){
            tvUserName.text = userName
        }

        val btnChangeUser = findViewById<ImageButton>(R.id.ibChangeUser)
        btnChangeUser.setOnClickListener(){
            //Starts the select a profile activity.
            intent = Intent(this, ProfilesActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Solution modified from https://stackoverflow.com/questions/4178168/how-to-programmatically-move-copy-and-delete-files-and-directories-on-sd
     * Copies a file from the raw resource folder to the phones external storage.
     *
     * @param resourceId The resource to be moved to the phones external storage.
     * @param resourceName The name given to the resource once saved.
     */
    fun copyFileToStorage(resourceId: Int, resourceName: String){
        var filePath: String = "$fileDirectory/$resourceName"
        try{
            var input: InputStream = resources.openRawResource(resourceId)
            var output: FileOutputStream? = null
            output = FileOutputStream(filePath)
            var buff: ByteArray = ByteArray(1024*4)
            var read: Int = 0
            //While there are bytes left to write to the external storage, keep writing the files bytes.
            try{
                read = input.read(buff)
                while (read > 0){
                    output.write(buff, 0, read)
                    read = input.read(buff)
                }
            } finally {
                input.close()
                output.close()
            }
        } catch (e: FileNotFoundException){
            e.printStackTrace()
        } catch (e: IOException){
            e.printStackTrace()
        }
    }
}