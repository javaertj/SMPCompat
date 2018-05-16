package com.drivingassisstantHouse.library.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drivingassisstantHouse.library.data.OnNetWorkEvent;
import com.drivingassisstantHouse.library.tools.SLog;
import com.growingio.android.sdk.collection.GrowingIO;
import com.mcxiaoke.bus.annotation.BusReceiver;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 基于V4Fragment基类(V4包)
 *
 * @author sunji
 * @version 1.0
 */
public abstract class BaseFragmentV4 extends Fragment implements IBaseFragment {

    /**
     * 当前Fragment渲染的视图View
     **/
    protected View mContextView = null;
    /**
     * 依附的Activity
     **/
    protected AppCompatActivity mContext = null;
    /**
     * 当前frgment的inflater
     **/
    protected LayoutInflater mInflater = null;

    protected BaseHandler baseHandler;

    /**
     * onDestroyView方法执行的标识
     */
    protected boolean isDestroyView;

    protected Unbinder unbinder;

    protected static class BaseHandler extends Handler {
        private WeakReference<IBaseFragment> baseFragment;

        public BaseHandler(IBaseFragment baseFragment) {
            this.baseFragment = new WeakReference<>(baseFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            baseFragment.get().handleMessage(msg);
            super.handleMessage(msg);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //缓存当前依附的activity
        mContext = (AppCompatActivity) activity;
        SLog.d("BaseFragmentV4-->onAttach()");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        GrowingIO.getInstance().trackFragment(getActivity(), this);
        baseHandler = new BaseHandler(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SLog.d("BaseFragmentV4-->onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SLog.d("BaseFragmentV4-->onCreateView()");
        isDestroyView = false;
        mInflater = inflater;
        // 渲染视图View
        if (null == mContextView) {
            View mView = bindView();
            if (null == mView) {
                mContextView = inflater.inflate(bindLayout(), container, false);
            } else {
                mContextView = mView;
            }
            //使用butterKnife注解-fragment需要解绑
            unbinder=ButterKnife.bind(this, mContextView);
            //初始化参数
            initParms(getArguments());
            // 控件初始化
            initView(mContextView);
            // 业务处理
            doBusiness(mContext);
        } else {
            //使用butterKnife注解-fragment需要解绑
            unbinder=ButterKnife.bind(this, mContextView);
        }
        return mContextView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        SLog.d("BaseFragmentV4-->onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        SLog.d("BaseFragmentV4-->onSaveInstanceState()");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        SLog.d("BaseFragmentV4-->onStart()");
        super.onStart();
    }

    @Override
    public void onResume() {
        SLog.d("BaseFragmentV4-->onResume()");
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName());
    }

    @Override
    public void onPause() {
        SLog.d("BaseFragmentV4-->onPause()");
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getName());
    }

    @Override
    public void onStop() {
        SLog.d("BaseFragmentV4-->onStop()");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        SLog.d("BaseFragmentV4-->onDestroy()");
        super.onDestroy();

    }

    @Override
    public void onDetach() {
        SLog.d("BaseFragmentV4-->onDetach()");
        mContext = null;
        mContextView = null;
        baseHandler = null;
        mInflater = null;
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        isDestroyView = true;
        super.onDestroyView();
        if (null!=unbinder){
            unbinder.unbind();
        }
        if (mContextView != null && mContextView.getParent() != null) {
            ((ViewGroup) mContextView.getParent()).removeView(mContextView);
        }
    }

    /**
     * 获取当前Fragment依附在的Activity
     *
     * @return
     */
    protected Activity getAttachActivity() {
        return getActivity();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @BusReceiver
    public void onMainNetWorkEvent(OnNetWorkEvent event) {
        onNetWorkChanged(event.connected);
    }

    @Override
    public void onNetWorkChanged(boolean connected) {

    }

    @Override
    public void handleMessage(Message msg) {

    }
}
