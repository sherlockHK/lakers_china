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
import com.kaihu.lakers_china.adapter.HupuForumAdapter
import com.kaihu.lakers_china.entity.HupuForumItemEntity
import com.kaihu.lakers_china.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup

/**
 * Created by kai on 2018/7/16
 * Email：kaihu1989@gmail.com
 * Feature:hupu湖人专区
 */
const val HOST_HUPU_BBS = "https://bbs.hupu.com"
class HupuFragment : BaseFragment() {
    private var index = 1

    private var isLoading = false
    private val list: ArrayList<HupuForumItemEntity> = arrayListOf()
    private var team = "lakers"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hupu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    private fun initRecyclerView() {
        rv_news.layoutManager = LinearLayoutManager(context)
        rv_news.adapter = HupuForumAdapter(list)
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

    private fun refreshNews() {
        doAsync {
            val listFromDom: ArrayList<HupuForumItemEntity> = fetchNewsDomList(1)

            uiThread {
                list.clear()
                list.addAll(listFromDom)
                rv_news?.adapter?.notifyDataSetChanged()
                srl_home.isRefreshing = false
                index = 2
            }
        }
    }

    private fun loadMoreNews() {
        doAsync {
            isLoading = true
            val listFromDom: ArrayList<HupuForumItemEntity> = fetchNewsDomList(index)

            uiThread {
                list.addAll(listFromDom)
                rv_news?.adapter?.notifyDataSetChanged()
                isLoading = false
                index++
            }
        }
    }

    private fun fetchNewsDomList(index: Int): ArrayList<HupuForumItemEntity> {
        val doc = Jsoup.connect("$HOST_HUPU_BBS/$team-$index").get()
        val elements = doc.select("ul.for-list").first().select("li")

        val listFromDom: ArrayList<HupuForumItemEntity> = arrayListOf()
        for (e in elements) {

            val titleEle = e.select("a.truetit").first()
            val title = titleEle.text()
            val articlePath = HOST_HUPU_BBS + titleEle.attr("href")
            val author = e.getElementsByClass("aulink").first().text()
            val date = e.select("div.author").select("a")[1].text()

            listFromDom.add(HupuForumItemEntity(articlePath, title, author , date))
        }
        listFromDom.removeAt(0)
        return listFromDom
    }


}

