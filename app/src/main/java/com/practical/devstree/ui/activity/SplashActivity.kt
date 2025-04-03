package com.practical.devstree.ui.activity

import android.os.Handler
import android.os.Looper
import com.practical.devstree.ui.viewmodel.LocationViewModel
import com.practical.devstree.base.BaseActivity
import com.practical.devstree.databinding.ActivitySplashBinding
import com.practical.devstree.utils.startNewActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity<LocationViewModel, ActivitySplashBinding>() {

    override fun getViewBinding() = ActivitySplashBinding.inflate(layoutInflater)

    override fun  bindData() {
        Handler(Looper.getMainLooper()).postDelayed({
            startNewActivity(LocationActivity::class.java,isFinish = true)
        }, 2000)
    }

}