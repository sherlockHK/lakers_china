package com.kaihu.lakers_china.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.kaihu.lakers_china.R
import com.kaihu.lakers_china.adapter.ArticleAdapter
import com.kaihu.lakers_china.entity.ParagraphEntity
import kotlinx.android.synthetic.main.acitvity_article_detail.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup

@SuppressLint("Registered")
/**
 * Created by kai on 2018/7/16
 * Email：kaihu1989@gmail.com
 * Feature:文章详情
 */

const val TEXT: Int = -1
const val IMG: Int = -2

class ArticleDetailActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitvity_article_detail)

        val articlePath = intent.getStringExtra(INTENT_ARTICLE_PATH)
        initData(articlePath)
//        initDataWebView(articlePath)
    }

    private fun initDataWebView(path: String?) {
        doAsync {
            val document = Jsoup.connect(path).get()
            val content = document.getElementsByClass("section")[0].select("div.text-content").html().replace("//static", "http://static")

            val linkCss = "<link rel=\"stylesheet\" href=\"file:///android_asset/style.css\" type=\"text/css\">"

            val html = "<html><header>$linkCss</header><body>$content</body></html>"
            uiThread {
                print(html)
                val webSettings = wv_content.settings
                //支持缩放，默认为true。
                webSettings.setSupportZoom(false)
                //调整图片至适合webview的大小
                webSettings.useWideViewPort = true
                // 缩放至屏幕的大小
                webSettings.loadWithOverviewMode = true
                //设置默认编码
                webSettings.defaultTextEncodingName = "utf-8"
                //设置自动加载图片
                webSettings.loadsImagesAutomatically = true

                wv_content.loadData(html, "text/html", "UTF-8");
            }
        }
    }

    var list: ArrayList<ParagraphEntity>? = null
    private fun initData(path: String) {
        doAsync {
            val document = Jsoup.connect(path).get()
            val titleEle = document.select("h1.text-title")
            val contents = document.select("div.text-content")[0]
            val pEles = contents.select("p")

            list = arrayListOf()
            for (e in pEles) {
                var type: Int
                var isStrong = false
                var content: String

                if (e.html().contains("img")) {
                    type = IMG
                    val src = e.select("img")[0].attr("src")
                    content = src.replace("//static", "http://static")
                } else {
                    type = TEXT
                    content = e.select("p")[0].text()
                }
                if (e.html().contains("strong")) {
                    isStrong = true
                }

                val p = ParagraphEntity(type, content, isStrong)
                list?.add(p)
            }

            uiThread {
                println(list)
                article_title.text = titleEle.text()

                article_content.layoutManager = LinearLayoutManager(this@ArticleDetailActivity)
                article_content.adapter = ArticleAdapter(list!!)
            }
        }
    }

    companion object {
        private val INTENT_ARTICLE_PATH = "article_path"
        fun newIntent(context: Context, path: String): Intent {
            val intent = Intent(context, ArticleDetailActivity::class.java)
            intent.putExtra(INTENT_ARTICLE_PATH, path)
            return intent
        }
    }
}