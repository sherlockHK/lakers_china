package com.kaihu.lakers_china.ui

import android.os.Bundle
import android.os.SystemClock
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.kaihu.lakers_china.R
import com.kaihu.lakers_china.adapter.HupuForumAdapter
import com.kaihu.lakers_china.adapter.PostMenuAdapter
import com.kaihu.lakers_china.entity.HupuForumItemEntity
import com.kaihu.lakers_china.entity.HupuMenuEntity
import com.kaihu.lakers_china.ui.base.BaseFragment
import kotlinx.android.synthetic.main.bottom_sheet_menu.view.*
import kotlinx.android.synthetic.main.fragment_hupu.*
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
    private var menuAdapter: PostMenuAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hupu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMenu()
        initRecyclerView()
        initRefreshLayout()

        loadMoreNews()
    }

    private fun initMenu() {
        val inflate = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_menu, bsl_menu, false)
        inflate.post_menu?.layoutManager = GridLayoutManager(context, 2)
        menuAdapter = PostMenuAdapter()
        menuAdapter?.openLoadAnimation(BaseQuickAdapter.SCALEIN)
        menuAdapter?.setDuration(500)
        menuAdapter?.isFirstOnly(true)
        menuAdapter?.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            val menu = adapter.getItem(position) as HupuMenuEntity
            team = menu.path
            tv_hupu_title.text = menu.name

            index =1
            list.clear()
            loadMoreNews()
            bsl_menu.dismissSheet()
        }

        inflate.post_menu?.adapter = menuAdapter

        toolbar.inflateMenu(R.menu.hupu_menu)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.other_topic -> {
                    bsl_menu.showWithSheetView(inflate)

                    menuAdapter?.setNewData(generateMenuData())
                }
            }
            true
        }

        bsl_menu.addOnSheetDismissedListener {
            menuAdapter?.setNewData(arrayListOf())
        }
    }

    fun generateMenuData(): ArrayList<HupuMenuEntity> {
        val list = arrayListOf<HupuMenuEntity>()
        list.add(HupuMenuEntity("湖人论坛", "lakers", R.drawable.lakers))
        list.add(HupuMenuEntity("火箭论坛", "rockets", R.drawable.rockets_logo))
        list.add(HupuMenuEntity("湿乎乎的话题", "vote", R.drawable.default_pic))
        list.add(HupuMenuEntity("步行街主干道", "bxj", R.drawable.default_pic))
        list.add(HupuMenuEntity("图图图", "4846", R.drawable.default_pic))
        list.add(HupuMenuEntity("大家来爆照", "selfie", R.drawable.default_pic))
        list.add(HupuMenuEntity("数码区", "digital", R.drawable.default_pic))
        return list
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
        val elements = doc.select("div.common-list").first().select("li")

        val listFromDom: ArrayList<HupuForumItemEntity> = arrayListOf()
        for (e in elements) {

            val title = e.select("div.news-txt").first().select("h3").text()
            val articlePath = e.select("a").attr("href").replace("//", "http://")
            val author = e.getElementsByClass("news-source").first().text()
            val date = e.getElementsByClass("news-time").text()

            listFromDom.add(HupuForumItemEntity(articlePath, title, author, date))
        }
        listFromDom.removeAt(0)
        return listFromDom
    }


}

