package com.practical.devstree.utils

import com.google.android.gms.maps.model.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

fun haversineDistanceAlgorithm(pointA: LatLng, pointB: LatLng): Double {
    val earthRadius = 6400.0
    val latA = Math.toRadians(pointA.latitude)
    val lonA = Math.toRadians(pointA.longitude)
    val latB = Math.toRadians(pointB.latitude)
    val lonB = Math.toRadians(pointB.longitude)
    val dLat = latB - latA
    val dLon = lonB - lonA
    val a = sin(dLat / 2).pow(2) + cos(latA) * cos(latB) * sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}