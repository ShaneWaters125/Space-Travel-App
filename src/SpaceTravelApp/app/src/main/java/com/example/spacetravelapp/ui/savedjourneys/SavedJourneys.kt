package com.example.spacetravelapp.ui.savedjourneys

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spacetravelapp.MainActivity
import com.example.spacetravelapp.R
import com.example.spacetravelapp.data.Journey
import com.example.spacetravelapp.data.Planet
import com.example.spacetravelapp.databinding.FragmentJourneyPlannerBinding
import com.example.spacetravelapp.databinding.FragmentPlanetarchiveBinding
import com.example.spacetravelapp.databinding.FragmentSavedJourneysBinding
import com.example.spacetravelapp.recyceviewSavedJourneys.RecycleViewAdapterSavedJourneys
import com.example.spacetravelapp.recycleviewerArchive.SideSpacingItemDecoration
import com.example.spacetravelapp.recycleviewerArchive.TopSpacingItemDecoration
import com.example.spacetravelapp.recycleviewerJourney.RecycleViewAdapterJourney
import com.example.spacetravelapp.ui.archive.PlanetArchive
import com.example.spacetravelapp.ui.journeyplanner.JourneyPlanner
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.pow
import kotlin.random.Random

/**
 * The SavedJourneys Fragment class for the app.
 * Is responsible for initialising the recycle viewers, parsing the exoplanet database data.
 * Allows the user to see their saved journeys and open or delete them.
 *
 * @author Shane Waters
 * @version 1.0 (24/04/2021)
 */
class SavedJourneys : Fragment() {

    private lateinit var savedJourneysFragment: FragmentSavedJourneysBinding
    private lateinit var originalData: MutableList<Planet>
    private lateinit var savedJourneyAdapter: RecycleViewAdapterSavedJourneys

    /**
     * Initialises the Fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        savedJourneysFragment = FragmentSavedJourneysBinding.inflate(inflater, container, false)
        return savedJourneysFragment.root
    }

    /**
     * Initialises the recycle viewer and data.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRecyclerView()
        addData()
    }

    /**
     * Initialises the recycle viewer.
     */
    private fun initRecyclerView(){
        savedJourneysFragment.recycleviewSavedplanets.apply{
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecoration = TopSpacingItemDecoration(20)
            val sideSpacingItemDecoration = SideSpacingItemDecoration(20)
            addItemDecoration(topSpacingDecoration)
            addItemDecoration(sideSpacingItemDecoration)
            savedJourneyAdapter = RecycleViewAdapterSavedJourneys()
            adapter = savedJourneyAdapter
        }
    }

    /**
     * Parses the data from the database into the app.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun addData(){
        val data = mutableListOf<Planet>()
        val reader = Files.newBufferedReader(Paths.get(PlanetArchive.CSV_FILE_PATH))
        val csvParser = CSVParser(reader, CSVFormat.DEFAULT)
        //For each record in the database, make a planet object with all the corresponding information.
        for((i, csvRecord) in csvParser.withIndex()){
            //Get the data from the exoplanet database, if there is not data then put "Unknown".
            val name = if(csvRecord.get(0) != "") csvRecord.get(0) else "Unknown"
            val size = if(csvRecord.get(19) != "") csvRecord.get(19) else "Unknown"
            val orbit = if(csvRecord.get(11) != "") csvRecord.get(11) else "Unknown"
            val stars = if(csvRecord.get(3) != "") csvRecord.get(3) else "Unknown"
            val gravity = if(csvRecord.get(68) != "") csvRecord.get(68) else "Unknown"
            val temp = if(csvRecord.get(44) != "") csvRecord.get(44) else "Unknown"
            val distance = if(csvRecord.get(77) != "") csvRecord.get(77) else "Unknown"
            val rotation = rand(0, 360).toFloat()
            val ra = if(csvRecord.get(74) != "") csvRecord.get(74) else "Unknown"
            val dec = if(csvRecord.get(76) != "") csvRecord.get(76) else "Unknown"
            val mass = if(csvRecord.get(27) != "") csvRecord.get(27) else "Unknown"
            var planetDensity = "Unknown"

            //Calculates the density of the planet.
            if(mass != "Unknown" && size != "Unknown"){
                //https://www.astronomynotes.com/solarsys/s2.htm#:~:text=A%20planet's%20density%20is%20how,of%20their%20size%20and%20mass.
                //Calculate planets density so the surface properties can be estimated.
                val planetMass = mass.toDouble() * (5.972e+24)
                val planetDiameter = (size.toDouble() * (6371000)) * 2
                val planetVolume = (Math.PI/6)*(planetDiameter.pow(3.0))
                //val planetVolume = (planetDiameter.pow(3)*(Math.PI)*(4))/3
                //Convert kg/cm^3 to g/cm^3
                planetDensity = ((planetMass/planetVolume) / 1000).toString()
            }

            //Add the planet to the data that will be displayed in the recycle viewer.
            data.add(Planet(i, name, size, orbit, stars, gravity, temp, distance, rotation, ra, dec, mass, planetDensity))
        }

        originalData = data
        //Uses the exoplanet database to show the users saved journeys.
        val journeyList = ArrayList<Journey>()
        for(userJourney in MainActivity.userJourneys){
            val journey = Journey(ArrayList())
            for(planet in originalData){
                if(userJourney.contains(planet.id.toString())){
                    journey.planets.add(planet)
                }
            }
            journeyList.add(journey)
        }
        savedJourneyAdapter.submitList(journeyList)

    }

    /**
     * Random function for randomising a number between two ranges.
     */
    private fun rand(start: Int, end: Int): Int {
        require(!(start > end || end - start + 1 > Int.MAX_VALUE)) { "Illegal Argument" }
        return Random(System.nanoTime()).nextInt(end - start + 1) + start
    }
}