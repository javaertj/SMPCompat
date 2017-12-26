package com.drivingassisstantHouse.library.widget.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.ColorRes;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 描述:对话框holder，暂存view
 * 创建人:simpletour
 * 日期:2017/2/23
 * 时间:15:45
 * 版本号:1.0
 */
public class DialogViewHolder {
    private final SparseArray<View> mViews;
    private View mDialogView;

    private DialogViewHolder(Context context, int layoutId) {
        this.mViews = new SparseArray<View>();
        mDialogView = generalView(context, layoutId);
    }

    private View generalView(Context context, int layoutId) {
        LayoutInflater inflate = LayoutInflater.from(context);
        return inflate.inflate(layoutId, null);
    }

    public static DialogViewHolder get(Context context, int layoutId) {
        return new DialogViewHolder(context, layoutId);
    }

    public View getConvertView() {
        return mDialogView;
    }

    /**
     * 设置文本
     *
     * @param viewId
     * @param text
     * @return
     */
    public DialogViewHolder setText(int viewId, CharSequence text) {
        View view = getView(viewId);
        if (view instanceof TextView)
            ((TextView) view).setText(text);
        return this;
    }

    /**
     * 设置文本
     *
     * @param viewId
     * @param textRes
     * @return
     */
    public DialogViewHolder setText(int viewId, int textRes) {
        View view = getView(viewId);
        if (view instanceof TextView)
            ((TextView) view).setText(textRes);
        return this;
    }

    /**
     * 设置图片关联到
     *
     * @param viewId
     * @param bitmap
     * @return
     */
    public DialogViewHolder setBitmap(int viewId, Bitmap bitmap) {
        View view = getView(viewId);
        if (view instanceof ImageView)
            ((ImageView) view).setImageBitmap(bitmap);
        return this;
    }

    /**
     * 设置图片关联
     *
     * @param viewId
     * @param res
     * @return
     */
    public DialogViewHolder setImageRes(int viewId, int res) {
        View view = getView(viewId);
        if (view instanceof ImageView)
            ((ImageView) view).setImageResource(res);
        ;
        return this;
    }

    /**
     * 设置view可见
     *
     * @param viewId
     * @param viewId
     * @return
     */
    public DialogViewHolder setViewInViSible(int viewId) {
        View view = getView(viewId);
        view.setVisibility(View.INVISIBLE);
        return this;
    }

    /**
     * 显示view
     *
     * @param viewId
     * @param viewId
     * @return
     */
    public DialogViewHolder setViewViSible(int viewId) {
        View view = getView(viewId);
        view.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * 隐藏view
     *
     * @param viewId
     * @param viewId
     * @return
     */
    public DialogViewHolder setViewGone(int viewId) {
        View view = getView(viewId);
        view.setVisibility(View.GONE);
        return this;
    }

    /**
     * 设置点击
     */
    public DialogViewHolder setOnClick(int viewId, OnClickListener onClick) {
        View view = getView(viewId);
        view.setOnClickListener(onClick);
        return this;
    }

    /**
     * 设置触摸事件
     */
    public DialogViewHolder setOnTouch(int viewId, View.OnTouchListener onClick) {
        View view = getView(viewId);
        view.setOnTouchListener(onClick);
        return this;
    }

    /**
     * 设置颜色
     */
    public DialogViewHolder setColor(int viewId, @ColorRes int colorInt) {
        View view = getView(viewId);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(view.getContext().getResources().getColor(colorInt));
        }
        return this;
    }

    /**
     * 根据id获取view
     *
     * @param viewId
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mDialogView.findViewById(viewId);
            if (view != null)
                mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 清理缓存view的列表
     */
    public void clean() {
        if (mViews != null) {
            mViews.clear();
        }
        mDialogView = null;
    }
}
