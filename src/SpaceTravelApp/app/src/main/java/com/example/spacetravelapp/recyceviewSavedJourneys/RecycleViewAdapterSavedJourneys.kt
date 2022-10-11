package com.example.spacetravelapp.recyceviewSavedJourneys

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.spacetravelapp.JourneyActivity
import com.example.spacetravelapp.MainActivity
import com.example.spacetravelapp.R
import com.example.spacetravelapp.data.DatabaseHandler
import com.example.spacetravelapp.data.Journey
import com.example.spacetravelapp.ui.journeyplanner.JourneyPlanner
import com.example.spacetravelapp.ui.savedjourneys.SavedJourneys

/**
 * The RecycleViewAdapter for the saved journeys class for the app.
 * Is responsible for initialising the view holders inside the recycle viewer and binding the data. Also initialises the functionality of the view holders.
 *
 * @author Shane Waters
 * @version 1.0 (24/04/2021)
 */

class RecycleViewAdapterSavedJourneys : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //Data for the recycle viewer to display.
    private var journeys: List<Journey> = ArrayList()
    private lateinit var pIntent: Context

    /**
     * Sets the layout of the view holder to R.layout.saved_planet_entry.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        pIntent = parent.context
        return RecycleViewAdapterSavedJourneys.JourneyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.saved_planet_entry, parent, false)
        )
    }

    /**
     * Binds the view holder class to the view holder object. Initialises the view holders functionality.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is RecycleViewAdapterSavedJourneys.JourneyViewHolder ->{
                holder.bind(journeys[position])

                //Opens the saved journey.
                holder.itemView.findViewById<ImageButton>(R.id.ibOpenSavedJourney).setOnClickListener {
                    JourneyPlanner.plannedPlanets = journeys[position].planets
                    val intent = Intent(pIntent, JourneyActivity::class.java)
                    pIntent.startActivity(intent)
                    JourneyActivity.savedJourney = true
                }

                //Deletes the saved journey.
                holder.itemView.findViewById<ImageButton>(R.id.ibRemoveSavedJourney).setOnClickListener {
                    //Removes the journey from the profiles saved journey list.
                    journeys.drop(position)
                    notifyItemRangeRemoved(position, journeys.size)
                    notifyItemRemoved(position)
                    holder.itemView.visibility = View.GONE
                    submitList(journeys)
                    notifyDataSetChanged()

                    //Remove the view holder from the recycle viewer.
                    if(position == journeys.size-1 && position != 0){
                        MainActivity.userJourneys.removeAt(holder.layoutPosition-1)
                    }else if(position == 0){
                        MainActivity.userJourneys.removeAt(holder.layoutPosition)
                    } else{
                        MainActivity.userJourneys.removeAt(holder.layoutPosition)
                    }
                    val database = DatabaseHandler(pIntent)
                    //Update the profiles record in the database with the new data.
                    database.updateUser(MainActivity.userId, MainActivity.userName, MainActivity.userFavourites, MainActivity.userJourneys)
                }
            }
        }
    }

    /**
     * Returns the amount of items in the recycle viewer.
     */
    override fun getItemCount(): Int {
        return journeys.size
    }

    /**
     * Submits the data to recycle viewer to show.
     */
    fun submitList(journeyList: List<Journey>){
        journeys = journeyList
        notifyDataSetChanged()
    }

    /**
     * View Holder class which is responsible for binding the data to the view holder.
     */
    class JourneyViewHolder constructor(
        journeyView: View
    ): RecyclerView.ViewHolder(journeyView){
        private val journeyPlanets: TextView = journeyView.findViewById(R.id.tvSavedJourneyNames)

        /**
         * Binds all the data to the view holder.
         */
        @SuppressLint("SetTextI18n")
        fun bind(journey: Journey){
            var journeyName = ""
            for(planet in journey.planets){
                journeyName = journeyName + planet.name + ", "
            }
            journeyPlanets.text = journeyName
        }

    }
}