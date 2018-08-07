package com.kaihu.lakers_china.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import com.kaihu.lakers_china.R
import com.kaihu.lakers_china.adapter.ArticleAdapter
import com.kaihu.lakers_china.entity.ParagraphEntity
import com.kaihu.lakers_china.ui.base.BaseActivity
import kotlinx.android.synthetic.main.acitvity_post_detail.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup


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
        post_title.text = title

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        post_back.setOnClickListener { finish() }

        initData(postPath)
//        initData("$HOST_HUPU_BBS/23042145.html")
    }

    var list: ArrayList<ParagraphEntity>? = null
    private fun initData(path: String) {
        doAsync {
            val document = Jsoup.connect(path).get()
            val articleContent = document.select("article.article-content")[0]
            val pEles = document.select("div.quote-content")
            list = arrayListOf()
            if (articleContent != null){
                for (e in articleContent.children()) {
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
            }else {
            }


            uiThread {
                println(list)

                article_content.layoutManager = LinearLayoutManager(this@PostDetailActivity)
                article_content.adapter = ArticleAdapter(list!!)
            }
        }
    }

    companion object {
        private const val INTENT_POST_PATH = "post_path"
        private const val INTENT_POST_TITLE = "post_title"
        fun newIntent(context: Context, path: String, title: String): Intent {
            val intent = Intent(context, PostDetailActivity::class.java)
            intent.putExtra(INTENT_POST_PATH, path)
            intent.putExtra(INTENT_POST_TITLE, title)
            return intent
        }
    }
}