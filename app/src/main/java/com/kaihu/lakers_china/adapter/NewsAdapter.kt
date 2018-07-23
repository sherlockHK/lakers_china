package com.kaihu.lakers_china.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.kaihu.lakers_china.R
import com.kaihu.lakers_china.entity.NewsEntity
import com.kaihu.lakers_china.ui.ArticleDetailActivity
import kotlinx.android.synthetic.main.item_news.view.*

class NewsAdapter(val items : List<NewsEntity>) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        fun bind(item: NewsEntity) {
            Glide.with(view).load(item.img).into(view.video_img)
            view.video_title.text = item.title
            view.video_date.text = item.date
            view.tv_type.text = item.type

            view.setOnClickListener {
                val intent = ArticleDetailActivity.newIntent(view.context!!, item.articlePath)
                view.context.startActivity(intent)
            }
        }
    }
}
