package com.kaihu.lakers_china.adapter

import android.animation.Animator
import android.view.animation.BounceInterpolator
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kaihu.lakers_china.R
import com.kaihu.lakers_china.entity.HupuMenuEntity

/**
 * Created by kai on 2018/8/10
 * Email：kaihu1989@gmail.com
 * Feature:
 */
class PostMenuAdapter : BaseQuickAdapter<HupuMenuEntity, BaseViewHolder>(R.layout.item_hupu_menu) {
    override fun convert(helper: BaseViewHolder?, item: HupuMenuEntity?) {
        helper?.setText(R.id.tv_hupu_menu_title, item?.name)
        if (item?.img_res == 0){
            helper?.setVisible(R.id.iv_hupu_menu_icon, false)
        }else{
            helper?.setVisible(R.id.iv_hupu_menu_icon, true)
            helper?.setImageResource(R.id.iv_hupu_menu_icon, item!!.img_res)
        }
    }

    override fun startAnim(anim: Animator?, index: Int) {
        anim?.startDelay = index/2 * 100L
        super.startAnim(anim, index)
        anim?.interpolator = BounceInterpolator()
    }
}