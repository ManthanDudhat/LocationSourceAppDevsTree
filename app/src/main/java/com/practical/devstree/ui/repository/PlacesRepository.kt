package com.practical.devstree.ui.repository

import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient


class PlacesRepository(private val placesClient: PlacesClient) {
    fun getAutocompletePredictions(
        query: String,
        callback: (List<AutocompletePrediction>) -> Unit
    ) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            callback(response.autocompletePredictions)
        }.addOnFailureListener { exception ->
            exception.printStackTrace()
            callback(emptyList())
        }
    }
}
