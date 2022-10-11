package com.example.spacetravelapp.ui.journeyplanner

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spacetravelapp.JourneyActivity
import com.example.spacetravelapp.MainActivity
import com.example.spacetravelapp.ProfilesActivity
import com.example.spacetravelapp.R
import com.example.spacetravelapp.data.Planet
import com.example.spacetravelapp.databinding.FragmentJourneyPlannerBinding
import com.example.spacetravelapp.recycleviewerArchive.BotSpacingItemDecoration
import com.example.spacetravelapp.recycleviewerArchive.RecycleViewAdapterArchive
import com.example.spacetravelapp.recycleviewerArchive.SideSpacingItemDecoration
import com.example.spacetravelapp.recycleviewerArchive.TopSpacingItemDecoration
import com.example.spacetravelapp.recycleviewerJourney.RecycleViewAdapterJourney
import com.example.spacetravelapp.recycleviewerPlanned.RecycleViewAdapterPlanned
import com.example.spacetravelapp.ui.archive.PlanetArchive
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.pow
import kotlin.random.Random

/**
 * The JourneyPlanner Fragment class for the app.
 * Is responsible for initialising the recycle viewers, parsing the exoplanet database data, and allowing the the database to be filtered.
 * Allows the user to create journeys using the recycle viewers and their favourited planets.
 *
 * @author Shane Waters
 * @version 1.0 (24/04/2021)
 */
class JourneyPlanner : Fragment() {

    private lateinit var journeyFragment: FragmentJourneyPlannerBinding
    private lateinit var originalData: MutableList<Planet>
    private var filterData: MutableList<Planet> = ArrayList()

    companion object{
        var plannedPlanets: MutableList<Planet> = ArrayList()
        lateinit var planetAdapter: RecycleViewAdapterJourney
        lateinit var plannedAdapter: RecycleViewAdapterPlanned
        lateinit var jIntent: Context
    }

    /**
     * Initialises the Fragment.
     */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        journeyFragment = FragmentJourneyPlannerBinding.inflate(inflater, container, false)
        return journeyFragment.root
    }

    /**
     * Initialises the recycle viewer, buttons and data.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        plannedPlanets = ArrayList()
        initRecyclerView()
        addData()
        initSpinnersAndButtons()
        jIntent = this.requireContext()
    }

    /**
     * Initialises the recycle viewers.
     */
    private fun initRecyclerView(){
        journeyFragment.recycleviewJourney.apply{
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecoration = TopSpacingItemDecoration(20)
            val sideSpacingItemDecoration = SideSpacingItemDecoration(20)
            addItemDecoration(topSpacingDecoration)
            addItemDecoration(sideSpacingItemDecoration)
            planetAdapter = RecycleViewAdapterJourney()
            adapter = planetAdapter
        }

        journeyFragment.recycleviewPlanned.apply{
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val sideSpacingItemDecoration = SideSpacingItemDecoration(10)
            val botSpacingItemDecoration = BotSpacingItemDecoration(10)
            addItemDecoration(sideSpacingItemDecoration)
            addItemDecoration(botSpacingItemDecoration)
            plannedAdapter = RecycleViewAdapterPlanned()
            adapter = plannedAdapter
        }
    }

    /**
     * Initialises the recycle buttons and spinners.
     */
    private fun initSpinnersAndButtons(){
        //Initialising the spinners.
        val spinnerType: Spinner = journeyFragment.spnJourneyType
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.type,
                android.R.layout.simple_spinner_item
        ).also{ adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerType.adapter = adapter
        }

        val spinnerTemp: Spinner = journeyFragment.spnJourneyTemp
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.temp,
                android.R.layout.simple_spinner_item
        ).also{ adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTemp.adapter = adapter
        }

        val spinnerGravity: Spinner = journeyFragment.spnJourneyGravity
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.gravity,
                android.R.layout.simple_spinner_item
        ).also{ adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerGravity.adapter = adapter
        }

        //Filters the data depending on the users choices in the spinners.
        val btnJourneyFilter = journeyFragment.ibJourneyFilter
        btnJourneyFilter.setOnClickListener {
            filterData = originalData
            when(spinnerType.selectedItemPosition){
                1 -> {
                    filterData = filterData.filter { !it.temperature.startsWith("Unknown", 0, true)}.toCollection(ArrayList())
                    filterData = filterData.filter { it.temperature.toDouble()-273.15 < -50 }.toCollection(ArrayList())
                }
                2 -> {
                    filterData = filterData.filter { !it.temperature.startsWith("Unknown", 0, true)}.toCollection(ArrayList())
                    filterData = filterData.filter { it.temperature.toDouble()-273.15 >= -50 && it.temperature.toDouble()-273.15 <= 50 }.toCollection(ArrayList())
                }
                3 -> {
                    filterData = filterData.filter { !it.temperature.startsWith("Unknown", 0, true)}.toCollection(ArrayList())
                    filterData = filterData.filter { it.temperature.toDouble()-273.15 > 50 && it.temperature.toDouble()-273.15 <= 100}.toCollection(ArrayList())
                }
                4 -> {
                    filterData = filterData.filter { !it.temperature.startsWith("Unknown", 0, true)}.toCollection(ArrayList())
                    filterData = filterData.filter { it.temperature.toDouble()-273.15 > 100 }.toCollection(ArrayList())
                }
                5 -> {
                    filterData = filterData.filter { !it.density.startsWith("Unknown", 0, true)}.toCollection(ArrayList())
                    filterData = filterData.filter { it.density.toDouble() < 2.0 }.toCollection(ArrayList())
                }
            }
            when(spinnerTemp.selectedItemPosition){
                1 -> {
                    filterData = filterData.filter { !it.temperature.startsWith("Unknown", 0, true)}.toCollection(ArrayList())
                    filterData = filterData.filter { it.temperature.toDouble()-273.15 < -50 }.toCollection(ArrayList())
                }
                2 -> {
                    filterData = filterData.filter { !it.temperature.startsWith("Unknown", 0, true)}.toCollection(ArrayList())
                    filterData = filterData.filter { it.temperature.toDouble()-273.15 >= -50 && it.temperature.toDouble()-273.15 <= 50 }.toCollection(ArrayList())
                }
                3 -> {
                    filterData = filterData.filter { !it.temperature.startsWith("Unknown", 0, true)}.toCollection(ArrayList())
                    filterData = filterData.filter { it.temperature.toDouble()-273.15 > 50}.toCollection(ArrayList())
                }
            }
            when(spinnerGravity.selectedItemPosition){
                1 -> {
                    filterData = filterData.filter { !it.gravity.startsWith("Unknown", 0, true)}.toCollection(ArrayList())
                    filterData = filterData.filter { it.gravity.toDouble()/2.99 < 0.8 }.toCollection(ArrayList())
                }
                2 -> {
                    filterData = filterData.filter { !it.gravity.startsWith("Unknown", 0, true)}.toCollection(ArrayList())
                    filterData = filterData.filter { it.gravity.toDouble()/2.99 in 0.8..1.2 }.toCollection(ArrayList())
                }
                3 -> {
                    filterData = filterData.filter { !it.gravity.startsWith("Unknown", 0, true)}.toCollection(ArrayList())
                    filterData = filterData.filter { it.gravity.toDouble()/2.99 > 1.2}.toCollection(ArrayList())
                }
            }
            planetAdapter.submitList(filterData)
        }

        //Completes the journey and starts the journey activity.
        val btnCompleteJourney = journeyFragment.btnCompleteJourney
        btnCompleteJourney.setOnClickListener {
            //Checking to see if the user has added planets to their journey.
            if(plannedPlanets.size == 0){
                Toast.makeText(requireContext(), "Add planets to your journey first!", Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(requireContext(), JourneyActivity::class.java)
                startActivity(intent)
                JourneyActivity.savedJourney = false
            }
        }

    }

    /**
     * Random function for randomising a number between two ranges.
     */
    private fun rand(start: Int, end: Int): Int {
        require(!(start > end || end - start + 1 > Int.MAX_VALUE)) { "Illegal Argument" }
        return Random(System.nanoTime()).nextInt(end - start + 1) + start
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
        originalData = data.filter { MainActivity.userFavourites.contains(it.id.toString()) }.toCollection(ArrayList())
        originalData = originalData.filter { it.distance != "Unknown"}.toCollection(ArrayList())
        planetAdapter.submitList(originalData)

    }

}