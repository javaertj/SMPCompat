package com.drivingassisstantHouse.library.widget.web;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by simpletour on 2016/9/9.
 * 设置最大高度webview
 * 设置最大高度一定要在加载内容之前!!!
 */


public class MaxHeightWebView extends WebView {
    private int mMaxHeight = -1;

    public MaxHeightWebView(Context context) {
        super(context);

    }

    public MaxHeightWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public MaxHeightWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public MaxHeightWebView(Context context, AttributeSet attrs, int defStyle,
                            boolean privateBrowsing) {
        super(context, attrs, defStyle, privateBrowsing);

    }

    public void setMaxHeight(int height) {
        mMaxHeight = height;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mMaxHeight > -1 && getMeasuredHeight() > mMaxHeight) {
            setMeasuredDimension(getMeasuredWidth(), mMaxHeight);
        }
    }
}