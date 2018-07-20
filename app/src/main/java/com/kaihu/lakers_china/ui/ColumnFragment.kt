package com.kaihu.lakers_china.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kaihu.lakers_china.R
import com.kaihu.lakers_china.adapter.ArticleAdapter
import com.kaihu.lakers_china.entity.ParagraphEntity
import kotlinx.android.synthetic.main.fragment_column.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup

/**
 * Created by kai on 2018/7/16
 * Email：kaihu1989@gmail.com
 * Feature:专栏
 */
class ColumnFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_column, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private var list: ArrayList<ParagraphEntity>? = null
    private fun initData() {
        doAsync {
            val document = Jsoup.connect("https://www.lakerschina.com/show/article/855/").get()
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

                article_content.layoutManager = LinearLayoutManager(context)
                article_content.adapter = ArticleAdapter(list!!)
            }
        }
    }
}