package com.kaihu.lakers_china.entity

import com.chad.library.adapter.base.entity.SectionEntity

/**
 * Created by kai on 2018/8/10
 * Email：kaihu1989@gmail.com
 * Feature:
 */
class ReplyEntity : SectionEntity<PostReplyEntity>{


    constructor(isHeader: Boolean, header: String) : super(isHeader, header)

    constructor(t: PostReplyEntity) : super(t)
}