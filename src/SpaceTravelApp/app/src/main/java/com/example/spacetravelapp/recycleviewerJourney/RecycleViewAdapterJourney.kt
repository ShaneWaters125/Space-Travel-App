package com.example.spacetravelapp.recycleviewerJourney

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.spacetravelapp.R
import com.example.spacetravelapp.data.Planet
import com.example.spacetravelapp.ui.journeyplanner.JourneyPlanner
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * The RecycleViewAdapter (planets the user can add) for the plan your journey class for the app.
 * Is responsible for initialising the view holders inside the recycle viewer and binding the data. Also initialises the functionality of the view holders.
 *
 * @author Shane Waters
 * @version 1.0 (24/04/2021)
 */
class RecycleViewAdapterJourney : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var planets: List<Planet> = ArrayList()
    private lateinit var parentView: ViewGroup

    /**
     * Sets the layout of the view holder to R.layout.saved_planet_entry.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        parentView = parent
        return PlanetViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.planet_journey_entry, parent, false)
        )
    }

    /**
     * Binds the view holder class to the view holder object. Initialises the view holders functionality.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is PlanetViewHolder ->{
                holder.bind(planets[position])
                //Show the planet dialog window when the user presses on the view holder.
                holder.itemView.setOnClickListener{
                    planetDialog(holder.itemView.context, position)
                }

                //Adds the journey to the users journey plan.
                holder.itemView.findViewById<Button>(R.id.btnJourneyAdd).setOnClickListener {
                    //Adds the selected planet to the planned journey recycle view.
                    JourneyPlanner.plannedPlanets.add(planets[position])
                    //Updates the journey plan recycle viewer to contain the newly added planet.
                    JourneyPlanner.plannedAdapter.submitList(JourneyPlanner.plannedPlanets)
                    //Updates number of planets in the planned journey by retrieving JourneyPlanner intent.
                    val journeyIntent = JourneyPlanner.jIntent as Activity
                    journeyIntent.findViewById<TextView>(R.id.tvNumPlannedPlanets).text = JourneyPlanner.plannedPlanets.size.toString()
                    holder.itemView.findViewById<Button>(R.id.btnJourneyAdd).isEnabled = false

                    JourneyPlanner.planetAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    /**
     * Returns the amount of items in the recycle viewer.
     */
    override fun getItemCount(): Int {
        return planets.size
    }

    /**
     * Updates the view holders button status.
     */
    fun updateButtons(recyclerView: RecyclerView){
        //If a planet is removed from the current journey plan then the add to journey button needs to be re-enabled.
        for(x in 0 until recyclerView.childCount){
            var enableButton = true
            val viewHolder = recyclerView.getChildViewHolder(recyclerView.getChildAt(x))
            for(y in JourneyPlanner.plannedPlanets.indices){
                if(JourneyPlanner.plannedPlanets[y].name == viewHolder.itemView.findViewById<TextView>(R.id.tvJourneyPlanetName).text){
                    enableButton = false
                }
            }
            if(enableButton){
                viewHolder.itemView.findViewById<Button>(R.id.btnJourneyAdd).isEnabled = true
            }
        }

    }

    /**
     * Initialises the additional planet information dialog window.
     */
    @SuppressLint("SetTextI18n")
    private fun planetDialog(context: Context, position: Int){
        val planetDialog = Dialog(context, R.style.DialogTheme)
        planetDialog.setContentView(R.layout.dialog_planet)
        val planetName: TextView = planetDialog.findViewById(R.id.tvPlanetName)
        val planetSize: TextView = planetDialog.findViewById(R.id.tvPlanetSize)
        val planetTemp: TextView = planetDialog.findViewById(R.id.tvPlanetTemp)
        val planetDistance: TextView = planetDialog.findViewById(R.id.tvPlanetDistance)
        val planetGravity: TextView = planetDialog.findViewById(R.id.tvPlanetGravity)
        val planetOrbit: TextView = planetDialog.findViewById(R.id.tvPlanetOrbit)
        val planetStars: TextView = planetDialog.findViewById(R.id.tvPlanetStars)
        val planetImage: ImageView = planetDialog.findViewById(R.id.ivPlanetInfo)
        val planetRA: TextView = planetDialog.findViewById(R.id.tvRA)
        val planetDec: TextView = planetDialog.findViewById(R.id.tvDec)
        val planetType: TextView = planetDialog.findViewById(R.id.tvPlanetType)

        planetName.text = planets[position].name
        planetSize.text = if(planets[position].size == "Unknown") "Unknown" else String.format("%.2f x Earth", (planets[position].size.toDouble()))
        planetTemp.text = if(planets[position].temperature == "Unknown") "Unknown" else String.format("%.2f°C", planets[position].temperature.toDouble()-273.15)
        planetDistance.text = if(planets[position].distance == "Unknown") "Unknown" else String.format("%.2f Light-years", planets[position].distance.toDouble()*3.26156)
        planetGravity.text = if(planets[position].gravity == "Unknown") "Unknown" else String.format("%.2f x Earth", (planets[position].gravity.toDouble() / 2.99))
        planetOrbit.text = if(planets[position].orbit == "Unknown") "Unknown" else String.format("%.2f Days", (planets[position].orbit.toDouble()))
        planetStars.text = if(planets[position].stars == "Unknown") "Unknown" else String.format("%d", (planets[position].stars.toInt()))
        planetRA.text = if(planets[position].ra == "Unknown") "Unknown" else String.format("%.2f°", (planets[position].ra.toDouble()))
        planetDec.text = if(planets[position].dec == "Unknown") "Unknown" else String.format("%.2f°", (planets[position].dec.toDouble()))

        //Set the planet image and type depending on temperature and density.
        if(planets[position].temperature == "Unknown"){
            planetImage.setImageResource(R.drawable.unkownvector)
        } else{
            if(planets[position].temperature.toDouble()-273.15 < -50){
                planetImage.setImageResource(R.drawable.watervector)
                planetType.text = "Ice or Rock/Mountain Planet"
            } else if(planets[position].temperature.toDouble()-273.15 >= -50 && planets[position].temperature.toDouble()-273.15 <= 50){
                planetImage.setImageResource(R.drawable.earthvector)
                planetType.text = "Earth-Like or Desert Planet"
            } else if(planets[position].temperature.toDouble()-273.15 in 50.0..100.0){
                planetImage.setImageResource(R.drawable.desertvector)
                planetType.text = "Hot Desert Planet / Mountain Planet"
            }else{
                planetImage.setImageResource(R.drawable.moltenvector)
                planetType.text = "Molten Planet"
            }
        }

        //Check whether the planet is a gas planet or not.
        if(planets[position].density != "Unknown"){
            if(planets[position].density.toDouble() < 2.0){
                planetImage.setImageResource(R.drawable.gasvector)
                planetType.text = "Gas Planet"
            }
        }

        if(planetType.text == ""){
            planetType.text = "Unknown"
        }

        //Initialises the close button.
        val btnClose: ImageButton = planetDialog.findViewById(R.id.btnPlanetClose)
        btnClose.setOnClickListener(View.OnClickListener { View ->
            planetDialog.dismiss()
        })

        //Shows the dialog window after everything has been initialised.
        planetDialog.show()
    }

    /**
     * Submits the data to recycle viewer to show.
     */
    fun submitList(planetList: List<Planet>){
        planets = planetList
        notifyDataSetChanged()
    }

    /**
     * View Holder class which is responsible for binding the data to the view holder.
     */
    class PlanetViewHolder constructor(
            planetView: View
    ): RecyclerView.ViewHolder(planetView){
        private val planetName: TextView = planetView.findViewById(R.id.tvJourneyPlanetName)
        private val planetType: TextView = planetView.findViewById(R.id.tvJourneyPlanetType)
        private val planetTemp: TextView = planetView.findViewById(R.id.tvJourneyPlanetTemp)
        private val planetGravity: TextView = planetView.findViewById(R.id.tvJourneyPlanetGravity)
        private val planetImage: ImageView = planetView.findViewById(R.id.ivJourneyPlanetImage)
        private val cardBackground: CardView = planetView.findViewById(R.id.cardviewJourney)
        private val planetDistanceAdd: TextView = planetView.findViewById(R.id.tvJourneyDistanceAdd)
        private val planetAddJourney: Button = planetView.findViewById(R.id.btnJourneyAdd)

        @SuppressLint("SetTextI18n")
        fun bind(planet: Planet){
            planetName.text = planet.name
            planetTemp.text = if(planet.temperature == "Unknown") "Unknown" else String.format("%.2f°C", planet.temperature.toDouble()-273.15)
            planetGravity.text = if(planet.gravity == "Unknown") "Unknown" else String.format("%.2f x Earth", (planet.gravity.toDouble() / 2.99))
            planetDistanceAdd.text = String.format("+%.2f Light Years", planet.distance.toDouble()*3.262)

            if(JourneyPlanner.plannedPlanets.size != 0){
                //Calculates distance from one planet to another.
                //http://neoprogrammics.com/stars/distance_between_two_stars/index.php
                val lastPlanet = JourneyPlanner.plannedPlanets[JourneyPlanner.plannedPlanets.size-1]
                //Calculating their x, y, and z coordinates on the star map.
                val x1 = lastPlanet.distance.toDouble()*3.26156 * kotlin.math.cos(Math.toRadians(lastPlanet.ra.toDouble())) * kotlin.math.cos(Math.toRadians(lastPlanet.dec.toDouble()))
                val y1 = lastPlanet.distance.toDouble()*3.26156 * kotlin.math.sin(Math.toRadians(lastPlanet.ra.toDouble())) * kotlin.math.cos(Math.toRadians(lastPlanet.dec.toDouble()))
                val z1 = lastPlanet.distance.toDouble()*3.26156 * kotlin.math.sin(abs(Math.toRadians(lastPlanet.dec.toDouble())))

                val x2 = planet.distance.toDouble()*3.26156 * kotlin.math.cos(Math.toRadians(planet.ra.toDouble())) * kotlin.math.cos(Math.toRadians(planet.dec.toDouble()))
                val y2 = planet.distance.toDouble()*3.26156 * kotlin.math.sin(Math.toRadians(planet.ra.toDouble())) * kotlin.math.cos(Math.toRadians(planet.dec.toDouble()))
                val z2 = planet.distance.toDouble()*3.26156 * kotlin.math.sin(abs(Math.toRadians(planet.dec.toDouble())))

                //Finding the difference between the two x, y, and z coordinates.
                val dx = (x2-x1)
                val dy = (y2-y1)
                val dz = (z2-z1)
                //Calculating the distance between two points in 3d space.
                val answer = sqrt((dx*dx)+(dy*dy)+(dz*dz))
                //Updates the distance on the view holder.
                planetDistanceAdd.text = String.format("+%.2f Light Years", answer)
            }

            //Set the planet image and type depending on temperature and density.
            if(planet.temperature == "Unknown"){
                planetImage.setImageResource(R.drawable.unkownvector)
            } else{
                if(planet.temperature.toDouble()-273.15 < -50){
                    planetImage.setImageResource(R.drawable.watervector)
                    planetType.text = "Ice or Rock/Mountain Planet"
                    cardBackground.setCardBackgroundColor(Color.parseColor("#c5fafa"))
                } else if(planet.temperature.toDouble()-273.15 in -50.0..50.0){
                    planetImage.setImageResource(R.drawable.earthvector)
                    planetType.text = "Earth-Like or Desert Planet"
                    cardBackground.setCardBackgroundColor(Color.parseColor("#ddfac5"))
                } else if(planet.temperature.toDouble()-273.15 in 50.0..100.0) {
                    planetImage.setImageResource(R.drawable.desertvector)
                    planetType.text = "Hot Desert Planet / Mountain Planet"
                    cardBackground.setCardBackgroundColor(Color.parseColor("#fae6c5"))
                } else{
                    planetImage.setImageResource(R.drawable.moltenvector)
                    planetType.text = "Molten Planet"
                    cardBackground.setCardBackgroundColor(Color.parseColor("#fac5c5"))
                }
            }

            //Check whether the planet is a gas planet or not.
            if(planet.density != "Unknown"){
                if(planet.density.toDouble() < 2.0){
                    planetImage.setImageResource(R.drawable.gasvector)
                    planetType.text = "Gas Planet"
                }
            }
            if(planetType.text == ""){
                planetType.text = "Unknown"
            }

            //Check whether the planet is already added to their journey or not.
            var enableButton = true
            for(y in JourneyPlanner.plannedPlanets.indices){
                if(JourneyPlanner.plannedPlanets[y].name == planetName.text){
                    enableButton = false
                }
            }
            planetAddJourney.isEnabled = enableButton

        }


    }
}
