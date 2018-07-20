package com.kaihu.lakers_china.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.webkit.WebSettings
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import com.kaihu.lakers_china.R
import kotlinx.android.synthetic.main.activity_video_play.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup

/**
 * Created by kai on 2018/7/17
 * Email：kaihu1989@gmail.com
 * Feature:
 */
class VideoPlayActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)

        val url = intent.getStringExtra(VIDEO_PAGE_PATH)

        val webSettings = h5_player.settings
        webSettings.javaScriptEnabled = true;
        webSettings.useWideViewPort = true; // 关键点
        webSettings.allowFileAccess = true;
        webSettings.setSupportZoom(true); //
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE; // 不加载缓存内容
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.pluginState = WebSettings.PluginState.ON;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW;
        }


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

    private fun parseVideoPath(url: String) {
        var videoUrl = "<p>加载失败<p/>"
        val vid: String
        val type: String
        if (url.indexOf("youku.com") > 0 && url.indexOf("/id_") > 0) {
            vid = url.split("/id_")[1].split(".")[0]
            videoUrl = "$HOST/player.youku.com/embed/$vid"

            println("videoUrl: $videoUrl")
            h5_player.loadUrl(videoUrl)
        } else if (url.indexOf("v.ums.uc.cn") > 0) {
            videoUrl = url

            println("videoUrl: $videoUrl")
            h5_player.loadUrl(videoUrl)
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

    private fun getPathFromLakersChina(vid: String, type: String) {
        Fuel.get("$HOST/public/geturl/index.php?site=$type&show=json&id=$vid")
                .responseJson(handler = { request, response, result ->
                    println(request.toString())
                    println(response.toString())
                    println(result.toString())
                    val videourl = result.get().array().getJSONObject(0).optString("url")

                    println("videourl: $videourl")
                    h5_player.loadUrl(videourl)
                })
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