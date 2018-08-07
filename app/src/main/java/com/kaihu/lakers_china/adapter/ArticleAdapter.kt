package com.kaihu.lakers_china.adapter

import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.kaihu.lakers_china.R
import com.kaihu.lakers_china.entity.ParagraphEntity
import com.kaihu.lakers_china.ui.IMG
import com.kaihu.lakers_china.ui.TEXT
import kotlinx.android.synthetic.main.item_article.view.*


class ArticleAdapter(val items: List<ParagraphEntity>) : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: ParagraphEntity) {
            when (item.type) {
                TEXT -> {
                    view.item_text.visibility = VISIBLE
                    view.item_image.visibility = GONE
                    view.item_text.text = item.content
                }
                IMG -> {
                    view.item_text.visibility = GONE
                    view.item_image.visibility = VISIBLE
                    val url = item.content

                    Glide.with(view).asBitmap().load(url).into(object : SimpleTarget<Bitmap>() {
                        override fun onLoadStarted(placeholder: Drawable?) {
                            view.item_image.setImageResource(R.drawable.article_place_holder)
                        }

                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            view.item_image.setImageBitmap(resource)
                        }
                    })

                    if (url.contains(".gif")) {
                        val result = url.substring(0, url.indexOf(".gif") + ".gif".length)
                        Glide.with(view).asGif().load(result).into(view.item_image)
                    }
                }
            }

            if (item.isStrong) {
                view.item_text.typeface = Typeface.DEFAULT_BOLD
            } else {
                view.item_text.typeface = Typeface.DEFAULT
            }

        }

    }
}
