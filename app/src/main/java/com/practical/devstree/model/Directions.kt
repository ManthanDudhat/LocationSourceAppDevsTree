package com.practical.devstree.model

import com.google.gson.annotations.SerializedName

data class Directions(
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("error_message")
    var errorMessage: String? = null,
    @SerializedName("geocoded_waypoints")
    var geocodedWaypoints: List<GeocodedWaypoints>? = null,
    @SerializedName("routes")
    var routes: List<Routes>? = null
)

data class GeocodedWaypoints(
    @SerializedName("geocoder_status")
    var geocoderStatus: String? = null,
    @SerializedName("place_id")
    var placeId: String? = null,
    @SerializedName("types")
    var types: List<String>? = null
)

data class Routes(
    @SerializedName("bounds")
    var bounds: Bounds? = null,
    @SerializedName("copyrights")
    var copyrights: String? = null,
    @SerializedName("overview_polyline")
    var overviewPolyline: OverviewPolyline? = null,
    @SerializedName("summary")
    var summary: String? = null,
    @SerializedName("legs")
    var legs: List<Legs>? = null,
    @SerializedName("warnings")
    var warnings: List<String>? = null,
    @SerializedName("waypoint_order")
    var waypointOrder: List<String>? = null
)

data class Bounds(
    @SerializedName("northeast")
    var northeast: Northeast? = null,
    @SerializedName("southwest")
    var southwest: Southwest? = null
)

data class Northeast(
    @SerializedName("lat")
    var lat: Double = 0.0,
    @SerializedName("lng")
    var lng: Double = 0.0
)

data class Southwest(
    @SerializedName("lat")
    var lat: Double = 0.0,
    @SerializedName("lng")
    var lng: Double = 0.0
)

data class OverviewPolyline(
    @SerializedName("points")
    var points: String? = null
)

data class Legs(
    @SerializedName("distance")
    var distance: Distance? = null,
    @SerializedName("duration")
    var duration: Duration? = null,
    @SerializedName("end_address")
    var endAddress: String? = null,
    @SerializedName("end_location")
    var endLocation: EndLocation? = null,
    @SerializedName("start_address")
    var startAddress: String? = null,
    @SerializedName("start_location")
    var startLocation: StartLocation? = null,
    @SerializedName("steps")
    var steps: List<Steps>? = null,
    @SerializedName("traffic_speed_entry")
    var trafficSpeedEntry: List<String>? = null,
    @SerializedName("via_waypoint")
    var viaWaypoint: List<String>? = null
)

data class Distance(
    @SerializedName("text")
    var text: String? = null,
    @SerializedName("value")
    var value: Int = 0
)

data class Duration(
    @SerializedName("text")
    var text: String? = null,
    @SerializedName("value")
    var value: Int = 0
)

data class EndLocation(
    @SerializedName("lat")
    var lat: Double = 0.0,
    @SerializedName("lng")
    var lng: Double = 0.0
)

data class StartLocation(
    @SerializedName("lat")
    var lat: Double = 0.0,
    @SerializedName("lng")
    var lng: Double = 0.0
)

data class Steps(
    @SerializedName("distance")
    var distance: Distance? = null,
    @SerializedName("duration")
    var duration: Duration? = null,
    @SerializedName("end_location")
    var endLocation: EndLocation? = null,
    @SerializedName("html_instructions")
    var htmlInstructions: String? = null,
    @SerializedName("polyline")
    var polyline: Polyline? = null,
    @SerializedName("start_location")
    var startLocation: StartLocation? = null,
    @SerializedName("travel_mode")
    var travelMode: String? = null,
    @SerializedName("maneuver")
    var maneuver: String? = null
)

data class Polyline(
    @SerializedName("points")
    var points: String? = null
)