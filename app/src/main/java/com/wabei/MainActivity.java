package com.wabei;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;

import com.wabei.ui.ChangeColorTextView;
import com.wabei.ui.ChatFragment;
import com.wabei.ui.TabFragment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends FragmentActivity implements OnClickListener, OnPageChangeListener {

    private ViewPager mViewPage = null;

    private List<Fragment> mTabList = new ArrayList<Fragment>();

    private String[] mTitles = new String[] { "chat", "contacts", "found", "me" };

    private List<ChangeColorTextView> mTabIndicators = new ArrayList<>();

    private FragmentPagerAdapter mAdapter = null;

    private String TAG = "com.wabei";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getActionBar()).setDisplayShowHomeEnabled(false);
        setOverFlowButtonAlways();
        initView();
        initData();
    }

    private void initView() {
        mViewPage = findViewById(R.id.content_viewpage);
        ChangeColorTextView tabChatCTV = findViewById(R.id.tab_chat);
        ChangeColorTextView tabContacttCTV = findViewById(R.id.tab_contact);
        ChangeColorTextView tabFoundCTV = findViewById(R.id.tab_found);
        ChangeColorTextView tabMeCTV = findViewById(R.id.tab_me);
        mTabIndicators.add(tabChatCTV);
        mTabIndicators.add(tabContacttCTV);
        mTabIndicators.add(tabFoundCTV);
        mTabIndicators.add(tabMeCTV);
        tabChatCTV.setOnClickListener(this);
        tabContacttCTV.setOnClickListener(this);
        tabFoundCTV.setOnClickListener(this);
        tabMeCTV.setOnClickListener(this);
        tabChatCTV.setIconAlpha(1.0f);
        mViewPage.setOnPageChangeListener(this);

    }

    private void initData() {
        for (String title : mTitles) {
            Fragment tabFragment;
            if (title.equals("chat")) {
                tabFragment = new ChatFragment();
            } else {
                tabFragment = new TabFragment();
                Bundle bundle = new Bundle();
                bundle.putString(TabFragment.TITLE, title);
                tabFragment.setArguments(bundle);
            }
            mTabList.add(tabFragment);
        }
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mTabList.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabList.get(position);
            }
        };
        mViewPage.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    // 同样先获取到需要调用的method
                    @SuppressLint("PrivateApi")
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    // 设为可访问
                    m.setAccessible(true);
                    // 调用方法
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    private void setOverFlowButtonAlways() {
        ViewConfiguration config = ViewConfiguration.get(this);
        try {
            Field menuKey = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKey.setAccessible(true);
            menuKey.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        resetOtherTab();
        switch (v.getId()) {
            case R.id.tab_chat:
                mTabIndicators.get(0).setIconAlpha(1.0f);
                mViewPage.setCurrentItem(0, false);
                break;
            case R.id.tab_contact:
                mTabIndicators.get(1).setIconAlpha(1.0f);
                mViewPage.setCurrentItem(1, false);
                break;
            case R.id.tab_found:
                mTabIndicators.get(2).setIconAlpha(1.0f);
                mViewPage.setCurrentItem(2, false);
                break;
            case R.id.tab_me:
                mTabIndicators.get(3).setIconAlpha(1.0f);
                mViewPage.setCurrentItem(3, false);
                break;
            default:
                break;
        }
    }

    /**
     * 充值其他tab的颜色
     */
    private void resetOtherTab() {
        for (int i = 0; i < mTabIndicators.size(); i++) {
            mTabIndicators.get(i).setIconAlpha(0.0f);
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        Log.i(TAG, "onPageScrollStateChanged " + arg0);
    }

    @Override
    public void onPageScrolled(int position, float positionOffSet, int positionOffSetPixels) {
        Log.i(TAG, "onPageScrolled " + position + " " + positionOffSet + " " + positionOffSetPixels);
        if (positionOffSet > 0) {
            mTabIndicators.get(position).setIconAlpha(1 - positionOffSet);
            mTabIndicators.get(position + 1).setIconAlpha(positionOffSet);
        }
    }

    @Override
    public void onPageSelected(int arg0) {
        Log.i(TAG, "onPageSelected " + arg0);
    }

}
