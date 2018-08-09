package com.kaihu.lakers_china.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kaihu.lakers_china.R
import com.kaihu.lakers_china.entity.PostReplyEntity

/**
 * Created by kai on 2018/8/8
 * Email：kaihu1989@gmail.com
 * Feature:
 */
class PostReplyAdapter(data: MutableList<PostReplyEntity>?) : BaseQuickAdapter<PostReplyEntity, BaseViewHolder>(R.layout.item_post_reply, data) {

    override fun convert(helper: BaseViewHolder, item: PostReplyEntity) {
        helper.setText(R.id.tv_reply_name, item.username)
                .setText(R.id.tv_reply_owner, item.owner)
                .setText(R.id.tv_reply_time, item.replyTime)
                .setText(R.id.tv_reply_content, item.content)

        val imageView = helper.getView<ImageView>(R.id.iv_reply_avatar)
        Glide.with(imageView).load(item.avatar).apply(RequestOptions().circleCrop()).into(imageView)
    }


}