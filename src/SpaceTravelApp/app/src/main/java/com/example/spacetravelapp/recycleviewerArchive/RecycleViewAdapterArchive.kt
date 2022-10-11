package com.example.spacetravelapp.recycleviewerArchive

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.spacetravelapp.MainActivity
import com.example.spacetravelapp.R
import com.example.spacetravelapp.data.DatabaseHandler
import com.example.spacetravelapp.data.Planet

/**
 * The RecycleViewAdapter for the planet archive class for the app.
 * Is responsible for initialising the view holders inside the recycle viewer and binding the data. Also initialises the functionality of the view holders.
 *
 * @author Shane Waters
 * @version 1.0 (24/04/2021)
 */

class RecycleViewAdapterArchive : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var planets: List<Planet> = ArrayList()

    /**
     * Sets the layout of the view holder to R.layout.saved_planet_entry.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PlanetViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.planet_entry, parent, false)
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
                //When the user presses the favourite planet button, the planet is either added or removed from their favourites.
                holder.itemView.findViewById<ImageButton>(R.id.ibFavourite).setOnClickListener {
                    val databaseHandler = DatabaseHandler(holder.itemView.context)
                    //Check to see if they have selected an account.
                    if(MainActivity.userId.isEmpty()){
                        Toast.makeText(holder.itemView.context, "Please select an account to favourite planets!", Toast.LENGTH_SHORT).show()
                    } else{
                        //Remove the planet to users favourites.
                        if(MainActivity.userFavourites.contains(planets[position].id.toString())){
                            //Remove the planet from the users favourited planets list.
                            MainActivity.userFavourites.remove(planets[position].id.toString())
                            //Update the profiles record inside the database with the new data.
                            databaseHandler.updateUser(MainActivity.userId, MainActivity.userName, MainActivity.userFavourites, MainActivity.userJourneys)
                            val planetFavourite: ImageButton = holder.itemView.findViewById(R.id.ibFavourite)
                            //Change the color of the star.
                            planetFavourite.setImageResource(R.drawable.ic_baseline_star_24)
                            //Create a toast letting the user know their action was successful.
                            Toast.makeText(holder.itemView.context, "Planet removed from favourites!", Toast.LENGTH_SHORT).show()
                            notifyDataSetChanged()
                        } else{
                            //Add the planet from the users favourited planets list.
                            MainActivity.userFavourites.add(planets[position].id.toString())
                            //Update the profiles record inside the database with the new data.
                            databaseHandler.updateUser(MainActivity.userId, MainActivity.userName, MainActivity.userFavourites, MainActivity.userJourneys)
                            val planetFavourite: ImageButton = holder.itemView.findViewById(R.id.ibFavourite)
                            //Change the color of the star.
                            planetFavourite.setImageResource(R.drawable.ic_baseline_star_24_golden)
                            //Create a toast letting the user know their action was successful.
                            Toast.makeText(holder.itemView.context, "Planet added to favourites!", Toast.LENGTH_SHORT).show()
                            notifyDataSetChanged()
                        }
                    }
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
        if(planetType.text == ""){
            planetType.text = "Unknown"
        }
        if(planets[position].density != "Unknown"){
            if(planets[position].density.toDouble() < 2.0){
                planetImage.setImageResource(R.drawable.gasvector)
                planetType.text = "Gas Planet"
            }
        }

        //Initialises the close button.
        val btnClose: ImageButton = planetDialog.findViewById(R.id.btnPlanetClose)
        btnClose.setOnClickListener(View.OnClickListener {
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
        private val planetName: TextView = planetView.findViewById(R.id.tvName)
        private val planetSize: TextView = planetView.findViewById(R.id.tvSize)
        private val planetTemp: TextView = planetView.findViewById(R.id.tvTemp)
        private val planetDistance: TextView = planetView.findViewById(R.id.tvDistance)
        private val planetGravity: TextView = planetView.findViewById(R.id.tvGravity)
        private val planetImage: ImageView = planetView.findViewById(R.id.ivPlanet)
        private val planetFavourite: ImageButton = planetView.findViewById(R.id.ibFavourite)

        @SuppressLint("SetTextI18n")
        fun bind(planet: Planet){
            planetName.text = planet.name
            planetSize.text = if(planet.size == "Unknown") "Unknown" else planet.size + " x Earth"
            planetTemp.text = if(planet.temperature == "Unknown") "Unknown" else String.format("%.2f°C", planet.temperature.toDouble()-273.15)
            planetDistance.text = if(planet.distance == "Unknown") "Unknown" else String.format("%.2f Light-years", planet.distance.toDouble()*3.26156)
            planetGravity.text = if(planet.gravity == "Unknown") "Unknown" else String.format("%.2f x Earth", (planet.gravity.toDouble() / 2.99))
            planetImage.rotation = planet.rotation

            //Set the planet image and type depending on temperature and density.
            if(planet.temperature == "Unknown"){
                planetImage.setImageResource(R.drawable.unkownvector)
            } else{
                if(planet.temperature.toDouble()-273.15 < -50){
                    planetImage.setImageResource(R.drawable.watervector)
                } else if(planet.temperature.toDouble()-273.15 in -50.0..50.0){
                    planetImage.setImageResource(R.drawable.earthvector)
                } else if(planet.temperature.toDouble()-273.15 in 50.0..100.0) {
                    planetImage.setImageResource(R.drawable.desertvector)
                } else{
                    planetImage.setImageResource(R.drawable.moltenvector)
                }
            }

            //Check whether the planet is a gas planet or not.
            if(planet.density != "Unknown"){
                if(planet.density.toDouble() < 2.0){
                    planetImage.setImageResource(R.drawable.gasvector)
                }
            }
            //Check whether the planet is favourited or not.
            if(MainActivity.userFavourites.contains(planet.id.toString())){
                planetFavourite.setImageResource(R.drawable.ic_baseline_star_24_golden)
            } else{
                planetFavourite.setImageResource(R.drawable.ic_baseline_star_24)
            }
        }

    }
}
