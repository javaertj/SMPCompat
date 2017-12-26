package com.simpletour.lib.autoupdate;


import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.simpletour.lib.autoupdate.model.UpdateButton;
import com.simpletour.lib.autoupdate.model.UpdateResponse;
import com.simpletour.lib.autoupdate.utils.UpdateUtil;

import java.util.Locale;


/**
 * 包名：com.simpletour.lib.autoupdate
 * 描述：更新弹窗
 * 创建者：yankebin
 * 日期：2016/5/18
 */
public class UpdateDialogActivity extends AppCompatActivity implements DownLoadListener {

    ProgressBar downLoadProgressBar;
    TextView tvProgress;
    Button btnCancelDownLoad;
    TextView tvProgress_;
    LinearLayout layoutProgress;
    ViewGroup rootView;

    private UpdateResponse response;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_dialog);
        setTitle("");
        initViews();
        Bundle param = null == savedInstanceState ? getIntent().getExtras() : savedInstanceState;
        initParam(null == param ? new Bundle() : param);
    }

    private void initViews() {
        downLoadProgressBar = findView(R.id.down_load_progress_bar);
        tvProgress = findView(R.id.tv_progress);
        btnCancelDownLoad = findView(R.id.btn_cancel_down_load);
        tvProgress_ = findView(R.id.tv_progress_);
        layoutProgress = findView(R.id.layout_progress);
        rootView = findView(R.id.layout_update_root);
    }

    private void initParam(Bundle param) {
        if (null != param) {
            response = (UpdateResponse) param.getSerializable(UpdateConfig.UPDATE_RESPONSE_KEY);
        }
        UpdateAgent.setDownLoadListener(this);
    }

    private void doBusiness() {
        final UpdateButton confirmBtn = response.findBtnById("ok");
        UpdateButton cancelBtn = response.findBtnById("cancel");

        UpdateDialog updateDialog = new UpdateDialog(this)
//                .customIconEnable(true)
                .setMTitle(response.getUpdateTitle())
                .setMContent(response.getContent());

        if (null != confirmBtn) {
            updateDialog.setMConfirmText(confirmBtn.getName())
                    .setConfirmClickListener(new UpdateDialog.OnUpdateClickListener() {
                        @Override
                        public void onClick(UpdateDialog updateDialog1) {
                            updateDialog1.cancel();
                            if (TextUtils.isEmpty(confirmBtn.getAction())) {
                                Toast.makeText(UpdateDialogActivity.this, "下载地址出错，更新终止！", Toast.LENGTH_LONG).show();
                                UpdateDialogActivity.this.finish();
                                return;
                            }
                            response.setAndroidDown(confirmBtn.getAction());
                            UpdateAgent.onDialogClick(UpdateDialogActivity.this, response,
                                    UpdateStatus.Update);
                        }
                    });
        }

        updateDialog.setMCancelButtonEnable(true)
                .setMCancelText(null!=cancelBtn?cancelBtn.getName():"取消")
                .setCancelClickListener(new UpdateDialog.OnUpdateClickListener() {
                    @Override
                    public void onClick(UpdateDialog updateDialog1) {
                        updateDialog1.cancel();
                        UpdateAgent.onDialogClick(UpdateDialogActivity.this, response,
                                UpdateStatus.Ignore);
                        UpdateDialogActivity.this.finish();
                    }
                });

        updateDialog.show();
    }


    private <T extends View> T findView(@IdRes int id) {
        return (T) findViewById(id);
    }

    public void downLoadCancel(View view) {
        UpdateAgent.onUserCancel(response.isUpdateForce());
    }

    @Override
    public void finish() {
        super.finish();
        downLoadCancel(null);
    }

    @Override
    public void onDownLoading(final int progress, final int currentSize, final int totalSize) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (View.VISIBLE != layoutProgress.getVisibility()) {
                    layoutProgress.setVisibility(View.VISIBLE);
                    layoutProgress.requestLayout();
                }
                downLoadProgressBar.setProgress(progress);
                tvProgress_.setText(String.format(Locale.CHINESE, "%d%s", progress, "%"));
                String currentP = UpdateUtil.generateDecimalFormat("0.00", currentSize / (Math.pow(2, 20)));
                String totalP = UpdateUtil.generateDecimalFormat("0.00", totalSize / (Math.pow(2, 20)));
                tvProgress.setText(String.format(Locale.CHINESE, "%sM/%sM", currentP, totalP));
            }
        });
    }

    @Override
    public void onDownLoadComplete() {
        UpdateAgent.installApk(this);
        finish();
    }

    @Override
    public void onDownLoadError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UpdateDialogActivity.this, "下载出错，更新终止...", Toast.LENGTH_LONG).show();
                UpdateDialogActivity.this.finish();
            }
        });
    }
}