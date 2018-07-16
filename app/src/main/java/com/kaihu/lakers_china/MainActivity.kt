package com.kaihu.lakers_china

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.kaihu.lakers_china.entity.ParagraphEntity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup

const val TEXT: Int = -1
const val IMG: Int = -2

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                message.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                message.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        initData()
    }

    private fun initData() {
        doAsync {
            val document = Jsoup.connect("https://www.lakerschina.com/show/article/855/").get()
            val titleEle = document.select("h1.text-title")
            val contents = document.select("div.text-content")[0]
            val pEles = contents.select("p")

            var list: ArrayList<ParagraphEntity> = arrayListOf()
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

//                if (e.html() == "<strong>原文：</strong>") {
//                    break
//                }

                val p = ParagraphEntity(type, content, isStrong)
                list.add(p)
            }

            uiThread {
                println(list)
                article_title.text = titleEle.text()

                article_content.layoutManager = LinearLayoutManager(this@MainActivity)
                article_content.adapter = MainAdapter(list)
            }
        }
    }
}
