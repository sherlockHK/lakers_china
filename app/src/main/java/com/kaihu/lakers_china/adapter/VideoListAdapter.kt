package com.kaihu.lakers_china.adapter

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import com.kaihu.lakers_china.R
import com.kaihu.lakers_china.entity.VideoEntity
import com.kaihu.lakers_china.ui.HOST_LAKERS_CHINA
import com.shuyu.gsyvideoplayer.utils.GSYVideoHelper
import kotlinx.android.synthetic.main.item_video_list.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup

/**
 * Created by kai on 2018/7/19
 * Email：kaihu1989@gmail.com
 * Feature:
 */
const val TAG: String = "VideoListAdapter"

class VideoListAdapter(private val items: List<VideoEntity>, smallVideoHelper: GSYVideoHelper, gsySmallVideoHelperBuilder: GSYVideoHelper.GSYVideoHelperBuilder) : RecyclerView.Adapter<VideoListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    val smallVideoHelper = smallVideoHelper
    val gsySmallVideoHelperBuilder = gsySmallVideoHelperBuilder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], smallVideoHelper)
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: VideoEntity, smallVideoHelper: GSYVideoHelper) {
            view.video_title.text = item.title
            view.tv_video_type.text = item.videoType
            view.video_date.text = item.date

            //增加封面
            val imageView = ImageView(view.context)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(view).load(item.img).into(imageView)

            smallVideoHelper.addVideoPlayer(position, imageView, TAG, view.list_item_container, view.list_item_btn)

            view.findViewById<ImageView>(R.id.list_item_btn).setOnClickListener {
                notifyDataSetChanged()
                smallVideoHelper.setPlayPositionAndTag(position, TAG)
                parseDom(item.videoPagePath, item.title)
            }
        }

        private fun parseDom(url: String?, title: String) {
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
                    val videoUrl = parseVideoPath(dataUrl, title)
                    if (!TextUtils.isEmpty(videoUrl)) {
                        play(videoUrl, title)
                    }
                }
            }
        }

        private fun play(videoUrl: String, title: String) {
            gsySmallVideoHelperBuilder.setVideoTitle(title)
                    .setUrl(videoUrl)
            smallVideoHelper.startPlay()
        }

        private fun parseVideoPath(url: String, title: String): String {
            var videoUrl = ""
            val vid: String
            val type: String
            if (url.indexOf("youku.com") > 0 && url.indexOf("/id_") > 0) {
                vid = url.split("/id_")[1].split(".")[0]
                videoUrl = "$HOST_LAKERS_CHINA/player.youku.com/embed/$vid"
            } else if (url.indexOf("v.ums.uc.cn") > 0) {
                videoUrl = url
            } else if (url.indexOf("live.qq.com") > 0 && url.indexOf("/v/") > 0) {
                vid = url.split("/v/")[1].split("?")[0]
                type = "qqlive"
                getPathFromLakersChina(vid, type, title)

            } else if (url.indexOf("weibo.com") > 0) {
                vid = url.split("?fid=")[1].split("&")[0];
                type = "weibo"
                getPathFromLakersChina(vid, type, title)

            } else if (url.indexOf("miaopai.com") > 0) {
                vid = url.split("/show/")[1].split(".htm")[0];
                type = "miaopai"
                getPathFromLakersChina(vid, type, title)
            }
            return videoUrl
        }

        private fun getPathFromLakersChina(vid: String, type: String, title: String) {
            Fuel.get("$HOST_LAKERS_CHINA/public/geturl/index.php?site=$type&show=json&id=$vid")
                    .responseJson(handler = { request, response, result ->
                        println(request.toString())
                        println(response.toString())
                        println(result.toString())
                        val videoUrl = result.get().array().getJSONObject(0).optString("url")

                        play(videoUrl, title)
                    })
        }
    }


}