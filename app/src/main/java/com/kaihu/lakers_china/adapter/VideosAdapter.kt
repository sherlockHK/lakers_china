package com.kaihu.lakers_china.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.kaihu.lakers_china.R
import com.kaihu.lakers_china.entity.VideoEntity
import com.kaihu.lakers_china.ui.VideoPlayActivity
import kotlinx.android.synthetic.main.item_videos.view.*

/**
 * Created by kai on 2018/7/19
 * Email：kaihu1989@gmail.com
 * Feature:
 */
class VideosAdapter(private val items : List<VideoEntity>) : RecyclerView.Adapter<VideosAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_videos, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        fun bind(item: VideoEntity) {
            Glide.with(view).load(item.img).into(view.news_img)
            view.news_title.text = item.title
            view.tv_video_type.text = item.videoType
            view.video_date.text = item.date

            view.setOnClickListener {
                val intent = VideoPlayActivity.newIntent(view.context!!, item.videoPagePath)
                intent.putExtra("path", item.videoPagePath)
                view.context.startActivity(intent)
            }
        }
    }
}