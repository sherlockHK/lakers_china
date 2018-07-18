package com.kaihu.lakers_china

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.kaihu.lakers_china.ui.ColumnFragment
import com.kaihu.lakers_china.ui.HomeFragment
import com.kaihu.lakers_china.ui.VideoFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val fragments: List<Fragment> = listOf(HomeFragment(), VideoFragment(), ColumnFragment())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                onTabItemSelected(0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_video -> {
                onTabItemSelected(1)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_column -> {
                onTabItemSelected(2)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun onTabItemSelected(position: Int){
        val transaction = supportFragmentManager.beginTransaction()
        for (fg in fragments){
            transaction?.hide(fg)
        }
        transaction.show(fragments[position])
        transaction.commit()
    }
}