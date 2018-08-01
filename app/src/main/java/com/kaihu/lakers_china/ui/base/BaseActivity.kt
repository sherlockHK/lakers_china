package com.kaihu.lakers_china.ui.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.umeng.analytics.MobclickAgent

/**
 * Created by kai on 2018/7/27
 * Email：kaihu1989@gmail.com
 * Feature:
 */
open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }
}