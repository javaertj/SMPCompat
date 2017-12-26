package com.drivingassisstantHouse.library.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drivingassisstantHouse.library.R;
import com.drivingassisstantHouse.library.tools.ToolUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 包名：com.drivingassisstantHouse.library.widget
 * 描述：主页导航tabbar
 * 创建者：yankebin
 * 日期：2016/2/26
 */
public class MainNavigateTabBar extends LinearLayout implements View.OnClickListener {
    private static final String KEY_CURRENT_TAG = "com.drivingassisstantHouse.library.widget.MainNavigateTabBar";

    private List<ViewHolder> mViewHolderList;
    private OnTabSelectedListener mTabSelectListener;
    private FragmentActivity mFragmentActivity;
    private String mCurrentTag;
    private String mRestoreTag;
    /**
     * 主内容显示区域View的id
     */
    private int mMainContentLayoutId;
    /**
     * 是否显示分割线
     */
    private boolean showTabDivider;
    /**
     * Tab文字的颜色
     */
    private float mTabTextSize;
    /**
     * 默认选中的tab index
     */
    private int mDefaultSelectedTab = 0;
    /**
     * 当前选中的tab下标
     */
    private int mCurrentSelectedTab;
    /**
     * 如果是fragment嵌套fragment，需要传入此参数
     */
    private FragmentManager fragmentManager;

    public MainNavigateTabBar(Context context) {
        this(context, null);
    }

    public MainNavigateTabBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainNavigateTabBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MainNavigateTabBar, 0, 0);
        mTabTextSize = typedArray.getDimensionPixelSize(R.styleable.MainNavigateTabBar_navigateTabTextSize, 0);
        mMainContentLayoutId = typedArray.getResourceId(R.styleable.MainNavigateTabBar_containerId, 0);
        showTabDivider = typedArray.getBoolean(R.styleable.MainNavigateTabBar_showTabDivider, true);
        typedArray.recycle();
        mViewHolderList = new ArrayList<>();
    }

    /**
     * 如果是fragment嵌套fragment，需要传入此参数
     *
     * @param fragmentManager
     */
    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    /**
     * 如果是fragment嵌套fragment，返回的是传入的fragmentManager，其他返回activity的fragmentManager
     *
     * @return
     */
    public FragmentManager getFragmentManager() {
        return null != fragmentManager ? fragmentManager :
                mFragmentActivity.getSupportFragmentManager();
    }

    /**
     * 装载tab
     *
     * @param fragmentClass
     * @param tabParam
     */
    public void addTab(Class fragmentClass, TabParam tabParam) {
        int defaultLayout = R.layout.item_main_tab;
        if (TextUtils.isEmpty(tabParam.title)) {
            tabParam.title = getContext().getString(tabParam.titleStringRes);
        }

        View view = LayoutInflater.from(getContext()).inflate(defaultLayout, null);
        view.setFocusable(true);

        ViewHolder holder = new ViewHolder();

        holder.tabIndex = mViewHolderList.size();

        holder.fragmentClass = fragmentClass;
        holder.tag = tabParam.title;
        holder.pageParam = tabParam;

        holder.tabIcon = (ImageView) view.findViewById(R.id.tab_icon);
        holder.tabTitle = ((TextView) view.findViewById(R.id.tab_title));
        holder.tabFlag = (TextView) view.findViewById(R.id.tab_flag);
        holder.tabView = view.findViewById(R.id.tabView);
        holder.tabIconGroup = view.findViewById(R.id.layout_tab_icon);
        holder.tabDivider = view.findViewById(R.id.tabDivider);

        if (showTabDivider) {
            holder.tabDivider.setVisibility(VISIBLE);
        } else {
            holder.tabDivider.setVisibility(GONE);
        }
        if (TextUtils.isEmpty(tabParam.title)) {
            holder.tabTitle.setVisibility(View.INVISIBLE);
        } else {
            holder.tabTitle.setText(tabParam.title);
        }

        if (mTabTextSize > 0) {
            holder.tabTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTabTextSize);
        }

        updateTabTitleColor(holder.tabTitle, tabParam.titleNormalColorResId);
        updateTabBackground(holder.tabView, tabParam.tabBgNormalResId);
        updateTabIcon(holder, holder.tabIcon, tabParam.iconResId);

        //设置监听
        view.setTag(holder);
        view.setOnClickListener(this);
        mViewHolderList.add(holder);

        addView(view, new LayoutParams(0, -1, 1.0F));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mMainContentLayoutId == 0) {
            throw new RuntimeException("mFrameLayoutId Cannot be 0");
        }
        if (mViewHolderList.size() == 0) {
            throw new RuntimeException("mViewHolderList.size Cannot be 0, Please call addTab()");
        }
        if (!(getContext() instanceof FragmentActivity)) {
            throw new RuntimeException("parent activity must is extends FragmentActivity");
        }
        mFragmentActivity = (FragmentActivity) getContext();

        hideAllFragment();

        ViewHolder defaultHolder = null;
        if (!TextUtils.isEmpty(mRestoreTag)) {
            for (ViewHolder holder : mViewHolderList) {
                if (TextUtils.equals(mRestoreTag, holder.tag)) {
                    defaultHolder = holder;
                    mRestoreTag = null;
                    break;
                }
            }
        } else {
            defaultHolder = mViewHolderList.get(mDefaultSelectedTab);
        }

        showFragment(defaultHolder);
    }

    @Override
    public void onClick(View v) {
        Object object = v.getTag();
        if (object != null && object instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) v.getTag();
            String tempTag = mCurrentTag;
            showFragment(holder);
            if (mTabSelectListener != null) {
                if (TextUtils.equals(holder.tag, tempTag)) {
                    mTabSelectListener.onTabReSelected(holder);
                } else {
                    mTabSelectListener.onTabSelected(holder);
                }
            }
        }
    }

    /**
     * 显示 holder 对应的 fragment
     *
     * @param holder
     */
    private void showFragment(ViewHolder holder) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (isFragmentShown(transaction, holder.tag)) {
            return;
        }
        setCurrSelectedTabByTag(holder.tag);

        Fragment fragment = getFragmentManager().findFragmentByTag(holder.tag);
        if (fragment == null) {
            fragment = getFragmentInstance(holder.tag);
            if (null != holder.pageParam.parmas) {
                fragment.setArguments(holder.pageParam.parmas);
            }
            transaction.add(mMainContentLayoutId, fragment, holder.tag);
        } else {
            transaction.show(fragment);
        }
        transaction.commit();
        mCurrentSelectedTab = holder.tabIndex;
    }

    private boolean isFragmentShown(FragmentTransaction transaction, String newTag) {
        if (TextUtils.equals(newTag, mCurrentTag)) {
            return true;
        }

        if (TextUtils.isEmpty(mCurrentTag)) {
            return false;
        }

        Fragment fragment = getFragmentManager().findFragmentByTag(mCurrentTag);
        if (fragment != null && !fragment.isHidden()) {
            transaction.hide(fragment);
        }

        return false;
    }

    /**
     * 检测传入的资源id是否有效
     *
     * @param resValue
     * @return
     */
    private boolean invalidResValue(int resValue) {
        return resValue <= 0;
    }

    /**
     * 设置当前选中tab的图片和文字颜色
     */
    private void setCurrSelectedTabByTag(String tag) {
        if (TextUtils.equals(mCurrentTag, tag)) {
            return;
        }
        for (ViewHolder holder : mViewHolderList) {
            if (TextUtils.equals(mCurrentTag, holder.tag)) {
                updateTabIcon(holder, holder.tabIcon, holder.pageParam.iconResId);
                updateTabTitleColor(holder.tabTitle, holder.pageParam.titleNormalColorResId);
                updateTabBackground(holder.tabView, holder.pageParam.tabBgNormalResId);
            } else if (TextUtils.equals(tag, holder.tag)) {
                updateTabIcon(holder, holder.tabIcon, holder.pageParam.iconSelectedResId);
                updateTabTitleColor(holder.tabTitle, holder.pageParam.titleSelectedColorResId);
                updateTabBackground(holder.tabView, holder.pageParam.tabBgSelectedResId);
            }
        }
        mCurrentTag = tag;
    }

    /**
     * 更新imageview现实的图片
     *
     * @param holder
     * @param tabIcon
     * @param iconRes
     */
    private void updateTabIcon(ViewHolder holder, ImageView tabIcon, int iconRes) {
        if (!invalidResValue(iconRes)) {
            holder.tabIconGroup.setVisibility(VISIBLE);
            holder.tabDivider.setVisibility(VISIBLE);
            tabIcon.setImageResource(iconRes);
        } else {
            holder.tabIconGroup.setVisibility(GONE);
            holder.tabDivider.setVisibility(GONE);
        }
    }

    /**
     * 更新textview文本颜色
     *
     * @param tabTitle
     * @param colorRes
     */
    private void updateTabTitleColor(TextView tabTitle, int colorRes) {
        if (!invalidResValue(colorRes)) {
            tabTitle.setTextColor(getResources().getColor(colorRes));
        }
    }

    /**
     * 更新view背景
     *
     * @param tabView
     * @param resId
     */
    private void updateTabBackground(View tabView, int resId) {
        if (!invalidResValue(resId)) {
            tabView.setBackgroundResource(resId);
        }
    }

    /**
     * 创建fragment
     *
     * @param tag
     * @return
     */
    private Fragment getFragmentInstance(String tag) {
        Fragment fragment = null;
        for (ViewHolder holder : mViewHolderList) {
            if (TextUtils.equals(tag, holder.tag)) {
                try {
                    fragment = (Fragment) Class.forName(holder.fragmentClass.getName()).newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return fragment;
    }

    /**
     * 隐藏所有fragment
     */
    private void hideAllFragment() {
        if (mViewHolderList == null || mViewHolderList.size() == 0) {
            return;
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        for (ViewHolder holder : mViewHolderList) {
            Fragment fragment = getFragmentManager().findFragmentByTag(holder.tag);
            if (fragment != null && !fragment.isHidden()) {
                transaction.hide(fragment);
            }
        }
        transaction.commit();
    }

    /**
     * 设置fragment的容器布局id
     *
     * @param frameLayoutId
     */
    public void setFrameLayoutId(int frameLayoutId) {
        mMainContentLayoutId = frameLayoutId;
    }

    /**
     * 获取存储的tag
     *
     * @param savedInstanceState
     */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mRestoreTag = savedInstanceState.getString(KEY_CURRENT_TAG);
        }
    }

    /**
     * 存储当前的tag
     *
     * @param outState
     */
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_CURRENT_TAG, mCurrentTag);
    }

    /**
     * 视图缓存类
     */
    public static class ViewHolder {
        public String tag;
        public TabParam pageParam;
        public View tabView;
        public ImageView tabIcon;
        public TextView tabTitle;
        public TextView tabFlag;
        public Class fragmentClass;
        public int tabIndex;
        public View tabIconGroup;
        public View tabDivider;
    }

    /**
     * tab属性类
     */
    public static class TabParam {
        public int titleNormalColorResId;
        public int titleSelectedColorResId;
        public int iconResId;
        public int flagCount;
        public int iconSelectedResId;
        public int titleStringRes;
        public int tabBgNormalResId;
        public int tabBgSelectedResId;
        public String title;
        public Bundle parmas;

        private TabParam(Builder builder) {
            this.titleNormalColorResId = builder.titleNormalColorResId;
            this.titleSelectedColorResId = builder.titleSelectedColorResId;
            this.iconResId = builder.iconResId;
            this.flagCount = builder.flagCount;
            this.iconSelectedResId = builder.iconSelectedResId;
            this.titleStringRes = builder.titleStringRes;
            this.tabBgNormalResId = builder.tabBgNormalResId;
            this.tabBgSelectedResId = builder.tabBgSelectedResId;
            this.title = builder.title;
            this.parmas = builder.parmas;
        }

        /**
         * tab属性构建器
         */
        public static class Builder {
            private int titleNormalColorResId;
            private int titleSelectedColorResId;
            private int iconResId;
            private int flagCount;
            private int iconSelectedResId;
            private int titleStringRes;
            private int tabBgNormalResId;
            private int tabBgSelectedResId;
            private String title;
            private Bundle parmas;

            public Builder titleNormalColorResId(int titleNormalColorResId) {
                this.titleNormalColorResId = titleNormalColorResId;
                return this;
            }


            public Builder titleSelectedColorResId(int titleSelectedColorResId) {
                this.titleSelectedColorResId = titleSelectedColorResId;
                return this;
            }

            public Builder iconResId(int iconResId) {
                this.iconResId = iconResId;
                return this;
            }

            public Builder flagResId(int flagResId) {
                this.flagCount = flagResId;
                return this;
            }

            public Builder iconSelectedResId(int iconSelectedResId) {
                this.iconSelectedResId = iconSelectedResId;
                return this;
            }

            public Builder titleStringRes(int titleStringRes) {
                this.titleStringRes = titleStringRes;
                return this;
            }

            public Builder tabBgNormalResId(int tabBgNormalResId) {
                this.tabBgNormalResId = tabBgNormalResId;
                return this;
            }

            public Builder tabBgSelectedResId(int tabBgSelectedResId) {
                this.tabBgSelectedResId = tabBgSelectedResId;
                return this;
            }

            public Builder title(String title) {
                this.title = title;
                return this;
            }

            public Builder parmas(Bundle parmas) {
                this.parmas = parmas;
                return this;
            }

            public TabParam build() {
                return new TabParam(this);
            }
        }
    }

    /**
     * tab被选中的回调接口
     */
    public interface OnTabSelectedListener {
        void onTabSelected(ViewHolder holder);

        void onTabReSelected(ViewHolder holder);
    }

    /**
     * 设置tab被选中时的回调接口
     *
     * @param tabSelectListener
     */
    public void setTabSelectListener(OnTabSelectedListener tabSelectListener) {
        mTabSelectListener = tabSelectListener;
    }

    /**
     * 设置默认显示的页面
     *
     * @param index
     */
    public void setDefaultSelectedTab(int index) {
        if (index >= 0 && index < mViewHolderList.size()) {
            mDefaultSelectedTab = index;
        }
    }

    /**
     * 设置一个页面被选中
     *
     * @param index
     */
    public void setCurrentSelectedTab(int index) {
        if (index >= 0 && index < mViewHolderList.size()) {
            ViewHolder holder = mViewHolderList.get(index);
            showFragment(holder);
        }
    }

    /**
     * 获取当前被选中的页面下标
     *
     * @return
     */
    public int getCurrentSelectedTab() {
        return mCurrentSelectedTab;
    }

    /**
     * 获取当前显示的fragment
     *
     * @param title
     * @return
     */
    public Fragment getFragment(String title) {
        return getFragmentManager().findFragmentByTag(title);
    }

    /**
     * 控制提醒icon显隐藏
     *
     * @param index
     * @param count
     * @param maxCount
     */
    public void handleTabFlag(int index, int count, int maxCount) {
        for (int i = 0; i < mViewHolderList.size(); i++) {
            if (i == index) {
                ViewHolder holder = mViewHolderList.get(i);
                ViewGroup.LayoutParams params = holder.tabFlag.getLayoutParams();
                holder.pageParam.flagCount = count;
                if (count < 1) {
                    holder.tabFlag.setText("");
                } else if (count <= maxCount) {
                    params.width = ToolUnit.dipTopx(14);
                    params.height = params.width;
                    holder.tabFlag.setText(String.format(Locale.CHINESE, "%d", count));
                } else {
                    holder.tabFlag.setText("");
                    params.width = ToolUnit.dipTopx(8);
                    params.height = params.width;
                }
                holder.tabFlag.setLayoutParams(params);
                holder.tabFlag.setVisibility(count > 0 ? VISIBLE : INVISIBLE);
                return;
            }
        }
    }
}

