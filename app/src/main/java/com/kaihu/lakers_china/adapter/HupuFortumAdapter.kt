package com.kaihu.lakers_china.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.kaihu.lakers_china.R
import com.kaihu.lakers_china.entity.HupuForumItemEntity
import kotlinx.android.synthetic.main.item_hupu.view.*

class HupuFortumAdapter(val items: List<HupuForumItemEntity>) : RecyclerView.Adapter<HupuFortumAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hupu, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: HupuForumItemEntity) {
            view.tv_hupu_title.text = item.title
            view.tv_hupu_date.text = item.date
            view.tv_hupu_author.text = item.author

            view.setOnClickListener {
                Toast.makeText(view.context, item.articlePath,LENGTH_SHORT).show()
            }
        }
    }
}
