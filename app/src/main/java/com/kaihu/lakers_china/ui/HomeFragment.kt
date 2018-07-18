package com.kaihu.lakers_china.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kaihu.lakers_china.R
import com.kaihu.lakers_china.adapter.NewsAdapter
import com.kaihu.lakers_china.entity.BannerEntity
import com.kaihu.lakers_china.entity.NewsEntity
import com.kaihu.lakers_china.utils.GlideImageLoader
import com.youth.banner.BannerConfig
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup


/**
 * Created by kai on 2018/7/16
 * Email：kaihu1989@gmail.com
 * Feature:home
 */
const val HOST: String = "https://www.lakerschina.com"

class HomeFragment : Fragment() {
    private var index = 1
    private var isLoading = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private val list: ArrayList<NewsEntity> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initBanner()
        loadMoreNews()
        initRecyclerView()
        initRefreshLayout()
    }

    private fun initRefreshLayout() {
        swiperefreshlayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary)
        swiperefreshlayout.setOnRefreshListener { refreshNews() }
    }

    private fun initRecyclerView() {
        rv_news.layoutManager = LinearLayoutManager(context)
        rv_news.adapter = NewsAdapter(list)
        rv_news.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                //拿到最后一条的position
                val layoutManager = rv_news.layoutManager as LinearLayoutManager
                val endCompletelyPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                if (endCompletelyPosition <= rv_news.adapter.itemCount - 8) {
                    //执行加载更多的方法，无论是用接口还是别的方式都行
                    if (!isLoading) {
                        loadMoreNews()
                    }
                }
            }
        })
    }

    private fun initBanner() {
        doAsync {
            val doc = Jsoup.connect(HOST).get()
            val swiperList = doc.select("div.swiper-wrapper").select("a")
            println(swiperList)

            val list: ArrayList<BannerEntity> = arrayListOf()
            val imglist: ArrayList<String> = arrayListOf()
            val titleList: ArrayList<String> = arrayListOf()
            for (e in swiperList) {
                val title: String = e.select("span").text()
                val img: String = e.select("img").attr("src").replace("//static", "http://static")
                val path: String = HOST + e.attr("href")

                val p = BannerEntity(title, img, path)
                list.add(p)
                titleList.add(title)
                imglist.add(img)
            }

            uiThread {
                banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                        .setImages(imglist)
                        .setBannerTitles(titleList)
                        .setDelayTime(5000)
                        .setImageLoader(GlideImageLoader())
                        .start()
                        .setOnBannerListener({ position ->
                            val intent = ArticleDetailActivity.newIntent(context!!, list[position].path)
                            startActivity(intent)
                        })
            }
        }
    }

    private fun refreshNews() {
        doAsync {
            val listFromDom: ArrayList<NewsEntity> = fetchNewsDomList(1)

            uiThread {
                if (rv_news == null) return@uiThread
                list.clear()
                list.addAll(listFromDom)
                rv_news.adapter.notifyDataSetChanged()
                swiperefreshlayout.isRefreshing = false
                index = 1
            }
        }
    }

    private fun loadMoreNews() {
        doAsync {
            isLoading = true
            val listFromDom: ArrayList<NewsEntity> = fetchNewsDomList(index)

            uiThread {
                if (rv_news == null) return@uiThread
                list.addAll(listFromDom)
                rv_news.adapter.notifyDataSetChanged()
                isLoading = false
                index++
            }
        }
    }

    private fun fetchNewsDomList(index: Int): ArrayList<NewsEntity> {
        val doc = Jsoup.connect("$HOST/list/article/18-$index/").get()
        val elements = doc.select("div.textlist")[0].getElementsByClass("item")

        val listFromDom: ArrayList<NewsEntity> = arrayListOf()
        for (e in elements) {
            val img = e.getElementsByClass("pic").select("a img").attr("src").replace("//static", "http://static")
            val info = e.getElementsByClass("info")
            val xx = info.select("h5").select("a")
            val articlePath = HOST + xx.attr("href")
            val title = xx.text()

            val date = info[0].getElementsByClass("pull-right").text()

            listFromDom.add(NewsEntity(articlePath, title, img, date))
        }
        return listFromDom
    }

}