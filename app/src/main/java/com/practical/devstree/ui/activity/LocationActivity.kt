package com.practical.devstree.ui.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.practical.devstree.db.entity.LocationInfo
import com.practical.devstree.ui.adapter.LocationAdapter
import com.practical.devstree.ui.viewmodel.LocationViewModel
import com.practical.devstree.R
import com.practical.devstree.base.BaseActivity
import com.practical.devstree.databinding.ActivityLocationBinding
import com.practical.devstree.utils.Constants
import com.practical.devstree.utils.haversineDistanceAlgorithm
import com.practical.devstree.utils.startActivityWithLauncher
import com.practical.devstree.utils.startNewActivity
import com.practical.devstree.utils.withNotNull
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.practical.devstree.model.MapFunction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocationActivity : BaseActivity<LocationViewModel, ActivityLocationBinding>() {

    private val viewModel: LocationViewModel by viewModels()
    private var isAscending = true
    private val locationAdapter by lazy {
        LocationAdapter(onEventListener = { view, adapterPos ->
            onEventListener(view, adapterPos)
        })
    }

    private fun onEventListener(view: View, adapterPos: Int) {
        when (view.id) {
            R.id.ivDelete -> {
                showDeleteConfirmationDialog(adapterPos)
            }

            R.id.ivEdit -> {
                activityLauncher.startActivityWithLauncher(
                    context = this,
                    MapActivity::class.java,
                    bundle = bundleOf(
                        Constants.BUNDLE_KEY_MAP_FOR to MapFunction.EDIT.name,
                        Constants.BUNDLE_KEY_LOCATION_ITEM to locationAdapter.mList[adapterPos]
                    )
                ) {
                    if (it.resultCode == RESULT_OK) {
                        if (it.data?.extras?.getString(Constants.BUNDLE_KEY_MAP_FOR) != null &&
                            it.data?.extras?.getString(Constants.BUNDLE_KEY_MAP_FOR) ==
                            MapFunction.EDIT.name
                        ) {
                            it.data?.extras?.getParcelable<LocationInfo>(Constants.BUNDLE_KEY_ADDRESS_ITEM)
                                .withNotNull {
                                    locationAdapter.mList.find { it.isPrimary == true }?.let { item ->
                                        val distance = haversineDistanceAlgorithm(
                                            LatLng(item.latitude ?: 0.0, item.longitude ?: 0.0),
                                            LatLng(
                                                it.latitude ?: 0.0,
                                                it.longitude ?: 0.0
                                            )
                                                                                 )
                                        it.distance = distance
                                    }
                                    locationAdapter.notifyDataSetChanged()
                                    dbUpdateLocation(it)
                                }
                            getAllLocations()
                        }
                    }
                }
            }
        }
    }

    override fun getViewBinding() = ActivityLocationBinding.inflate(layoutInflater)

    override fun bindData() {
        setListeners()
        setAdapter()
        getAllLocations()
        flowObserve()
    }

    private fun setAdapter() {
        binding.rvAddresses.adapter = locationAdapter
    }

    private fun setListeners() = with(binding) {
        btnSort.setOnClickListener { manageSortingDialog() }
        btnPOI.setOnClickListener { onClickButtonPOI() }
        btnPath.setOnClickListener { onClickButtonPath() }
    }

    private fun onClickButtonPOI() {
        activityLauncher.startActivityWithLauncher(
            this,
            MapActivity::class.java,
            bundle = bundleOf(
                Constants.BUNDLE_KEY_MAP_FOR to MapFunction.ADD.name
            ),
        ) {
            if (it.resultCode == RESULT_OK) {
                it.data?.extras?.getParcelable<LocationInfo>(Constants.BUNDLE_KEY_ADDRESS_ITEM)
                    .withNotNull { locationEntity ->
                        locationAdapter.mList.find { it.isPrimary == true }?.let { item ->
                            val distance = haversineDistanceAlgorithm(
                                LatLng(item.latitude ?: 0.0, item.longitude ?: 0.0),
                                LatLng(
                                    locationEntity.latitude ?: 0.0,
                                    locationEntity.longitude ?: 0.0
                                )
                                                                     )
                            locationEntity.distance = distance
                        }
                        locationAdapter.mList.add(locationEntity)
                        dbAddLocation(locationEntity)

                        if (locationAdapter.mList.size == 1) {
                            locationAdapter.mList[0].isPrimary = true
                            dbMarkAsPrimary(locationEntity.id)
                            locationAdapter.notifyDataSetChanged()
                        }
                    }
                getAllLocations()
            }
        }
    }

    private fun onClickButtonPath() {
        if (locationAdapter.mList.size > 1) {
            val bundle = Bundle()
            bundle.putParcelableArrayList(
                Constants.BUNDLE_KEY_LOCATION_LIST,
                locationAdapter.mList
            )
            startNewActivity(
                MapActivity::class.java,
                bundle = bundleOf(
                    Constants.BUNDLE_KEY_MAP_FOR to MapFunction.PATH.name,
                    Constants.INTENT_BUNDLE to bundle
                )
            )
        } else {
            Snackbar.make(
                findViewById(android.R.id.content),
                getString(R.string.validation_please_add_2_or_more_locations),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun manageSortingDialog() {
        sortingDialog(
            selectedPos = isAscending, isAscendingCallback = { selectedPos ->
                isAscending = if (selectedPos == 0) {
                    getLocationsDistanceAscending()
                    true
                } else {
                    getLocationsDistanceDescending()
                    false
                }
            }
                     )
    }

    private fun sortingDialog(
        selectedPos: Boolean = true,
        isAscendingCallback: (selectedPos: Int) -> Unit,
                             ) {
        AlertDialog.Builder(this)
            .setTitle("Sorting by..")
            .setSingleChoiceItems(
                arrayOf("Ascending location", "Descending location"),
                if (selectedPos) 0 else 1) { dialog: DialogInterface, index: Int ->
                dialog.dismiss()
                if (index == 0) {
                    isAscendingCallback.invoke(0)
                } else {
                    isAscendingCallback.invoke(1)
                }
            }.create().show()
    }

    private fun showDeleteConfirmationDialog(adapterPos: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.label_delete_location))
        builder.setMessage(getString(R.string.dialog_label_delete_location))
        builder.setPositiveButton(getString(R.string.dialog_label_delete)) { dialog, which ->
            deleteLocation(adapterPos)
        }
        builder.setNegativeButton(getString(R.string.dialog_label_cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteLocation(adapterPos: Int) {
        if (locationAdapter.mList[adapterPos].isPrimary) {
            if (locationAdapter.mList.size == 1) {
                dbDeleteLocation(locationAdapter.mList[adapterPos])
                removePrimaryMark()
            } else {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(R.string.validation_primary_location),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        } else {
            dbDeleteLocation(locationAdapter.mList[adapterPos])
        }

    }

    private fun dbAddLocation(locationInfo: LocationInfo) {
        viewModel.addLocation(locationInfo)
    }

    private fun dbUpdateLocation(locationInfo: LocationInfo) {
        viewModel.updateLocation(
            id = locationInfo.id,
            primaryAddress = locationInfo.primaryAddress.toString(),
            city = locationInfo.city.toString(),
            latitude = locationInfo.latitude ?: 0.0,
            longitude = locationInfo.longitude ?: 0.0,
            distance = locationInfo.distance ?: 0.0
        )
    }

    private fun dbDeleteLocation(locationInfo: LocationInfo) {
        viewModel.deleteLocation(locationInfo)
    }

    private fun getAllLocations() {
        viewModel.getAllLocations()
    }

    private fun dbMarkAsPrimary(id: Int) {
        viewModel.markAsPrimary(id)
    }

    private fun removePrimaryMark() {
        viewModel.removePrimaryMark()
    }

    private fun getLocationsDistanceAscending() {
        viewModel.getLocationsDistanceAscending()
    }

    private fun getLocationsDistanceDescending() {
        viewModel.getLocationsDistanceDescending()
    }

    private fun flowObserve() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.getAllLocationsFlow.collect { response ->
                        if (response != null) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                locationAdapter.mList.clear()
                                locationAdapter.mList.addAll(response)
                                locationAdapter.notifyDataSetChanged()
                            }, 300)
                        }
                    }
                }
                launch {
                    viewModel.getLocationsDistanceAscFlow.collect { response ->
                        if (response != null) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                locationAdapter.mList.clear()
                                locationAdapter.mList.addAll(response)
                                locationAdapter.notifyDataSetChanged()
                            }, 300)
                        }
                    }
                }
                launch {
                    viewModel.getLocationsDistanceDesFlow.collect { response ->
                        if (response != null) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                locationAdapter.mList.clear()
                                locationAdapter.mList.addAll(response)
                                locationAdapter.notifyDataSetChanged()
                            }, 300)
                        }
                    }
                }
            }
        }
    }
}