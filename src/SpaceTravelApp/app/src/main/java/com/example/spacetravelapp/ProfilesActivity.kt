package com.example.spacetravelapp

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spacetravelapp.data.DatabaseHandler
import com.example.spacetravelapp.data.User
import com.example.spacetravelapp.recycleviewerArchive.SideSpacingItemDecoration
import com.example.spacetravelapp.recycleviewerArchive.TopSpacingItemDecoration
import com.example.spacetravelapp.recycleviewerProfiles.RecycleViewAdapterProfiles
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import org.w3c.dom.Text

/**
 * The ProfilesActivity class for the app.
 * Is responsible for initialising the recycle viewer, buttons and allows the user to select a profile.
 *
 * @author Shane Waters
 * @version 1.0 (24/04/2021)
 */
class ProfilesActivity : AppCompatActivity() {

    private lateinit var profileAdapter: RecycleViewAdapterProfiles

    companion object{
        //This provides context of the activity to the recycle viewer so it can close after selecting a profile.
        lateinit var pIntent: Context
    }

    /**
     * Initialises the Activity and recycle viewer.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profiles)
        setSupportActionBar(findViewById(R.id.toolbar))
        actionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRecycleView()

        //Load the create a new profile dialog window when pressing the button.
        val btnCreateProfile = findViewById<Button>(R.id.btnCreateProfile)
        btnCreateProfile.setOnClickListener(){
            profileDialog()
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
        val recyclerViewer = findViewById<RecyclerView>(R.id.recycleviewer_profile)

        recyclerViewer.apply{
            layoutManager = LinearLayoutManager(context)
            //This provides context of the activity to the recycle viewer so it can close after selecting a profile.
            pIntent = context
            val topSpacingDecoration = TopSpacingItemDecoration(20)
            val sideSpacingItemDecoration = SideSpacingItemDecoration(20)
            addItemDecoration(topSpacingDecoration)
            addItemDecoration(sideSpacingItemDecoration)
            profileAdapter = RecycleViewAdapterProfiles()
            adapter = profileAdapter
        }
        //Sets the contents of the recycle viewer.
        addData()
    }

    /**
     * Initialises the create a profile dialog.
     */
    private fun profileDialog(){
        val profileDialog = Dialog(this, R.style.DialogTheme)
        profileDialog.setContentView(R.layout.dialog_profile)
        profileDialog.show()

        val btnSaveProfile = profileDialog.findViewById<Button>(R.id.btnSaveProfile)
        val etUsername = profileDialog.findViewById<TextView>(R.id.etUsername)
        //When the user presses the create button, the DatabaseHandler is called to add the new profile into the database.
        btnSaveProfile.setOnClickListener(){
            val database = DatabaseHandler(this)
            if(etUsername.text.toString() == ""){
                Toast.makeText(this, "Name cannot be empty!", Toast.LENGTH_SHORT).show()
            } else{
                database.addUser(etUsername.text.toString())
                addData()
                profileDialog.dismiss()
            }
        }
    }

    /**
     * Sends the data to the recycle viewer to display.
     */
    private fun addData(){
        var data = mutableListOf<User>()
        val database = DatabaseHandler(this)
        data = database.viewUsers()
        profileAdapter.submitList(data)
    }
}