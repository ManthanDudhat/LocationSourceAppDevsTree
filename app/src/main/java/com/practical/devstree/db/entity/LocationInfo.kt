package com.practical.devstree.db.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity("location")
data class LocationInfo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var primaryAddress: String? = null,
    var city: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var distance: Double? = null,
    var isPrimary: Boolean = false
) : Parcelable