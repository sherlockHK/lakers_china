package com.kaihu.lakers_china

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.KeyEvent
import android.widget.Toast
import com.kaihu.lakers_china.ui.ColumnFragment
import com.kaihu.lakers_china.ui.HomeFragment
import com.kaihu.lakers_china.ui.VideoListFragment
import com.kaihu.lakers_china.ui.base.BaseActivity
import com.kaihu.lakers_china.ui.base.BaseFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private val fragments: List<BaseFragment> = listOf(HomeFragment(), VideoListFragment(), ColumnFragment())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        StatusBarUtil.setTransparentForImageView(this, banner)
        val transaction = supportFragmentManager.beginTransaction()
        for (fg in fragments){
            transaction?.add(R.id.main_container, fg)
        }
        transaction.commit()
        onTabItemSelected(0)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                onClickBottomTap(0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_video -> {
                onClickBottomTap(1)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_column -> {
                onClickBottomTap(2)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun onClickBottomTap(index: Int) {
        if (curIndex == index) {
            fragments[index].onScrollToTop()
        } else {
            onTabItemSelected(index)
        }
    }

    private var curIndex = 0
    private fun onTabItemSelected(position: Int){
        val transaction = supportFragmentManager.beginTransaction()
        for (fg in fragments){
            transaction?.hide(fg)
        }
        transaction.show(fragments[position])
        transaction.commit()
        curIndex = position
    }

    override fun onBackPressed() {
        if (curIndex == 1 && (fragments[1] as VideoListFragment).onBackPressed()){
            return
        }
        super.onBackPressed()
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle?) {}

    private var mExitTime: Long = 0

    override fun onKeyDown(keyCode: Int, event:KeyEvent):Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show()
                mExitTime = System.currentTimeMillis()
            } else {
                System.exit(0)
            }
            return true
        }
        return super.onKeyDown(keyCode, event);
    }
}
