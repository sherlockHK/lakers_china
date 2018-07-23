package com.kaihu.lakers_china.ui

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import com.kaihu.lakers_china.R
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import kotlinx.android.synthetic.main.activity_video_play.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup

/**
 * Created by kai on 2018/7/17
 * Email：kaihu1989@gmail.com
 * Feature: 视频播放
 */
class VideoPlayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)

        val url = intent.getStringExtra(VIDEO_PAGE_PATH)

        initPlayer()
        parseDom(url)
    }

    private fun parseDom(url: String?) {
        doAsync {
            val doc = Jsoup.connect(url)
                    .header("Accept-Encoding", "gzip, deflate")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                    .maxBodySize(0)
                    .timeout(10000)
                    .get()
            val playerBox = doc.select("#player-box")[0]
            val dataUrl = playerBox.attr("data-url")

            uiThread {
                parseVideoPath(dataUrl)
            }
        }
    }

    private fun initPlayer() {
        videoPlayer.titleTextView.visibility = View.VISIBLE
        //设置返回键
        videoPlayer.backButton.visibility = View.VISIBLE
        //设置旋转
        orientationUtils = OrientationUtils(this, videoPlayer)
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        videoPlayer.fullscreenButton.setOnClickListener { orientationUtils?.resolveByClick() }
        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(true)
        //设置返回按键功能
        videoPlayer.backButton.setOnClickListener { onBackPressed() }
    }

    private fun parseVideoPath(url: String) {
        var videoUrl: String
        val vid: String
        val type: String
        if (url.indexOf("youku.com") > 0 && url.indexOf("/id_") > 0) {
            vid = url.split("/id_")[1].split(".")[0]
            videoUrl = "$HOST/player.youku.com/embed/$vid"
            play(videoUrl)
        } else if (url.indexOf("v.ums.uc.cn") > 0) {
            videoUrl = url
            play(videoUrl)
        } else if (url.indexOf("live.qq.com") > 0 && url.indexOf("/v/") > 0) {
            vid = url.split("/v/")[1].split("?")[0]
            type = "qqlive"
            getPathFromLakersChina(vid, type)

        } else if (url.indexOf("weibo.com") > 0) {
            vid = url.split("?fid=")[1].split("&")[0];
            type = "weibo"
            getPathFromLakersChina(vid, type)

        } else if (url.indexOf("miaopai.com") > 0) {
            vid = url.split("/show/")[1].split(".htm")[0];
            type = "miaopai"
            getPathFromLakersChina(vid, type)
        }
    }

    private var orientationUtils: OrientationUtils? = null
    private fun getPathFromLakersChina(vid: String, type: String) {
        Fuel.get("$HOST/public/geturl/index.php?site=$type&show=json&id=$vid")
                .responseJson(handler = { request, response, result ->
                    println(request.toString())
                    println(response.toString())
                    println(result.toString())
                    val videoUrl = result.get().array().getJSONObject(0).optString("url")

                    play(videoUrl)
                })
    }

    private fun play(videoUrl: String?) {
        println("videoUrl: $videoUrl")

        if (TextUtils.isEmpty(videoUrl)) {
            Toast.makeText(this, "视频地址为空", LENGTH_LONG).show()
            return
        }
        videoPlayer.enlargeImageRes = R.drawable.full_screen
        videoPlayer.shrinkImageRes = R.drawable.de_full_screen
        videoPlayer.setUp(videoUrl, true, "测试视频")
        videoPlayer.startPlayLogic()
//        videoPlayer.startWindowFullscreen(this, false,true)
    }

    override fun onPause() {
        super.onPause()
        videoPlayer.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        videoPlayer.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()

        GSYVideoManager.releaseAllVideos()
        if (orientationUtils != null)
            orientationUtils?.releaseListener()
    }

    override fun onBackPressed() {
        //先返回正常状态
        if (orientationUtils?.screenType == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            videoPlayer.fullscreenButton.performClick();
            return
        }
        //释放所有
        videoPlayer.setVideoAllCallBack(null)
        super.onBackPressed()
    }

    companion object {
        private const val VIDEO_PAGE_PATH = "videoPagePath"
        fun newIntent(context: Context, path: String): Intent {
            val intent = Intent(context, VideoPlayActivity::class.java)
            intent.putExtra(VIDEO_PAGE_PATH, path)
            return intent
        }
    }
}