package com.example.spacetravelapp

import android.annotation.SuppressLint
import android.app.Activity
import android.opengl.Visibility
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spacetravelapp.data.DatabaseHandler
import com.example.spacetravelapp.data.Planet
import com.example.spacetravelapp.recycleviewerArchive.SideSpacingItemDecoration
import com.example.spacetravelapp.recycleviewerArchive.TopSpacingItemDecoration
import com.example.spacetravelapp.recycleviewerFinalisedJourney.RecycleViewAdapterFinalisedJourney
import com.example.spacetravelapp.ui.journeyplanner.JourneyPlanner
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * The JourneyActivity class for the app.
 * Is responsible for initialising the recycle viewer, buttons and calculating the total distance travelled.
 *
 * @author Shane Waters
 * @version 1.0 (24/04/2021)
 */
class JourneyActivity : AppCompatActivity() {

    private lateinit var finalJourneyAdapter: RecycleViewAdapterFinalisedJourney
    //Holds the planets the journey has.
    private lateinit var originalData: MutableList<Planet>

    /**
     * Global variable used to determine if the journey is a newly created one or a user viewing an old journey.
     */
    companion object{
        var savedJourney = false
    }

    /**
     * Initialises the Activity.
     */
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journey)
        setSupportActionBar(findViewById(R.id.toolbar))
        actionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRecycleView()
        //Calculate all the statistics of the journey.
        setStats()
        initButtons()

        //If the journey is an old journey being viewed then the save button is removed and the cancel journey button is replaced with close journey.
        if(savedJourney){
            findViewById<Button>(R.id.btnSaveJourney).visibility = View.GONE
            findViewById<Button>(R.id.btnCancelJourney).text = "Close Journey"
        }

    }

    /**
     * Sets the back button in the toolbar of the activity to return the user to the previous activity.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    /**
     * Initialises the recycle viewer.
     */
    private fun initRecycleView(){
        val recyclerViewer = findViewById<RecyclerView>(R.id.recycleview_finaljourney)

        recyclerViewer.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            //This provides context of the activity to the recycle viewer so it can close after selecting a profile.
            ProfilesActivity.pIntent = context
            val topSpacingDecoration = TopSpacingItemDecoration(20)
            val sideSpacingItemDecoration = SideSpacingItemDecoration(20)
            addItemDecoration(topSpacingDecoration)
            addItemDecoration(sideSpacingItemDecoration)
            finalJourneyAdapter = RecycleViewAdapterFinalisedJourney()
            adapter = finalJourneyAdapter
        }
        //Sets the contents of the recycle viewer.
        addData()
    }

    /**
     * Initialises the activities buttons.
     */
    private fun initButtons(){
        val btnSaveJourney = findViewById<Button>(R.id.btnSaveJourney)
        //Setting up the save button.
        btnSaveJourney.setOnClickListener {
            val listToSave = ArrayList<String>()
            //Adding the journey to the list of journeys the user has.
            for(planet in originalData){
                listToSave.add(planet.id.toString())
            }
            MainActivity.userJourneys.add(listToSave)
            val database = DatabaseHandler(this)
            //Update the users record inside the database with the new data.
            database.updateUser(MainActivity.userId, MainActivity.userName, MainActivity.userFavourites, MainActivity.userJourneys)
            //Clears the recycle viewer on the plan your journey fragment which is opened in the background. This makes it so when the journey has been saved the users plan your journey is a fresh fragment.
            JourneyPlanner.plannedPlanets.clear()
            JourneyPlanner.planetAdapter.notifyDataSetChanged()
            JourneyPlanner.plannedAdapter.notifyDataSetChanged()
            (JourneyPlanner.jIntent as Activity).findViewById<TextView>(R.id.tvNumPlannedPlanets).text = "0"
            //Closes the activity.
            finish()
        }

        //Closes the activity.
        val btnCancelJourney = findViewById<Button>(R.id.btnCancelJourney)
        btnCancelJourney.setOnClickListener {
            finish()
        }
    }

    /**
     * Sets the TextViews on the activity to display the journeys statistics.
     */
    @SuppressLint("SetTextI18n")
    private fun setStats(){
        findViewById<TextView>(R.id.tvTotalPlanets).text = originalData.size.toString()
        findViewById<TextView>(R.id.tvTotalDistance).text = String.format("%.2f Light Years", calculateDistance())
        var totalStars = 0
        for(planet in originalData){
            if(planet.stars != "Unknown"){
                totalStars += planet.stars.toInt()
            }
        }
        findViewById<TextView>(R.id.tvTotalStars).text = totalStars.toString()
        //Formats the distance to be in standard form as its too big to display on the screen.
        val formatter = DecimalFormat("0.##E0", DecimalFormatSymbols.getInstance(Locale.ROOT))
        findViewById<TextView>(R.id.tvTotalTravelTime).text = formatter.format(calculateDistance()/2.9596023e-8) + " Light Years"
    }

    /**
     * Calculates the total distance travelled.
     */
    private fun calculateDistance(): Double{
        var totalDistance: Double = 0.0
        //http://neoprogrammics.com/stars/distance_between_two_stars/index.php
        for((i, planet) in originalData.withIndex()){
            //The distance to the first planet visited is from Earth to the planet.
            if(i == 0){
                totalDistance += planet.distance.toDouble()*3.26156
            } else if(i < originalData.size){
                //Calculating their x, y, and z coordinates on the star map.
                val x1 = planet.distance.toDouble()*3.26156 * kotlin.math.cos(Math.toRadians(planet.ra.toDouble())) * kotlin.math.cos(Math.toRadians(planet.dec.toDouble()))
                val y1 = planet.distance.toDouble()*3.26156 * kotlin.math.sin(Math.toRadians(planet.ra.toDouble())) * kotlin.math.cos(Math.toRadians(planet.dec.toDouble()))
                val z1 = planet.distance.toDouble()*3.26156 * kotlin.math.sin(abs(Math.toRadians(planet.dec.toDouble())))

                val x2 = originalData[i-1].distance.toDouble()*3.26156 * kotlin.math.cos(Math.toRadians(originalData[i-1].ra.toDouble())) * kotlin.math.cos(Math.toRadians(originalData[i-1].dec.toDouble()))
                val y2 = originalData[i-1].distance.toDouble()*3.26156 * kotlin.math.sin(Math.toRadians(originalData[i-1].ra.toDouble())) * kotlin.math.cos(Math.toRadians(originalData[i-1].dec.toDouble()))
                val z2 = originalData[i-1].distance.toDouble()*3.26156 * kotlin.math.sin(abs(Math.toRadians(originalData[i-1].dec.toDouble())))

                //Finding the difference between the two x, y, and z coordinates.
                val dx = (x2-x1)
                val dy = (y2-y1)
                val dz = (z2-z1)
                //Calculating the distance between two points in 3d space.
                val answer = sqrt((dx*dx)+(dy*dy)+(dz*dz))
                //Add the distance to the total distance.
                totalDistance += answer
            }
        }
        return totalDistance
    }

    /**
     * Sends the data to the recycle viewer to display.
     */
    private fun addData(){
        val data = JourneyPlanner.plannedPlanets
        originalData = data
        finalJourneyAdapter.submitList(data)
    }
}