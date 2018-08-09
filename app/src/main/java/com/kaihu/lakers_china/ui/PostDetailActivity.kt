package com.kaihu.lakers_china.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.kaihu.lakers_china.R
import com.kaihu.lakers_china.adapter.ArticleAdapter
import com.kaihu.lakers_china.adapter.PostReplyAdapter
import com.kaihu.lakers_china.entity.ParagraphEntity
import com.kaihu.lakers_china.entity.PostReplyEntity
import com.kaihu.lakers_china.ui.base.BaseActivity
import kotlinx.android.synthetic.main.acitvity_post_detail.*
import org.jetbrains.anko.doAsync
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

        post_reply.layoutManager = LinearLayoutManager(this@PostDetailActivity)
        post_reply.adapter = PostReplyAdapter(replyList!!)
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

    var replyList: ArrayList<PostReplyEntity>? = arrayListOf()
    private fun fetchReply(replyDom: Element) {
        doAsync {
            val brightDom = replyDom.select("div.bright-reply").first()
            val brights = brightDom.select("dl.reply-list")

            val list = arrayListOf<PostReplyEntity>()
            for (e in brights){
                val userDom = e.getElementsByClass("user-info").first()
                val avatar = userDom.select("div.avatar a img").attr("data-echo")
                val username = userDom.getElementsByClass("user-name").text()
                val owner = userDom.getElementsByClass("replu-own").text()
                val replyTime = userDom.getElementsByClass("times").text()

                val content = e.getElementsByClass("short-content").first().text()

                list?.add(PostReplyEntity(avatar,username,owner,replyTime,content))
            }

            uiThread {
                replyList?.clear()
                replyList?.addAll(list)
                post_reply.adapter.notifyDataSetChanged()
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