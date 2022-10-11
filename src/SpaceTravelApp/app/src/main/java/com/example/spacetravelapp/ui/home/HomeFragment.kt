package com.example.spacetravelapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.spacetravelapp.R
import com.example.spacetravelapp.databinding.FragmentHomeBinding
import com.example.spacetravelapp.databinding.FragmentPlanetarchiveBinding

/**
 * The Home Fragment class for the app.
 *
 * @author Shane Waters
 * @version 1.0 (24/04/2021)
 */
class HomeFragment : Fragment() {

    private lateinit var homeFragment: FragmentHomeBinding

    /**
     * Initialises the Fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeFragment = FragmentHomeBinding.inflate(inflater, container, false)

        return homeFragment.root
    }
}