package com.kaihu.lakers_china

import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.kaihu.lakers_china.entity.ParagraphEntity
import kotlinx.android.synthetic.main.item_article.view.*

class MainAdapter(val items : List<ParagraphEntity>) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        fun bind(item: ParagraphEntity) {
            when(item.type){
                TEXT->{
                    view.item_text.visibility = VISIBLE
                    view.item_image.visibility = GONE
                    view.item_text.text = item.content
                }
                IMG->{
                    view.item_text.visibility = GONE
                    view.item_image.visibility = VISIBLE
                    Glide.with(view).load(item.content).into(view.item_image)
                }
            }

            if (item.isStrong){
                view.item_text.typeface = Typeface.DEFAULT_BOLD
            }else{
                view.item_text.typeface = Typeface.DEFAULT
            }

        }

    }
}
