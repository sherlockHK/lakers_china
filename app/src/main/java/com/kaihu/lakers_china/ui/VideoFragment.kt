package com.kaihu.lakers_china.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kaihu.lakers_china.R
import com.kaihu.lakers_china.adapter.VideosAdapter
import com.kaihu.lakers_china.entity.VideoEntity
import kotlinx.android.synthetic.main.fragment_video.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup

/**
 * Created by kai on 2018/7/16
 * Email：kaihu1989@gmail.com
 * Feature:
 */
class VideoFragment : Fragment() {
    private var index = 1
    private var isLoading = false
    private val list: ArrayList<VideoEntity> = arrayListOf()
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_video, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRecyclerView()
        initRefreshLayout()

        loadMoreVideos()
    }

    private fun initRefreshLayout() {
        srl_video.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary)
        srl_video.setOnRefreshListener { refreshVideos() }
    }

    private fun initRecyclerView() {
        rv_videos.layoutManager = LinearLayoutManager(context)
        rv_videos.adapter = VideosAdapter(list)
        rv_videos.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                //拿到最后一条的position
                val layoutManager = rv_videos.layoutManager as LinearLayoutManager
                val endCompletelyPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                if (endCompletelyPosition >= rv_videos.adapter.itemCount - 8) {
                    //执行加载更多的方法，无论是用接口还是别的方式都行
                    if (!isLoading) {
                        loadMoreVideos()
                    }
                }
            }
        })
    }

    private fun refreshVideos() {
        doAsync {
            val listFromDom: ArrayList<VideoEntity> = fetchVideosDomList(1)

            uiThread {
                list.clear()
                list.addAll(listFromDom)
                rv_videos?.adapter?.notifyDataSetChanged()
                srl_video.isRefreshing = false
                index = 2
            }
        }
    }

    private fun loadMoreVideos() {
        doAsync {
            isLoading = true
            val listFromDom: ArrayList<VideoEntity> = fetchVideosDomList(index)

            uiThread {
                list.addAll(listFromDom)
                rv_videos?.adapter?.notifyDataSetChanged()
                isLoading = false
                index++
            }
        }
    }

    private fun fetchVideosDomList(index: Int): ArrayList<VideoEntity> {
        val doc = Jsoup.connect("$HOST/list/video/0-$index/").get()
        val elements = doc.select("div.textlist")[0].getElementsByClass("item")

        val listFromDom: ArrayList<VideoEntity> = arrayListOf()
        for (e in elements) {
            val img = e.getElementsByClass("pic").select("a img").attr("src").replace("//static", "http://static")
            val info = e.getElementsByClass("info")
            val xx = info.select("h5").select("a")
            val articlePath = HOST + xx.attr("href")
            val title = xx.text()

            val videoType = info[0].getElementsByClass("pull-left").text()
            val date = info[0].getElementsByClass("pull-right").text()

            listFromDom.add(VideoEntity(articlePath, title, img, videoType,date))
        }
        return listFromDom
    }
}