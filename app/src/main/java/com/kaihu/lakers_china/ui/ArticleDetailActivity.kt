package com.kaihu.lakers_china.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
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

class ArticleDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitvity_article_detail)


        val articlePath = intent.getStringExtra(INTENT_ARTICLE_PATH)
        val articleImg = intent.getStringExtra(INTENT_ARTICLE_IMG)

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            val drawable = resources.getDrawable(R.drawable.return_back)
            val bitmap = (drawable as BitmapDrawable).bitmap
            val newdrawable = BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, 80, 80, true))
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(newdrawable)
        }

        Glide.with(this).load(articleImg).into(article_img)

        Glide.with(this)
                .load(articleImg)
                .apply(RequestOptions().centerCrop().dontAnimate())
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }
                })
                .into(article_img)

        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar)
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar)

        initData(articlePath)
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
                    content = src.replace("//static", "https://static")
                } else {
                    type = TEXT
                    content = "\t\t\t\t" + e.select("p")[0].text()
                }
                if (e.html().contains("strong")) {
                    isStrong = true
                }

                val p = ParagraphEntity(type, content, isStrong)
                list?.add(p)
            }

            uiThread {
                println(list)
                collapsingToolbarLayout.title = titleEle.text()

                article_content.layoutManager = LinearLayoutManager(this@ArticleDetailActivity)
                article_content.adapter = ArticleAdapter(list!!)
            }
        }
    }

    companion object {
        private const val INTENT_ARTICLE_PATH = "article_path"
        private const val INTENT_ARTICLE_IMG = "article_img"
        fun newIntent(context: Context, path: String, img: String): Intent {
            val intent = Intent(context, ArticleDetailActivity::class.java)
            intent.putExtra(INTENT_ARTICLE_PATH, path)
            intent.putExtra(INTENT_ARTICLE_IMG, img)
            return intent
        }
    }
}