package com.practical.devstree.di

import android.app.Application
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp
import kotlin.text.Typography.dagger

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Places.initialize(this, "AIzaSyBSNyp6GQnnKlrMr7hD2HGiyF365tFlK5U")
    }

}