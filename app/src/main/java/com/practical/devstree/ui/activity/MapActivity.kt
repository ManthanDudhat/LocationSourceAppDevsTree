package com.practical.devstree.ui.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.practical.devstree.db.entity.LocationInfo
import com.practical.devstree.ui.viewmodel.LocationViewModel
import com.practical.devstree.R
import com.practical.devstree.base.BaseActivity
import com.practical.devstree.databinding.ActivityMapBinding
import com.practical.devstree.model.Directions
import com.practical.devstree.ui.adapter.SearchAddressAdapter
import com.practical.devstree.ui.repository.PlacesRepository
import com.practical.devstree.utils.Constants
import com.practical.devstree.utils.PathDrawer
import com.practical.devstree.utils.finishActivityWithLauncherResult
import com.practical.devstree.utils.gone
import com.practical.devstree.utils.visible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.gson.Gson
import com.google.maps.android.ui.IconGenerator
import com.practical.devstree.model.MapFunction
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.util.Locale

@AndroidEntryPoint
class MapActivity : BaseActivity<LocationViewModel, ActivityMapBinding>(), OnMapReadyCallback {

    private lateinit var glgMap: GoogleMap
    private var mapFor = MapFunction.ADD.name
    private lateinit var placesRepository: PlacesRepository
    private lateinit var placesClient: PlacesClient
    private lateinit var locationInfoForEdit: LocationInfo
    private lateinit var newLocationInfoForEdit: LocationInfo

    private val searchAddressAdapter by lazy {
        SearchAddressAdapter(itemClickCallback = ::fetchPlaceDetails)
    }

    private fun fetchPlaceDetails(
        placeId: String?,
        primaryAddress: String?,
        mainAddress: String?,
    ) {
        val placeFields =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
        val request = placeId?.let { FetchPlaceRequest.newInstance(it, placeFields) }

        if (request != null) {
            placesClient.fetchPlace(request).addOnSuccessListener { response ->
                val place = response.place
                val latLng = place.latLng
                if (latLng != null) {
                    val lc = LocationInfo(
                        primaryAddress = primaryAddress, city = mainAddress.toString(),
                        latitude = latLng.latitude, longitude = latLng.longitude
                                         )
                    if (mapFor == MapFunction.EDIT.name) {
                        binding.layoutSaveNewAddress.root.visible()
                        newLocationInfoForEdit = LocationInfo(
                            id = locationInfoForEdit.id,
                            primaryAddress = primaryAddress, city = mainAddress.toString(),
                            latitude = latLng.latitude, longitude = latLng.longitude
                                                             )
                        if (::glgMap.isInitialized) {
                            val newLatLng =
                                newLocationInfoForEdit.latitude?.let { lat ->
                                    newLocationInfoForEdit.longitude?.let { lng ->
                                        LatLng(lat, lng)
                                    }
                                }
                            newLatLng?.let { ltLng ->
                                CameraUpdateFactory.newLatLngZoom(ltLng, 12.0f)
                            }?.let { glgMap.animateCamera(it) }
                        }
                    } else {
                        finishActivityWithLauncherResult(bundle = Bundle().apply {
                            putParcelable(
                                Constants.BUNDLE_KEY_ADDRESS_ITEM,
                                lc
                            )
                        })
                    }
                }
            }.addOnFailureListener { exception ->
                exception.printStackTrace()
            }
        }
    }

    override fun getViewBinding() = ActivityMapBinding.inflate(layoutInflater)

    override fun bindData() {
        setUpPlacePicker()
        setUpMap()
        getBundle()
        setAdapter()
        searchingAddress()
        setListeners()
    }

    private fun setUpPlacePicker() {
        if (!Places.isInitialized()) {
            Places.initialize(
                applicationContext,
                "AIzaSyBSNyp6GQnnKlrMr7hD2HGiyF365tFlK5U", Locale.US
            )
        } else {
            placesClient = Places.createClient(this)
            placesRepository = PlacesRepository(placesClient)
        }
    }

    private fun setUpMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setListeners() {
        binding.layoutSaveNewAddress.btnSave.setOnClickListener {
            if (mapFor == MapFunction.EDIT.name) {
                finishActivityWithLauncherResult(bundle = Bundle().apply {
                    putParcelable(
                        Constants.BUNDLE_KEY_ADDRESS_ITEM,
                        newLocationInfoForEdit
                    )
                    putString(Constants.BUNDLE_KEY_MAP_FOR, MapFunction.EDIT.name)
                })
            }
        }
    }

    private fun searchingAddress() {
        binding.layoutAddress.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int,
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (!text.trim().isNullOrEmpty()) {
                    placesRepository.getAutocompletePredictions(text) { predictions ->
                        if (predictions.isNotEmpty()) {
                            searchAddressAdapter.setPredictions(predictions)
                        }
                    }
                }
                if (text.trim().isEmpty()) {
                    searchAddressAdapter.setPredictionsList = emptyList()
                    searchAddressAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun setAdapter() {
        if (mapFor != MapFunction.PATH.name) {
            binding.layoutAddress.rvAddresses.adapter = searchAddressAdapter
        } else {
            //Nothing.
            //Info : only view purpose for marker & path
        }
    }

    private fun getBundle() {
        if (intent.extras?.getString(Constants.BUNDLE_KEY_MAP_FOR) != null) {
            mapFor = intent.extras?.getString(Constants.BUNDLE_KEY_MAP_FOR)!!
            when (mapFor) {
                MapFunction.ADD.name -> {
                    binding.ivPin.gone()
                    binding.layoutSaveNewAddress.root.gone()
                }
                MapFunction.EDIT.name -> {
                    locationInfoForEdit =
                        intent.extras?.getParcelable(Constants.BUNDLE_KEY_LOCATION_ITEM)!!
                    binding.layoutSaveNewAddress.root.gone()
                }
                MapFunction.PATH.name -> {
                    binding.ivPin.gone()
                    binding.layoutSaveNewAddress.root.gone()
                    binding.layoutAddress.root.gone()
                }
                else -> {
                    // none
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        glgMap = googleMap

        when (mapFor) {
            MapFunction.ADD.name -> {
                mapReadyForAdd()
            }
            MapFunction.EDIT.name -> {
                mapReadyForEdit()
            }
            MapFunction.PATH.name -> {
                mapReadyForPath()
            }
            else -> {
                /// none
            }
        }
    }

    private fun mapReadyForPath() {
        val bundle = intent.extras?.getBundle(Constants.INTENT_BUNDLE)
        val mList =
            bundle?.getParcelableArrayList<LocationInfo>(Constants.BUNDLE_KEY_LOCATION_LIST)
        val stopList = if (mList?.size!! > 2) mList.drop(1)?.dropLast(1) else listOf()

        val url = getDirectionsUrl(
            origin = LatLng(
                mList.first()?.latitude?:0.0,
                mList.first()?.longitude?:0.0
            ),
            dest = LatLng(mList.last()?.latitude?:0.0, mList.last()?.longitude?:0.0),
            markerPoints = stopList
        )
        Handler(Looper.getMainLooper()).postDelayed({
            polylineDrawer.downloadTaskExecute(url) {
                drawPolylineIntoMap(it) {
                    addStopsMarker(mList)
                }
            }
        }, 300)
    }

    private fun mapReadyForEdit() {
        Handler(Looper.getMainLooper()).postDelayed({
            val latLng =
                locationInfoForEdit.latitude?.let { lat ->
                    locationInfoForEdit.longitude?.let { lng ->
                        LatLng(lat, lng)
                    }
                }
            latLng?.let { ltLng ->
                CameraUpdateFactory.newLatLngZoom(ltLng, 12.0f)
            }?.let { glgMap.animateCamera(it) }
        }, 100)
    }

    private fun mapReadyForAdd() {
        Handler(Looper.getMainLooper()).postDelayed({
            val india = LatLng(20.5937, 78.9629)
            glgMap.animateCamera(CameraUpdateFactory.newLatLngZoom(india, 4.0f))
        }, 100)
    }

    private fun addStopsMarker(
        mList: List<LocationInfo>?,
    ) {
        mList?.forEachIndexed { index, data ->
            glgMap.addMarker(
                MarkerOptions()
                    .position(LatLng(data.latitude!!, data.longitude!!))
                    .icon(
                        getStopsMarker(
                            context = this@MapActivity,
                            text = mList[index].primaryAddress.toString()
                        )
                    )
            )
        }
    }

    private fun getStopsMarker(context: Context, text: String): BitmapDescriptor {
        val iconGenerator = IconGenerator(context)
        iconGenerator.setBackground(null)
        val inflatedView = View.inflate(context, R.layout.marker_view, null)
        val textView = inflatedView?.findViewById<AppCompatTextView>(R.id.tvStopTitle)
        textView?.text = text
        iconGenerator.setContentView(inflatedView)
        return BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())
    }

    private fun getDirectionsUrl(
        origin: LatLng,
        dest: LatLng,
        markerPoints: List<LocationInfo> = listOf(),
    ): String {
        val strOrigin = "origin=" + origin.latitude + "," + origin.longitude
        val strDest = "destination=" + dest.latitude + "," + dest.longitude
        val sensor = "sensor=false&units=metric&mode=driving&alternatives=false"
        var waypoints = ""
        for ((position, data) in markerPoints.withIndex()) {
            if (position == 0) waypoints = "&waypoints=optimize:false|"
            waypoints = waypoints + data.latitude + "," + data.longitude + "|"
        }
        val parameters = "$strOrigin$waypoints&$strDest&$sensor"
        val output = "json"
        val key =
            "&key=AIzaSyBSNyp6GQnnKlrMr7hD2HGiyF365tFlK5U"

        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters$key"
    }

    private fun drawPolylineIntoMap(
        jObject: JSONObject = JSONObject(),
        durationInSecond: ((Int) -> Unit)? = null,
    ) {
        val directions: Directions? = Gson().fromJson(jObject.toString(), Directions::class.java)
        PathDrawer(this@MapActivity).apply {
            setMap(glgMap)
            if (directions?.routes?.isNotEmpty() == true) {
                newPolyline(directions.routes ?: listOf())
                pathColor(R.color.pathColor)
                pathWidth(5)
                animate(true)
                draw()
                durationInSecond?.invoke(this.durationInSecond)
            } else {
                glgMap.clear()
            }
        }
    }
}