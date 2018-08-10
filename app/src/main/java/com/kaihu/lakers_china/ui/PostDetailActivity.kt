package com.kaihu.lakers_china.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.kaihu.lakers_china.R
import com.kaihu.lakers_china.adapter.ArticleAdapter
import com.kaihu.lakers_china.adapter.PostReplyAdapter
import com.kaihu.lakers_china.entity.ParagraphEntity
import com.kaihu.lakers_china.entity.PostReplyEntity
import com.kaihu.lakers_china.entity.ReplyEntity
import com.kaihu.lakers_china.ui.base.BaseActivity
import kotlinx.android.synthetic.main.acitvity_post_detail.*
import kotlinx.android.synthetic.main.bottom_sheet_reply.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onClick
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup
import org.jsoup.nodes.Element


@SuppressLint("Registered")
/**
 * Created by kai on 2018/7/16
 * Email：kaihu1989@gmail.com
 * Feature:文章详情
 */


class PostDetailActivity : BaseActivity() {
    var postReplyAdapter:PostReplyAdapter ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitvity_post_detail)


        val postPath = intent.getStringExtra(INTENT_POST_PATH)
        val title = intent.getStringExtra(INTENT_POST_TITLE)
        val author = intent.getStringExtra(INTENT_POST_AUTHOR)
        tv_post_title.text = title
        tv_post_author_name.text = author

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        post_back.setOnClickListener { finish() }

        initData(postPath)

        val inflate = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_reply, bsl_reply, false)
        inflate.post_reply?.layoutManager = LinearLayoutManager(this@PostDetailActivity)
        postReplyAdapter = PostReplyAdapter(R.layout.item_post_reply, R.layout.item_reply_header, arrayListOf())
        inflate.post_reply?.adapter = postReplyAdapter

        postReplyAdapter?.setEmptyView(R.layout.layout_loading, inflate.post_reply)
        fab_reply.onClick {
            bsl_reply.showWithSheetView(inflate)
        }
    }

    var list: ArrayList<ParagraphEntity>? = null
    private fun initData(path: String) {
        doAsync {
            val document = Jsoup.connect(path).get()
            val authorDom = document.select("div.detail-author").first()
            val postContentDom = document.select("article.article-content").first()
            val replysDom = document.select("div.m-reply").first()

            val authorAvatar = authorDom.select("img").attr("src")

            list = arrayListOf()
            if (postContentDom != null){
                for (e in postContentDom.children()) {
                    var type: Int
                    var isStrong = false
                    var content: String

                    val text = e.text()
                    if (!TextUtils.isEmpty(text)){
                        type = TEXT
                        content =text
                        if (text.contains("strong")){
                            isStrong = true
                        }else if (text.contains("能看专业及时的新闻")){
                            break
                        }
                    }else {
                        val src = e.select("center img").attr("src")
                        if (!TextUtils.isEmpty(src)){
                            type = IMG
                            content = src
                        }else{
                            continue
                        }
                    }

                    val p = ParagraphEntity(type, content, isStrong)
                    list?.add(p)
                }
            }

            if (list?.size == 0){
                val html = postContentDom.html()
                val content = html.replace("<br>", "")
                list?.add(ParagraphEntity(TEXT, content, false))
            }

            uiThread {
                println(list)
                Glide.with(iv_post_author_img).load(authorAvatar).apply(RequestOptions.bitmapTransform(CircleCrop())).into(iv_post_author_img)

                post_content.layoutManager = LinearLayoutManager(this@PostDetailActivity)
                post_content.adapter = ArticleAdapter(list!!)

                fetchReply(replysDom)
            }
        }
    }

    private fun fetchReply(replyDom: Element) {
        doAsync {
            val brightDom = replyDom.select("div.bright-reply").first()
            val allDom = replyDom.select("div.replay_all").first()
            val brights = brightDom?.select("dl.reply-list")
            val all = allDom.select("dl.reply-list")

            val list = arrayListOf<ReplyEntity>()
            list.add(ReplyEntity(true, "热门评论"))
            if (brights != null) {
                for (e in brights){
                    val userDom = e.getElementsByClass("user-info").first()
                    val avatar = userDom.select("div.avatar a img").attr("data-echo")
                    val username = userDom.getElementsByClass("user-name").text()
                    val owner = userDom.getElementsByClass("reply-own").text()
                    val replyTime = userDom.getElementsByClass("times").text()

                    var likeNum = e.getElementsByClass("bright-number-box").first().text()
                    likeNum = likeNum.substring(1,likeNum.length-1)

                    val content = e.getElementsByClass("short-content").first().text()
                    val img = e.select("center img").attr("src")


                    list.add(ReplyEntity(PostReplyEntity(avatar, username, owner, replyTime, content, img,likeNum)))
                }
            }

            list.add(ReplyEntity(true, "全部评论"))

            for (e in all){
                val userDom = e.getElementsByClass("user-info").first()
                val avatar = userDom.select("div.avatar a img").attr("data-echo")
                val username = userDom.getElementsByClass("user-name").text()
                val owner = userDom.getElementsByClass("reply-own").text()
                val replyTime = userDom.getElementsByClass("times").text()

                var likeNum = e.getElementsByClass("bright-number-box").first().text()
                likeNum = likeNum.substring(1,likeNum.length-1)

                val content = e.getElementsByClass("short-content").first().text()
                val img = e.select("center img").attr("src")

                list.add(ReplyEntity(PostReplyEntity(avatar, username, owner, replyTime, content, img,likeNum)))
            }

            uiThread {
                postReplyAdapter?.setNewData(list)
            }
        }
    }

    companion object {
        private const val INTENT_POST_PATH = "post_path"
        private const val INTENT_POST_TITLE = "post_title"
        private const val INTENT_POST_AUTHOR = "post_author"
        fun newIntent(context: Context, path: String, title: String, author: String): Intent {
            val intent = Intent(context, PostDetailActivity::class.java)
            intent.putExtra(INTENT_POST_PATH, path)
            intent.putExtra(INTENT_POST_TITLE, title)
            intent.putExtra(INTENT_POST_AUTHOR, author)
            return intent
        }
    }
}