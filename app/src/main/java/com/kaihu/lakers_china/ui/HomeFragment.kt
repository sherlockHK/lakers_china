package com.kaihu.lakers_china.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.kaihu.lakers_china.R
import com.kaihu.lakers_china.entity.BannerEntity
import com.kaihu.lakers_china.utils.GlideImageLoader
import com.youth.banner.BannerConfig
import com.youth.banner.listener.OnBannerListener
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup


/**
 * Created by kai on 2018/7/16
 * Email：kaihu1989@gmail.com
 * Feature:home
 */
const val HOST:String = "https://www.lakerschina.com"
class HomeFragment : Fragment(), OnBannerListener {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
    }

    var list: ArrayList<BannerEntity> = arrayListOf()
    private fun init() {
        doAsync {
            val document = Jsoup.connect(HOST).get()
            val swiperList = document.select("div.swiper-wrapper").select("a")
            println(swiperList)

            val imglist: ArrayList<String> = arrayListOf()
            val titleList: ArrayList<String> = arrayListOf()
            for (e in swiperList) {
                var title: String
                var img: String
                var path: String

                title = e.select("span").text()
                img = e.select("img").attr("src").replace("//static", "http://static")
                path = HOST + e.attr("href")

                val p = BannerEntity(title, img, path)
                list.add(p)
                titleList.add(title)
                imglist.add(img)
            }

            uiThread {
                println(list)
                banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                        .setImages(imglist)
                        .setBannerTitles(titleList)
                        .setImageLoader(GlideImageLoader())
                        .start()
                        .setOnBannerListener(this@HomeFragment)
            }
        }
    }

    override fun OnBannerClick(position: Int) {
        Toast.makeText(context, list[position].title + list[position].path, LENGTH_LONG).show()
        val intent = ArticleDetailActivity.newIntent(context!!, list[position].path)
        startActivity(intent)
    }

}