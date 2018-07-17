package com.kaihu.lakers_china.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kaihu.lakers_china.R

/**
 * Created by kai on 2018/7/16
 * Email：kaihu1989@gmail.com
 * Feature:
 */
class VideoFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_video, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}