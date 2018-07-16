package com.kaihu.lakers_china.App

import android.app.Application
import com.facebook.stetho.Stetho

/**
 * Created by kai on 2018/7/13
 * Email：kaihu1989@gmail.com
 * Feature:
 */
class App : Application(){
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }



}