package com.example.spacetravelapp.ui.archive

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spacetravelapp.MainActivity
import com.example.spacetravelapp.R
import com.example.spacetravelapp.data.DatabaseHandler
import com.example.spacetravelapp.databinding.FragmentPlanetarchiveBinding
import com.example.spacetravelapp.data.Planet
import com.example.spacetravelapp.recycleviewerArchive.RecycleViewAdapterArchive
import com.example.spacetravelapp.recycleviewerArchive.SideSpacingItemDecoration
import com.example.spacetravelapp.recycleviewerArchive.TopSpacingItemDecoration
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.pow
import kotlin.random.Random

/**
 * The PlanetArchive Fragment class for the app.
 * Is responsible for initialising the recycle viewer, parsing the exoplanet database data, and allowing the the database to be filtered and searched.
 *
 * @author Shane Waters
 * @version 1.0 (24/04/2021)
 */
class PlanetArchive : Fragment() {

    private lateinit var archiveFragment: FragmentPlanetarchiveBinding
    private lateinit var planetAdapter: RecycleViewAdapterArchive
    private lateinit var originalData: MutableList<Planet>
    private lateinit var filterData: MutableList<Planet>
    private lateinit var searchData: MutableList<Planet>
    private lateinit var favouriteData: MutableList<Planet>
    private var showFavourites: Boolean = false

    companion object {
        //Hardcoded path for backup
        //const val CSV_FILE_PATH = "/data/data/com.example.spacetravelapp/files/planets.csv"
        //Dynamically found file path for external storage.
        val CSV_FILE_PATH = MainActivity.fileDirectory + "/planetsdata.csv"
    }

    /**
     * Initialises the Fragment.
     */
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        archiveFragment = FragmentPlanetarchiveBinding.inflate(inflater, container, false)
        //Initialises the recycle viewer.
        initRecyclerView()
        //Adds the data to the recycle viewer.
        addData()
        archiveFragment.tvNumPlanets.text = "Showing " + planetAdapter.itemCount + " planets"
        archiveFragment.etPlanetSearch.isVisible = false
        return archiveFragment.root
    }

    /**
     * Initialises the buttons.
     */
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Shows the database filter dialog window.
        archiveFragment.ibFilter.setOnClickListener(View.OnClickListener { View ->
            filterDialog()
        })

        //Toggles the search bar.
        archiveFragment.ibSearch.setOnClickListener(View.OnClickListener { View ->
            archiveFragment.etPlanetSearch.isVisible = !archiveFragment.etPlanetSearch.isVisible
        })

        //Favourites filter toggle
        archiveFragment.ibFavouriteFilter.setOnClickListener { View ->
            if(!showFavourites){
                showFavourites = true
                archiveFragment.ibFavouriteFilter.setImageResource(R.drawable.ic_baseline_star_24_golden)

                favouriteData = originalData.filter{MainActivity.userFavourites.contains(it.id.toString())}.toCollection(ArrayList())
                planetAdapter.submitList(favouriteData)
                archiveFragment.tvNumPlanets.text = "Showing " + planetAdapter.itemCount + " planets"
            } else{
                showFavourites = false
                archiveFragment.ibFavouriteFilter.setImageResource(R.drawable.ic_baseline_star_24)
                planetAdapter.submitList(originalData)
                archiveFragment.tvNumPlanets.text = "Showing " + planetAdapter.itemCount + " planets"
            }
        }

        //Searches the planet archive
        archiveFragment.etPlanetSearch.addTextChangedListener{ TextWatcher ->
            //Searching the filtered data requires the data to be filtered first, if not then we initialise the filtered data as the original data.
            //Searching the filtered data allows both filtered search and the name search to both be active at the same time.
            if(!this::filterData.isInitialized){
                filterData = originalData
            }
            if(showFavourites){
                searchData = favouriteData.filter{it.name.startsWith(archiveFragment.etPlanetSearch.text.toString(), 0, true)}.toCollection(ArrayList())
            } else{
                searchData = filterData.filter{it.name.startsWith(archiveFragment.etPlanetSearch.text.toString(), 0, true)}.toCollection(ArrayList())
            }
            planetAdapter.submitList(searchData)
            archiveFragment.tvNumPlanets.text = "Showing " + planetAdapter.itemCount + " planets"
        }
    }


    /**
     * Parses the data from the database into the app.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun addData(){
        val data = mutableListOf<Planet>()
        val reader = Files.newBufferedReader(Paths.get(CSV_FILE_PATH))
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
            data.add(Planet(i ,name, size, orbit, stars, gravity, temp, distance, rotation, ra, dec, mass, planetDensity))
        }
        originalData = data
        planetAdapter.submitList(data)
    }

    /**
     * Initialises the recycle viewer.
     */
    private fun initRecyclerView(){
        archiveFragment.recycleviewPlanets.apply{
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecoration = TopSpacingItemDecoration(20)
            val sideSpacingItemDecoration = SideSpacingItemDecoration(20)
            addItemDecoration(topSpacingDecoration)
            addItemDecoration(sideSpacingItemDecoration)
            planetAdapter = RecycleViewAdapterArchive()
            adapter = planetAdapter
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
     * Initialises the filter planet dialog window.
     */
    @SuppressLint("SetTextI18n")
    private fun filterDialog(){
        val filterDialog = Dialog(requireContext(), R.style.DialogTheme)
        filterDialog.setContentView(R.layout.dialog_filter)

        //Initialise the spinners used.
        val spinnerGravity: Spinner = filterDialog.findViewById(R.id.spnGravity)
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.gravity,
                android.R.layout.simple_spinner_item
        ).also{ adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerGravity.adapter = adapter
        }

        val spinnerSize: Spinner = filterDialog.findViewById(R.id.spnSize)
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.size,
                android.R.layout.simple_spinner_item
        ).also{ adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSize.adapter = adapter
        }

        //Closes the dialog window when pressed.
        val btnClose: ImageButton = filterDialog.findViewById(R.id.btnClose)
        btnClose.setOnClickListener(View.OnClickListener { View ->
            filterDialog.dismiss()
        })

        //Applies the filters to the data by repeatedly using the filter function.
        val btnApplyFilters: Button = filterDialog.findViewById(R.id.btnApplyFilters)
        btnApplyFilters.setOnClickListener(View.OnClickListener { View ->
            val minDistance: EditText = filterDialog.findViewById(R.id.etMinDistance)
            val maxDistance: EditText = filterDialog.findViewById(R.id.etMaxDistance)
            val minTemp: EditText = filterDialog.findViewById(R.id.etMinTemperature)
            val maxTemp: EditText = filterDialog.findViewById(R.id.etMaxTemperature)
            val spnGravity: Spinner = filterDialog.findViewById(R.id.spnGravity)
            val spnSize: Spinner = filterDialog.findViewById(R.id.spnSize)
            filterData = originalData

            //Distance filter.
            if(!minDistance.text.isNullOrEmpty() && !maxDistance.text.isNullOrEmpty() && !maxDistance.text.toString().all { it.isLetter() } && !minDistance.text.toString().all { it.isLetter() }){
                if((minDistance.text.toString().toDouble() <= maxDistance.text.toString().toDouble()) && maxDistance.text.toString().toDouble() >= 0){
                    filterData = filterData.filter{ !it.distance.startsWith("Unknown", 0, true)}.toCollection(ArrayList())
                    filterData = filterData.filter{ it.distance.toDouble()*3.26156 >= minDistance.text.toString().toDouble() && it.distance.toDouble()*3.26156 < maxDistance.text.toString().toDouble()}.toCollection(ArrayList())
                }
            }

            //Temp filter.
            if(!minTemp.text.isNullOrEmpty() && !maxTemp.text.isNullOrEmpty() && !maxTemp.text.toString().all { it.isLetter() } && !minTemp.text.toString().all { it.isLetter() }){
                if((minTemp.text.toString().toDouble() <= maxTemp.text.toString().toDouble()) && minTemp.text.toString().toDouble() >= -273.15){
                    filterData = filterData.filter{ !it.temperature.startsWith("Unknown", 0, true)}.toCollection(ArrayList())
                    filterData = filterData.filter{ it.temperature.toDouble()-273.15 >= minTemp.text.toString().toDouble() && it.temperature.toDouble()-273.15 < maxTemp.text.toString().toDouble()}.toCollection(ArrayList())
                }
            }

            //Gravity Filter.
            if(spnGravity.selectedItem != null){
                if(spnGravity.selectedItemPosition == 1){
                    filterData = filterData.filter{ !it.gravity.startsWith("Unknown", 0, true)}.toCollection(ArrayList())
                    filterData = filterData.filter{ it.gravity.toDouble()/2.99 < 0.8}.toCollection(ArrayList())
                }
                if(spnGravity.selectedItemPosition == 2){
                    filterData = filterData.filter{ !it.gravity.startsWith("Unknown", 0, true)}.toCollection(ArrayList())
                    filterData = filterData.filter{ it.gravity.toDouble()/2.99 in 0.8..1.2 }.toCollection(ArrayList())
                }
                if(spnGravity.selectedItemPosition == 3){
                    filterData = filterData.filter{ !it.gravity.startsWith("Unknown", 0, true)}.toCollection(ArrayList())
                    filterData = filterData.filter{ it.gravity.toDouble()/2.99 > 1.2}.toCollection(ArrayList())
                }
            }

            //Size Filter.
            if(spnSize.selectedItem != null){
                if(spnSize.selectedItemPosition == 1){
                    filterData = filterData.filter{ !it.size.startsWith("Unknown", 0, true)}.toCollection(ArrayList())
                    filterData = filterData.filter{ it.size.toDouble() < 0.7}.toCollection(ArrayList())
                }
                if(spnSize.selectedItemPosition == 2){
                    filterData = filterData.filter{ !it.size.startsWith("Unknown", 0, true)}.toCollection(ArrayList())
                    filterData = filterData.filter{ it.size.toDouble() in 0.7..1.3 }.toCollection(ArrayList())
                }
                if(spnSize.selectedItemPosition == 3){
                    filterData = filterData.filter{ !it.size.startsWith("Unknown", 0, true)}.toCollection(ArrayList())
                    filterData = filterData.filter{ it.size.toDouble() > 1.3}.toCollection(ArrayList())
                }
            }
            //Clear the name search after filtering in case they had a name entered beforehand as the name search will not function with names that are entered before a filter.
            archiveFragment.etPlanetSearch.text.clear()
            //Send the filtered data to the recycle viewer.
            planetAdapter.submitList(filterData)
            //Update planet count.
            archiveFragment.tvNumPlanets.text = "Showing " + planetAdapter.itemCount + " planets"
            filterDialog.dismiss()
        })

        //Show the filter dialog once it has been initialised.
        filterDialog.show()
    }

}