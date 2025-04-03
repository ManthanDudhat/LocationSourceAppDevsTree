package com.practical.devstree.base

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.practical.devstree.di.PolylineDrawer
import com.practical.devstree.utils.ActivityLauncher
import com.practical.devstree.utils.ActivityLauncher.registerActivityForResult
import javax.inject.Inject

abstract class BaseActivity<VM: ViewModel, VB : ViewBinding> : AppCompatActivity() {

    @Inject
    lateinit var polylineDrawer: PolylineDrawer

    protected lateinit var activityLauncher: ActivityLauncher<Intent, ActivityResult>
    protected lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)
        bindData()
        activityLauncher = registerActivityForResult(this)
    }

    protected abstract fun getViewBinding(): VB
    protected abstract fun  bindData()
}