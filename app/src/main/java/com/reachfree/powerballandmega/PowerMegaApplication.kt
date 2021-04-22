package com.reachfree.powerballandmega

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PowerMegaApplication : Application() {

    override fun onCreate() {
        MobileAds.initialize(this)
        super.onCreate()
    }

}