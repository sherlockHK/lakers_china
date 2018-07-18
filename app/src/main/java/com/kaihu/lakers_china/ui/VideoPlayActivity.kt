package com.kaihu.lakers_china.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.kaihu.lakers_china.R

/**
 * Created by kai on 2018/7/17
 * Email：kaihu1989@gmail.com
 * Feature:
 */
class VideoPlayActivity: Activity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)

        val path = intent.getStringExtra("path")

    }

    companion object {
        fun newIntent(context: Context, path:String) : Intent{
            val intent = Intent(context, VideoPlayActivity.javaClass)
            intent.putExtra("path", path)
            return intent
        }
    }
}