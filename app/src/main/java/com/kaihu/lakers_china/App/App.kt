package com.kaihu.lakers_china.App

import android.app.Application
import com.facebook.stetho.Stetho
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure

/**
 * Created by kai on 2018/7/13
 * Email：kaihu1989@gmail.com
 * Feature:
 */
class App : Application(){
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)

        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "pushSecret")
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL)
    }



}