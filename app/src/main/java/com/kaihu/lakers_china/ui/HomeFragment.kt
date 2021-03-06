package com.kaihu.lakers_china.ui

import android.os.Bundle
import android.os.SystemClock
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.kaihu.lakers_china.R
import com.kaihu.lakers_china.adapter.NewsAdapter
import com.kaihu.lakers_china.entity.BannerEntity
import com.kaihu.lakers_china.entity.NewsEntity
import com.kaihu.lakers_china.ui.base.BaseFragment
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
const val HOST_LAKERS_CHINA: String = "https://www.lakerschina.com"

class HomeFragment : BaseFragment() {
    private var index = 1
    private var isLoading = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private val list: ArrayList<NewsEntity> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initBanner()

        initRecyclerView()
        initRefreshLayout()

        loadMoreNews()
    }

    override fun onScrollToTop() {
        if (rv_news == null) return
        rv_news.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0f, 0f, 0))
        val manager = rv_news.layoutManager as LinearLayoutManager
        if (manager.findFirstVisibleItemPosition() > 20) {
            manager.scrollToPositionWithOffset(0, 0)
        } else {
            rv_news.post { rv_news.smoothScrollToPosition(0) }
        }
    }

    private fun initRefreshLayout() {
        srl_home.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary)
        srl_home.setOnRefreshListener { refreshNews() }
    }

    private val mAdapter = NewsAdapter()
    private fun initRecyclerView() {
        rv_news.layoutManager = LinearLayoutManager(context)
        mAdapter.setNewData(list)
        rv_news.adapter = mAdapter

        rv_news.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                //拿到最后一条的position
                val layoutManager = rv_news.layoutManager as LinearLayoutManager
                val endCompletelyPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                if (endCompletelyPosition >= rv_news.adapter.itemCount - 8) {
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
            val doc = Jsoup.connect(HOST_LAKERS_CHINA).get()
            val swiperList = doc.select("div.swiper-wrapper").select("a")
            println(swiperList)

            val list: ArrayList<BannerEntity> = arrayListOf()
            val imglist: ArrayList<String> = arrayListOf()
            val titleList: ArrayList<String> = arrayListOf()
            for (e in swiperList) {
                val title: String = e.select("span").text()
                val img: String = e.select("img").attr("src").replace("//static", "http://static")
                val path: String = HOST_LAKERS_CHINA + e.attr("href")

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
                        .setOnBannerListener { position ->
                            val intent = ArticleDetailActivity.newIntent(context!!, list[position].path, list[position].img)
                            startActivity(intent)
                        }
            }
        }
    }

    private fun refreshNews() {
        doAsync {
            val listFromDom: ArrayList<NewsEntity> = fetchNewsDomList(1)

            uiThread {
                mAdapter.setNewData(listFromDom)
                srl_home.isRefreshing = false
                index = 2
            }
        }
    }

    private fun loadMoreNews() {
        doAsync {
            isLoading = true
            val listFromDom: ArrayList<NewsEntity> = fetchNewsDomList(index)

            uiThread {
                mAdapter.addData(listFromDom)
                isLoading = false
                index++
            }
        }
    }

    private fun fetchNewsDomList(index: Int): ArrayList<NewsEntity> {
        val newsDoc = Jsoup.connect("$HOST_LAKERS_CHINA/list/article/6-$index").get()
        val columnDoc = Jsoup.connect("$HOST_LAKERS_CHINA/special/list-20-0-$index").get()
        val newsElements = newsDoc.select("div.textlist")[0].getElementsByClass("item")
        var elements = columnDoc.select("div.textlist")[0].getElementsByClass("item")

        newsElements.addAll(elements)
        val listFromDom: ArrayList<NewsEntity> = arrayListOf()
        for (e in newsElements) {
            val img = e.getElementsByClass("image").select("img").attr("src").replace("//static", "http://static")
            val info = e.getElementsByClass("info")
            val xx = info.select("dt a")
            val articlePath = HOST_LAKERS_CHINA + xx.attr("href")
            val title = xx.text()

            val type  = info[0].getElementsByClass("ulink").text()
            val date = info[0].getElementsByClass("date").text()

            listFromDom.add(NewsEntity(articlePath, title, img, date, type))
        }
        listFromDom.sortByDescending { it.date }
        return listFromDom
    }

}