package com.kaihu.lakers_china.adapter

import android.animation.Animator
import android.support.v4.app.ActivityOptionsCompat
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.kaihu.lakers_china.MainActivity
import com.kaihu.lakers_china.R
import com.kaihu.lakers_china.entity.NewsEntity
import com.kaihu.lakers_china.ui.ArticleDetailActivity
import com.kaihu.lakers_china.utils.Utils

//class NewsAdapter(val items: List<NewsEntity>) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun getItemCount(): Int = items.size
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(items[position])
//    }
//
//    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
//        fun bind(item: NewsEntity) {
//            Glide.with(view).load(item.img).into(view.news_img)
//            view.news_title.text = item.title
//            view.video_date.text = item.date
//            view.tv_type.text = item.type
//
//            view.setOnClickListener {
//                val intent = ArticleDetailActivity.newIntent(view.context!!, item.articlePath, item.img)
//                view.context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(view.context as MainActivity,
//                        view.news_img, "article_img")
//                        .toBundle())
//            }
//        }
//    }
//}

class NewsAdapter : BaseQuickAdapter<NewsEntity, BaseViewHolder>(R.layout.item_news) {
    override fun convert(helper: BaseViewHolder?, item: NewsEntity?) {
        val newsImG = helper?.getView<ImageView>(R.id.news_img)
        Glide.with(mContext).load(Utils.getDoorChainUrl(item?.img)).into(newsImG!!)
        helper?.setText(R.id.news_title, item?.title)
                ?.setText(R.id.video_date, item?.date)
                ?.setText(R.id.tv_type, item?.type)

        helper.setOnClickListener(R.id.cl_news) {
            val intent = ArticleDetailActivity.newIntent(mContext, item!!.articlePath, item.img)
            mContext.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(mContext as MainActivity,
                    newsImG, "article_img")
                    .toBundle())
        }
    }

    override fun startAnim(anim: Animator?, index: Int) {
        anim?.startDelay = index/2 * 100L
        super.startAnim(anim, index)
        anim?.interpolator = BounceInterpolator()
    }
}
