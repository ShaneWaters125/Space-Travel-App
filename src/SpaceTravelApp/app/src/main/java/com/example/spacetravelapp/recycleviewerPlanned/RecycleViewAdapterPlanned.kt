package com.example.spacetravelapp.recycleviewerPlanned

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.spacetravelapp.R
import com.example.spacetravelapp.data.Planet
import com.example.spacetravelapp.data.User
import com.example.spacetravelapp.ui.journeyplanner.JourneyPlanner

/**
 * The RecycleViewAdapter (planets the user has already added) for the plan your journey class for the app.
 * Is responsible for initialising the view holders inside the recycle viewer and binding the data. Also initialises the functionality of the view holders.
 *
 * @author Shane Waters
 * @version 1.0 (24/04/2021)
 */
class RecycleViewAdapterPlanned : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var planets: List<Planet> = ArrayList()
    private lateinit var parentView: ViewGroup

    /**
     * Sets the layout of the view holder to R.layout.saved_planet_entry.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        parentView = parent
        return RecycleViewAdapterPlanned.PlannedViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.planned_planet_entry, parent, false)
        )
    }

    /**
     * Binds the view holder class to the view holder object. Initialises the view holders functionality.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is RecycleViewAdapterPlanned.PlannedViewHolder ->{
                holder.bind(planets[position])
                //Removes the planet from the planned journey.
                holder.itemView.findViewById<ImageButton>(R.id.ibRemovePlannedPlanet).setOnClickListener {
                    //Removes the planet from the planned planet list.
                    JourneyPlanner.plannedPlanets.remove(planets[position])
                    //Updates the journey planner recycle viewer to re-enable the planet.
                    JourneyPlanner.plannedAdapter.submitList(JourneyPlanner.plannedPlanets)
                    //Updates number of planets in the planned journey by retrieving JourneyPlanner intent.
                    val journeyIntent = JourneyPlanner.jIntent as Activity
                    journeyIntent.findViewById<TextView>(R.id.tvNumPlannedPlanets).text = JourneyPlanner.plannedPlanets.size.toString()
                    JourneyPlanner.planetAdapter.updateButtons(journeyIntent.findViewById<RecyclerView>(R.id.recycleview_journey))
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
    fun submitList(accountList: List<Planet>){
        planets = accountList
        notifyDataSetChanged()
    }

    /**
     * View Holder class which is responsible for binding the data to the view holder.
     */
    class PlannedViewHolder constructor(
            plannedView: View
    ): RecyclerView.ViewHolder(plannedView){
        private val planetName: TextView = plannedView.findViewById(R.id.tvPlannedPlanetName)
        private val planetImage: ImageView = plannedView.findViewById(R.id.ivPlannedPlanetPicture)
        private val cardBackground: CardView = plannedView.findViewById(R.id.cvPlannedPlanet)

        @SuppressLint("SetTextI18n")
        fun bind(planet: Planet){
            planetName.text = planet.name

            //Set the planet image and type depending on temperature and density.
            if(planet.temperature == "Unknown"){
                planetImage.setImageResource(R.drawable.unkownvector)
            } else{
                if(planet.temperature.toDouble()-273.15 < -50){
                    planetImage.setImageResource(R.drawable.watervector)
                    cardBackground.setCardBackgroundColor(Color.parseColor("#c5fafa"))
                } else if(planet.temperature.toDouble()-273.15 in -50.0..50.0){
                    planetImage.setImageResource(R.drawable.earthvector)
                    cardBackground.setCardBackgroundColor(Color.parseColor("#ddfac5"))
                } else if(planet.temperature.toDouble()-273.15 in 50.0..100.0) {
                    planetImage.setImageResource(R.drawable.desertvector)
                    cardBackground.setCardBackgroundColor(Color.parseColor("#fae6c5"))
                } else{
                    planetImage.setImageResource(R.drawable.moltenvector)
                    cardBackground.setCardBackgroundColor(Color.parseColor("#fac5c5"))
                }
            }

            //Check whether the planet is a gas planet or not.
            if(planet.density != "Unknown"){
                if(planet.density.toDouble() < 2.0){
                    planetImage.setImageResource(R.drawable.gasvector)
                }
            }

        }

    }

}