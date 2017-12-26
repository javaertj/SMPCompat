package com.drivingassisstantHouse.library.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.drivingassisstantHouse.library.R;


/**
 * 描述:创建对话框
 * 创建人:simpletour
 * 日期:2017/2/23
 * 时间:15:45
 * 版本号:1.0
 */
public abstract class DialogBuilder {
    private Dialog mDialog;
    private Window mDialogWindow;
    private DialogViewHolder dilaogVh;
    private View mRootView;

    public DialogBuilder(Context context, int layoutId) {
        dilaogVh = DialogViewHolder.get(context, layoutId);
        mRootView = dilaogVh.getConvertView();
        mDialog = new Dialog(context, R.style.dialog_transparent_custom);
        mDialog.setContentView(mRootView);
        mDialogWindow = mDialog.getWindow();
        convert(dilaogVh);
    }

    public DialogBuilder setTheme(int style) {
        if (mDialog != null)
            mDialog.getContext().setTheme(style);
        return this;
    }

    public DialogBuilder setBgTransparent() {
        if (mDialog != null&& Build.VERSION.SDK_INT>=21)
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable());//解决5.0以上阴影问题
        return this;
    }

    /**
     * 是否作为系统弹框，需要使用权限android.permission.SYSTEM_ALERT_WINDOW
     */
    public void setCanServiceShow() {
        mDialogWindow.setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
    }

    /**
     * 暴露给view设置各种属性
     */
    public abstract void convert(DialogViewHolder view);

    /**
     * 显示dialog
     */
    public DialogBuilder showDialog() {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
            doStartCount();
        }
        return this;
    }

    /**
     * 从底部一直弹到中间
     */
    public DialogBuilder fromBottomToMiddle() {
        mDialogWindow.setWindowAnimations(R.style.dialog_bottom_in_out_animstyle);
        mDialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        return this;
    }

    /**
     * 从底部弹出
     */
    public DialogBuilder fromBottom() {
        fromBottomToMiddle();
        setDialogGravity(DialogGravity.CENTERBOTTOM);
        return this;
    }

    public DialogBuilder setDialogGravity(DialogGravity dialogGravity) {

        switch (dialogGravity) {
            case LEFTTOP:
                mDialogWindow.setGravity(Gravity.START | Gravity.TOP);
                break;
            case RIGHTTOP:
                mDialogWindow.setGravity(Gravity.END | Gravity.TOP);
                break;
            case CENTERTOP:
                mDialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
                break;
            case CENTER:
                mDialogWindow.setGravity(Gravity.CENTER);
                break;
            case LEFTBOTTOM:
                mDialogWindow.setGravity(Gravity.START | Gravity.BOTTOM);
                break;
            case RIGHTBOTTOM:
                mDialogWindow.setGravity(Gravity.END | Gravity.BOTTOM);
                break;
            case CENTERBOTTOM:
                mDialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
                break;
        }
        return this;
    }

    /**
     * 显示一个Dialog可以传递一个style
     */
    public DialogBuilder showDialog(int style) {
        mDialogWindow.setWindowAnimations(style);
        mDialog.show();
        doStartCount();
        return this;
    }

    /**
     * 显示一个Dialog可以传递一个是否显示动画
     */
    public DialogBuilder showDialog(boolean isAnimation) {
        mDialogWindow.setWindowAnimations(R.style.dialog_jelly_animstyle);
        mDialog.show();
        doStartCount();
        return this;
    }

    /**
     * 全屏显示
     */
    public DialogBuilder fullScreen() {
        WindowManager.LayoutParams wl = mDialogWindow.getAttributes();
        wl.height = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mDialog.onWindowAttributesChanged(wl);
        return this;
    }

    /**
     * 全屏宽度
     */
    public DialogBuilder fullWidth() {
        WindowManager.LayoutParams wl = mDialogWindow.getAttributes();
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mDialog.onWindowAttributesChanged(wl);
        return this;
    }

    /**
     * 设置显示为屏幕宽度的4/5
     *
     * @return
     */
    public DialogBuilder autoWindowWidth() {
        WindowManager windowManager = mDialogWindow.getWindowManager();
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        WindowManager.LayoutParams wl = mDialogWindow.getAttributes();
        wl.width = point.x * 4 / 5;
        mDialog.onWindowAttributesChanged(wl);
        return this;
    }

    /**
     * 全屏高度
     */
    public DialogBuilder fullHeight() {
        WindowManager.LayoutParams wl = mDialogWindow.getAttributes();
        wl.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mDialog.onWindowAttributesChanged(wl);
        return this;
    }

    /**
     * 自己设置高度和宽度
     */
    public DialogBuilder setWidthAndHeight(int width, int height) {
        WindowManager.LayoutParams wl = mDialogWindow.getAttributes();
        wl.width = width;
        wl.height = height;
        mDialog.onWindowAttributesChanged(wl);
        return this;
    }

    /**
     * cancel dialog
     */
    public void cancelDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        if (mTimer != null)
            mTimer.cancel();
        if (dilaogVh != null)
            dilaogVh.clean();
    }

    /**
     * 设置监听
     */
    public DialogBuilder setDialogDismissListener(OnDismissListener listener) {
        mDialog.setOnDismissListener(listener);
        return this;
    }

    /**
     * 设置监听
     */
    public DialogBuilder setOnCancelListener(OnCancelListener listener) {
        mDialog.setOnCancelListener(listener);
        return this;
    }

    /**
     * 设置是否能取消
     */
    public DialogBuilder setCancelAble(boolean cancel) {
        mDialog.setCancelable(cancel);
        return this;
    }

    /**
     * 设置触摸其他地方是否能取消
     */
    public DialogBuilder setCanceledOnTouchOutside(boolean cancel) {
        mDialog.setCanceledOnTouchOutside(cancel);
        return this;
    }


    /**
     * 以下是新增的计时相关***
     */
    private long countDownTime = 0;//总倒计时时间，倒计时到了自动关闭对话框
    private CountDownTimer mTimer;//计时工具
    private TimeChange timeInter;//时间间隔触发回调，每1s执行1次

    public void setTimeInter(TimeChange timeInter) {
        this.timeInter = timeInter;
    }


    /**
     * 设置倒计时时间
     */
    private void doStartCount() {
        if (countDownTime > 1000 && timeInter != null) {
            mTimer = new CountDownTimer(countDownTime, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    timeInter.change(dilaogVh, millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    cancelDialog();
                }
            }.start();
        }
    }


    public interface TimeChange {
        public void change(DialogViewHolder holder, long currentLess);
    }

    public DialogBuilder setAutoCancelTime(long time, TimeChange change) {
        this.countDownTime = time;
        setTimeInter(change);
        return this;
    }
}
