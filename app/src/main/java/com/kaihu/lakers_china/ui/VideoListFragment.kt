package com.kaihu.lakers_china.ui

import android.graphics.Point
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kaihu.lakers_china.R
import com.kaihu.lakers_china.adapter.TAG
import com.kaihu.lakers_china.adapter.VideoListAdapter
import com.kaihu.lakers_china.entity.VideoEntity
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.CommonUtil
import com.shuyu.gsyvideoplayer.utils.GSYVideoHelper
import kotlinx.android.synthetic.main.fragment_video.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup


/**
 * Created by kai on 2018/7/25
 * Email：kaihu1989@gmail.com
 * Feature:
 */
class VideoListFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_video_list, container, false)
    }

    private var index = 1
    private var isLoading = false
    private val list: ArrayList<VideoEntity> = arrayListOf()
    var smallVideoHelper: GSYVideoHelper? = null
    var listVideoAdapter: VideoListAdapter? = null
    private var gsySmallVideoHelperBuilder: GSYVideoHelper.GSYVideoHelperBuilder? = null
    var lastVisibleItem: Int = 0
    var firstVisibleItem: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initVideoPlayer()
        initRecyclerView()
        initRefreshLayout()

        loadMoreVideos()
    }

    private fun initVideoPlayer() {
        //创建小窗口帮助类
        smallVideoHelper = GSYVideoHelper(activity)
        //配置
        gsySmallVideoHelperBuilder = GSYVideoHelper.GSYVideoHelperBuilder()
        gsySmallVideoHelperBuilder!!.setHideStatusBar(true)
                .setEnlargeImageRes(R.drawable.full_screen)
                .setShrinkImageRes(R.drawable.de_full_screen)
                .setNeedLockFull(true)
                .setAutoFullWithSize(true)
                .setCacheWithPlay(true)
                .setShowFullAnimation(true)
                .setRotateViewAuto(true)
                .setLockLand(true)
                .setVideoAllCallBack(object : GSYSampleCallBack() {
                    override fun onQuitSmallWidget(url: String?, vararg objects: Any) {
                        super.onQuitSmallWidget(url, *objects)
                        //大于0说明有播放,//对应的播放列表TAG
                        if (smallVideoHelper!!.getPlayPosition() >= 0 && smallVideoHelper!!.getPlayTAG().equals(TAG)) {
                            //当前播放的位置
                            val position = smallVideoHelper!!.getPlayPosition()
                            //不可视的是时候
                            if (position < firstVisibleItem || position > lastVisibleItem) {
                                //释放掉视频
                                smallVideoHelper!!.releaseVideoPlayer()
                                if(listVideoAdapter != null){
                                    listVideoAdapter!!.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                })
    }

    private fun initRefreshLayout() {
        srl_video.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary)
        srl_video.setOnRefreshListener { refreshVideos() }
    }
    private fun initRecyclerView() {
        rv_videos.layoutManager = LinearLayoutManager(context)
        rv_videos.adapter = VideoListAdapter(list, smallVideoHelper!!, gsySmallVideoHelperBuilder!!)

        smallVideoHelper!!.setGsyVideoOptionBuilder(gsySmallVideoHelperBuilder)

        rv_videos.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                //列表滑动刷新
                val layoutManager = rv_videos.layoutManager as LinearLayoutManager
                val endCompletelyPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                if (endCompletelyPosition >= rv_videos.adapter.itemCount - 8) {
                    //执行加载更多的方法，无论是用接口还是别的方式都行
                    if (!isLoading) {
                        loadMoreVideos()
                    }
                }

                //播放器处理
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                //大于0说明有播放,//对应的播放列表TAG
                if (smallVideoHelper!!.getPlayPosition() >= 0 && smallVideoHelper!!.getPlayTAG().equals(TAG)) {
                    //当前播放的位置
                    val position = smallVideoHelper!!.getPlayPosition()
                    //不可视的是时候
                    if (position < firstVisibleItem || position > lastVisibleItem) {
                        //如果是小窗口就不需要处理
                        if (!smallVideoHelper!!.isSmall()) {
                            //小窗口
                            val size = CommonUtil.dip2px(activity, 150f)
                            smallVideoHelper!!.showSmallVideo(Point(size, size), false, true)
                        }
                    } else {
                        if (smallVideoHelper!!.isSmall) {
                            if (smallVideoHelper != null){
                                smallVideoHelper!!.smallVideoToNormal()
                            }
                        }
                    }
                }
            }
        })
    }

    fun onBackPressed():Boolean{
        if (smallVideoHelper!!.backFromFull()) {
            return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        smallVideoHelper!!.releaseVideoPlayer()
        GSYVideoManager.releaseAllVideos()
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

            listFromDom.add(VideoEntity(articlePath, title, img, videoType, date))
        }
        return listFromDom
    }
}