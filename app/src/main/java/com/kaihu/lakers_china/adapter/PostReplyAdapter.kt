package com.kaihu.lakers_china.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.View.VISIBLE
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kaihu.lakers_china.R
import com.kaihu.lakers_china.entity.ReplyEntity

/**
 * Created by kai on 2018/8/8
 * Email：kaihu1989@gmail.com
 * Feature:
 */
class PostReplyAdapter constructor(layoutResId: Int, sectionHeadResId: Int, data: List<ReplyEntity>) : BaseSectionQuickAdapter<ReplyEntity, BaseViewHolder>(layoutResId,sectionHeadResId, data) {
    override fun convertHead(helper: BaseViewHolder?, item: ReplyEntity?) {
        helper?.setText(R.id.tv_header, item?.header)
    }

    override fun convert(helper: BaseViewHolder, item: ReplyEntity) {
        val replyItem = item.t
        helper.setVisible(R.id.tv_reply_owner, !TextUtils.isEmpty(replyItem?.owner))
        helper.setText(R.id.tv_reply_name, replyItem?.username)
                .setText(R.id.tv_reply_owner, replyItem?.owner)
                .setText(R.id.tv_reply_time, replyItem?.replyTime)
                .setText(R.id.tv_like, replyItem?.likeNum)
                .setText(R.id.tv_reply_content, replyItem?.content)

        val imageView = helper.getView<ImageView>(R.id.iv_reply_avatar)
        Glide.with(imageView).load(replyItem?.avatar).apply(RequestOptions().circleCrop().placeholder(R.drawable.lakers)).into(imageView)

        val postImg = helper.getView<ImageView>(R.id.iv_post_img)
        val imgUrl = replyItem?.img
        helper.setGone(R.id.iv_post_img, !TextUtils.isEmpty(imgUrl))
        if (TextUtils.isEmpty(imgUrl)) {
            return
        }

        postImg.visibility = VISIBLE
        Glide.with(postImg).asBitmap().load(imgUrl).into(object : SimpleTarget<Bitmap>() {
            override fun onLoadStarted(placeholder: Drawable?) {
                postImg.setImageResource(R.drawable.article_place_holder)
            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                postImg.setImageBitmap(resource)
            }
        })

        if (imgUrl!!.contains(".gif")) {
            val result = imgUrl.substring(0, imgUrl.indexOf(".gif") + ".gif".length)
            Glide.with(postImg).asGif().load(result).into(postImg)
        }
    }


}