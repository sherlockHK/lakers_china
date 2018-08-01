package com.kaihu.lakers_china.ui.base;

import android.support.v4.app.Fragment;

/**
 * Created by kai on 2018/7/26
 * Email：kaihu1989@gmail.com
 * Feature:
 */
public abstract class BaseFragment extends Fragment implements OnBottomListener{
}
interface OnBottomListener{
    void onScrollToTop();
}