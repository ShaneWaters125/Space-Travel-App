package com.example.spacetravelapp.recycleviewerProfiles

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spacetravelapp.MainActivity
import com.example.spacetravelapp.ProfilesActivity
import com.example.spacetravelapp.R
import com.example.spacetravelapp.data.User

/**
 * The RecycleViewAdapter for the profiles class for the app.
 * Is responsible for initialising the view holders inside the recycle viewer and binding the data. Also initialises the functionality of the view holders.
 *
 * @author Shane Waters
 * @version 1.0 (24/04/2021)
 */
class RecycleViewAdapterProfiles  : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var accounts: List<User> = ArrayList()

    /**
     * Sets the layout of the view holder to R.layout.saved_planet_entry.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProfileViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.profile_entry, parent, false)
        )
    }

    /**
     * Binds the view holder class to the view holder object. Initialises the view holders functionality.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is RecycleViewAdapterProfiles.ProfileViewHolder ->{
                holder.bind(accounts[position])
                holder.itemView.setOnClickListener{
                    //Setting global user variables.
                    MainActivity.userId = accounts[position].id.toString()
                    MainActivity.userName = accounts[position].userName
                    MainActivity.userFavourites = getFavourites(accounts[position].favourites)
                    MainActivity.userJourneys = getJourneys(accounts[position].journeys)
                    //Getting the context of the profile activity so it can be closed.
                    val profileIntent = ProfilesActivity.pIntent as Activity
                    //Closes the side navigation so the name can be updated.
                    MainActivity.closeDrawer()
                    //Closes the profile activity.
                    profileIntent.finish()
                }
            }
        }
    }

    /**
     * Returns the amount of items in the recycle viewer.
     */
    override fun getItemCount(): Int {
        return accounts.size
    }

    /**
     * Submits the data to recycle viewer to show.
     */
    fun submitList(accountList: List<User>){
        accounts = accountList
        notifyDataSetChanged()
    }

    /**
     * Parses the database string of favourited planets to add them to the list of favourited planets.
     * @param fav The string of favourited planet from the database.
     */
    private fun getFavourites(fav: String): ArrayList<String>{
        var stringBuffer: String = ""
        val favouritePlanets: ArrayList<String> = ArrayList()
        //If the current character is a number then keep adding it a string, if its not then it is a ',' which denotes a new planet id.
        //Once the planet id is deduced add it to the list of planet ids.
        for((index, char) in fav.withIndex()){
            if(char.isDigit()){
                stringBuffer += char
                if(index == fav.length-1){
                    favouritePlanets.add(stringBuffer)
                }
            } else{
                favouritePlanets.add(stringBuffer)
                stringBuffer = ""
            }
        }
        return favouritePlanets
    }

    /**
     * Parses the database string of journeys to add them to the list of journeys.
     * @param journeys The string of journeys from the database.
     */
    private fun getJourneys(journeys: String): ArrayList<ArrayList<String>>{
        var stringBuffer: String = ""
        val journeysList: ArrayList<ArrayList<String>> = ArrayList()
        val journey: ArrayList<String> = ArrayList()
        //Same as getFavourites but if a '/' is found the add the list to the list of journeys then start a new list.
        for((index, char) in journeys.withIndex()){
            if(char == ','){
                journey.add(stringBuffer)
                stringBuffer = ""
            }
            if(char == '/'){
                journey.add(stringBuffer)
                val copyArray = ArrayList<String>()
                for(item in journey){
                    copyArray.add(item)
                }
                journeysList.add(copyArray)
                journey.clear()
                stringBuffer = ""
            }
            if(char.isDigit()){
                stringBuffer += char
            }
            if(index == journey.size-1){
                journey.add(stringBuffer)
                journeysList.add(journey)
            }
        }
        return journeysList
    }

    /**
     * View Holder class which is responsible for binding the data to the view holder.
     */
    class ProfileViewHolder constructor(
            accountView: View
    ): RecyclerView.ViewHolder(accountView){
        private val profileName: TextView = accountView.findViewById(R.id.tvProfileName)

        @SuppressLint("SetTextI18n")
        fun bind(profile: User){
            profileName.text = profile.userName
        }

    }

}