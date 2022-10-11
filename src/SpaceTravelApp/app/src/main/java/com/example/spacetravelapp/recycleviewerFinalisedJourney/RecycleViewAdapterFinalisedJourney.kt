package com.example.spacetravelapp.recycleviewerFinalisedJourney

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.spacetravelapp.R
import com.example.spacetravelapp.data.Planet
import com.example.spacetravelapp.recycleviewerJourney.RecycleViewAdapterJourney
import com.example.spacetravelapp.ui.journeyplanner.JourneyPlanner
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * The RecycleViewAdapter for the planned journey class for the app.
 * Is responsible for initialising the view holders inside the recycle viewer and binding the data. Also initialises the functionality of the view holders.
 *
 * @author Shane Waters
 * @version 1.0 (24/04/2021)
 */
class RecycleViewAdapterFinalisedJourney: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var planets: List<Planet> = ArrayList()

    /**
     * Sets the layout of the view holder to R.layout.saved_planet_entry.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RecycleViewAdapterFinalisedJourney.PlanetViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.final_planet_entry, parent, false)
        )
    }

    /**
     * Binds the view holder class to the view holder object. Initialises the view holders functionality.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is RecycleViewAdapterFinalisedJourney.PlanetViewHolder ->{
                holder.bind(planets[position])
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

        private val planetName: TextView = planetView.findViewById(R.id.tvFinalJourneyPlanetName)
        private val planetImage: ImageView = planetView.findViewById(R.id.ivFinalJourneyPlanetImage)
        private val cardBackground: CardView = planetView.findViewById(R.id.cardviewFinalJourney)

        @SuppressLint("SetTextI18n")
        fun bind(planet: Planet){
            planetName.text = planet.name

            //Set the color of the view holder depending on the temperature of the planet.
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
        }


    }

}