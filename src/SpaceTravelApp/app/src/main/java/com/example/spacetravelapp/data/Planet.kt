package com.example.spacetravelapp.data

/**
 * Dataclass for planets.
 *
 * @author Shane Waters
 * @version 1.0 (24/04/2021)
 */

data class Planet(
    var id: Int,
    var name: String,
    var size: String,
    var orbit: String,
    var stars: String,
    var gravity: String,
    var temperature: String,
    var distance: String,
    var rotation: Float,
    var ra: String,
    var dec: String,
    var mass: String,
    var density: String
)

