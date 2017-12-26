package com.simpletour.lib.autoupdate;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 包名： com.simpletour.lib.autoupdate
 * 描述：更新弹窗
 * 创建者：yankebin
 * 日期：2016/7/28
 */

public class UpdateDialog extends Dialog implements View.OnClickListener {
    public static interface OnUpdateClickListener {
        public void onClick(UpdateDialog updateDialog);
    }

    private TextView tvTitle, tvContent;
    private ImageView ivIcon;
    private Button btnConfirm, btnCancel;
    private View contentView;
    private OnUpdateClickListener confirmListener, cancelListener;

    public UpdateDialog(Context context) {
        this(context, R.style.Dialog_FS);
    }

    public UpdateDialog(Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    private void init() {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.layout_update_dialog);
        contentView = findViewById(R.id.layout_update_root);
        ivIcon = (ImageView) findViewById(R.id.iv_update_icon);
        tvTitle = (TextView) findViewById(R.id.tv_update_title);
        tvContent = (TextView) findViewById(R.id.tv_update_content);
        btnConfirm = (Button) findViewById(R.id.btn_update_confirm);
        btnCancel = (Button) findViewById(R.id.btn_update_cancel);

        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startWidthAnimation();
    }

    private void startWidthAnimation() {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(contentView, "scaleX", 0.0f, 1.0f, 0.5f, 1.0f, 0.8f, 1.0f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(contentView, "scaleY", 0.0f, 1.0f, 0.5f, 1.0f, 0.8f, 1.0f);
        ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(contentView, "alpha", 0.0f, 1.0f);
        ObjectAnimator animatorRotation = ObjectAnimator.ofFloat(contentView, "rotation", 0.0f, 360.0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorX, animatorY, animatorAlpha, animatorRotation);
        animatorSet.setDuration(800);
        animatorSet.start();
    }

    public UpdateDialog setMTitle(CharSequence title) {
        if (null != tvTitle) {
            tvTitle.setText(title);
        }
        return this;
    }


    public UpdateDialog setMContent(CharSequence content) {
        if (null != tvContent) {
            tvContent.setText(content);
        }
        return this;
    }

    public UpdateDialog customIconEnable(boolean enable) {
        if (null != ivIcon) {
            ivIcon.setVisibility(enable ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public UpdateDialog setMIcon(int resId) {
        if (null != ivIcon) {
            ivIcon.setVisibility(View.VISIBLE);
            ivIcon.setImageResource(resId);
        }
        return this;

    }

    public UpdateDialog setMConfirmText(CharSequence confirmText) {
        if (null != btnConfirm) {
            btnConfirm.setText(confirmText);
        }
        return this;
    }

    public UpdateDialog setMCancelButtonEnable(boolean enable) {
        btnCancel.setVisibility(enable ? View.VISIBLE : View.GONE);
        return this;
    }

    public UpdateDialog setMCancelText(CharSequence cancelText) {
        if (null != btnCancel) {
            btnCancel.setText(cancelText);
        }
        return this;
    }

    public void setConfirmClickListener(OnUpdateClickListener updateClickListener) {
        confirmListener = updateClickListener;
    }

    public void setCancelClickListener(OnUpdateClickListener updateClickListener) {
        cancelListener = updateClickListener;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_update_confirm) {
            if (null != confirmListener) {
                confirmListener.onClick(UpdateDialog.this);
            } else {
                dismiss();
            }
        } else if (v.getId() == R.id.btn_update_cancel) {
            if (null != cancelListener) {
                cancelListener.onClick(this);
            } else {
                dismiss();
            }
        }
    }
}
