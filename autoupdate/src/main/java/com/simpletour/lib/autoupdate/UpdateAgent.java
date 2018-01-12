package com.simpletour.lib.autoupdate;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.simpletour.lib.autoupdate.model.UpdateBean;
import com.simpletour.lib.autoupdate.model.UpdateResponse;
import com.simpletour.lib.autoupdate.utils.UpdateUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 包名：com.simpletour.lib.autoupdate
 * 描述：自动更新
 * 创建者：yankebin
 * 日期：2016/5/18
 */
public final class UpdateAgent {
    private static final String TAG = UpdateAgent.class.getName();
    /**
     * APK文件
     */
    private static File cacheFile;
    /**
     * 下载回调接口
     */
    private static DownLoadListener downLoadListener;
    /**
     * 检测更新结果回调接口
     */
    private static UpdateListener updateListener;
    /**
     * 检测更新对话框按钮点击事件回调接口
     */
    private static DialogListener dialogListener;
    /**
     * 用户取消更新回调接口
     */
    private static UpdateCancelListener cancelListener;
    /**
     * 开始检测更新回调的接口
     */
    private static OnCheckUpdateStartCallback onCheckUpdateStartCallback;
    /**
     * 下载开关
     */
    private static boolean interceptFlag = true;
    /**
     * 是否自动弹出更新提示页
     */
    private static boolean isAutoPopup;
    /**
     * 更新提示页是否已经加载过
     */
    private static boolean isPopupLaunched;
    /**
     * 是否仅wifi模式才更新
     */
    private static boolean onlyWifi;

    /**
     * 设置是否只有在wifi模式下才检查更新
     *
     * @param b
     */
    public static void setUpdateOnlyWifi(boolean b) {
        onlyWifi = b;
    }

    /**
     * 是否自动弹出更新对话框
     *
     * @param autoPopup
     */
    public static void setUpdateAutoPopup(boolean autoPopup) {
        isAutoPopup = autoPopup;
    }

    /**
     * 设置更新对话框点击事件的回调接口
     *
     * @param dialogListener
     */
    protected static void setDialogListener(DialogListener dialogListener) {
        UpdateAgent.dialogListener = dialogListener;
    }

    /**
     * 设置下载的回调接口
     *
     * @param downLoadListener
     */
    protected static void setDownLoadListener(DownLoadListener downLoadListener) {
        UpdateAgent.downLoadListener = downLoadListener;
    }

    /**
     * 设置检测更新结果的回调接口
     *
     * @param updateListener
     */
    public static void setUpdateListener(UpdateListener updateListener) {
        UpdateAgent.updateListener = updateListener;
    }

    /**
     * 设置取消操作回调接口
     *
     * @param cancelListener
     */
    public static void setCancelListener(UpdateCancelListener cancelListener) {
        UpdateAgent.cancelListener = cancelListener;
    }

    /**
     * 设置检测更新回调接口
     *
     * @param onCheckUpdateStartCallback
     */
    public static void setOnCheckUpdateStartCallback(OnCheckUpdateStartCallback onCheckUpdateStartCallback) {
        UpdateAgent.onCheckUpdateStartCallback = onCheckUpdateStartCallback;
    }

    /**
     * 用户取消更新
     *
     * @param isForceUpdate
     */
    protected static void onUserCancel(boolean isForceUpdate) {
        if (null != cancelListener) {
            cancelListener.onCancel(isForceUpdate);
        }
        stopDownLoad();
        destroy();
    }

    /**
     * 对话框点击事件
     *
     * @param context
     * @param response
     * @param which
     */
    protected static void onDialogClick(Context context, UpdateResponse response, int which) {
        switch (which) {
            case UpdateStatus.Update:
                performUpdate(context.getApplicationContext(), response);
                break;
            case UpdateStatus.Ignore:
            case UpdateStatus.NotNow:
                destroy();
                return;
        }
        if (null != dialogListener) {
            dialogListener.onClick(which);
        }
    }

    /**
     * 更新操作初始化
     *
     * @param context
     */
    public static void update(Context context) {
        if (onlyWifi && !UpdateUtil.isWifi(context)) {
            Log.d(TAG, "设置了仅wifi模式更新，当前为非wifi网络连接,更新操作终止");
            destroy();
            return;
        }
        checkUpdate(context.getApplicationContext());
    }

    /**
     * 检测更新
     */
    private static void checkUpdate(final Context context) {
        if (null != onCheckUpdateStartCallback) {
            onCheckUpdateStartCallback.onCheckUpdateStart(new UCallback<UpdateBean>() {
                @Override
                public void onSuccess(UpdateBean updateBean) {
                    Log.d(TAG, "检测更新成功");
                    if (null == updateBean) {
                        Log.d(TAG, "更新数据错误，更新终止！");
                        destroy();
                        return;
                    }
                    handleUpdate(context, updateBean.buildUpdateResponse());
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.d(TAG, "检测更新失败");
                    int updateStatus;
                    if (UpdateUtil.isAvailable(context)) {
                        updateStatus = UpdateStatus.Timeout;
                    } else {
                        updateStatus = UpdateStatus.NoneWifi;
                    }
                    if (null != updateListener) {
                        updateListener.onUpdateReturned(updateStatus, null);
                    }
                }
            });
        }
    }

    /**
     * 检查是否下载过最新文件
     *
     * @param context
     * @param response
     * @return
     */
    private static boolean checkIsDownLoaded(Context context, UpdateResponse response) {
        File file = new File(cacheFile, UpdateConfig.APK_SAVE_NAME);
        if (!file.exists()) {
            return false;
        }
        //此方法可以使用
        boolean isDownLoadComplete = response.getFileMd5().equalsIgnoreCase(UpdateUtil
                .getFileMD5String(file));
        if (!isDownLoadComplete) {
            file.delete();
            return false;
        }
        boolean isNewestVersion = UpdateUtil.isNewestVersion(context, file.getAbsolutePath(),
                response.getNewVersion());
        if (!isNewestVersion) {
            file.delete();
            return false;
        }
        //版本号匹配且文件完整
        return true;
    }

    /**
     * 处理更新
     *
     * @param context
     * @param response
     */
    private static void handleUpdate(Context context, UpdateResponse response) {
        if (null == response) {
            Log.d(TAG, "暂无更新！");
            destroy();
            return;
        }
        cacheFile = UpdateUtil.getOwnCacheDirectory(context.getApplicationContext(),
                UpdateConfig.DOWN_LOAD_CACHE_DIR_NAME);
        int updateStatus = UpdateStatus.No;
        if (response.isHasUpdate()) {
            updateStatus = UpdateStatus.Yes;
        }
        if (updateStatus == UpdateStatus.Yes && isAutoPopup) {
            showUpdateDialog(context.getApplicationContext(), response);
        }
        if (null != updateListener) {
            updateListener.onUpdateReturned(updateStatus, response);
        }
    }

    /**
     * 销毁资源
     */
    public static void destroy() {
        cacheFile = null;
        downLoadListener = null;
        updateListener = null;
        dialogListener = null;
        isAutoPopup = false;
        isPopupLaunched = false;
        onlyWifi = false;
        if (null != downLoadTask) {
            interceptFlag = true;
            if (!downLoadTask.isCancelled()) {
                downLoadTask.cancel(true);
            }
            downLoadTask = null;
        }
    }

    /**
     * 显示更新弹出框
     *
     * @param context
     * @param response
     */
    protected static void showUpdateDialog(Context context, UpdateResponse response) {
        if (isPopupLaunched) {
            return;
        }
        isPopupLaunched = true;
        Intent intent = new Intent(context, UpdateDialogActivity.class);
        intent.putExtra(UpdateConfig.UPDATE_RESPONSE_KEY, response);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 下载线程
     */
    private static AsyncTask<String, Integer, Boolean> downLoadTask = new AsyncTask<String, Integer,
            Boolean>() {
        @Override
        protected Boolean doInBackground(String... params) {
            return downLoad(params[0]);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (null != downLoadListener) {
                downLoadListener.onDownLoading(values[0], values[1], values[2]);
            }
        }

        @Override
        protected void onPostExecute(Boolean complete) {
            super.onPostExecute(complete);
            if (null != downLoadListener) {
                if (complete) {
                    downLoadListener.onDownLoadComplete();
                } else {
                    downLoadListener.onDownLoadError();
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * 下载
         * @param apkUrl
         */
        private boolean downLoad(String apkUrl) {
            try {
                URL url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (null == conn) {
                    return false;
                }
                conn.connect();
                int length = conn.getContentLength();
                if (length <= 0) {
                    conn.disconnect();
                    return false;
                }

                InputStream is = conn.getInputStream();
                File apkFile = new File(cacheFile, UpdateConfig.APK_SAVE_NAME);
                if (!apkFile.exists()) {
                    apkFile.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(apkFile);

                int count = 0;
                byte buf[] = new byte[1024];
                do {
                    int numRead = is.read(buf);
                    //下载完毕
                    if (numRead == -1) {
                        stopDownLoad();
                        break;
                    }
                    //更新进度
                    count += numRead;
                    int progress = (int) ((count * 1f / length) * 100);
                    onProgressUpdate(progress, count, length);
                    //写入缓冲区
                    fos.write(buf, 0, numRead);
                    fos.flush();
                } while (!interceptFlag);//点击取消就停止下载

                fos.close();
                is.close();
                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                stopDownLoad();
                return false;
            }

            return true;
        }
    };

    /**
     * 安装APK
     *
     * @param context
     */
    protected static void installApk(Context context) {
        File file = new File(cacheFile, UpdateConfig.APK_SAVE_NAME);
        if (file.exists()) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                UpdateUtil.installApk7(context.getApplicationContext(), file);
            } else {
                UpdateUtil.installApk(context.getApplicationContext(), file.getAbsolutePath());
            }
        }
        destroy();
    }

    /**
     * 终止下载
     */
    protected static void stopDownLoad() {
        interceptFlag = true;
    }

    /**
     * 确认更新
     *
     * @param context
     * @param response
     */
    private static void performUpdate(Context context, UpdateResponse response) {
        if (checkIsDownLoaded(context.getApplicationContext(), response)) {
            if (null != downLoadListener) {
                downLoadListener.onDownLoadComplete();
            } else {
                installApk(context);
            }
        } else {
            interceptFlag = false;
            //为了让下载立即执行
            ExecutorService executor = Executors.newCachedThreadPool();
            downLoadTask.executeOnExecutor(executor, response.getAndroidDown());
            //不在接受新任务,已添加的任务继续执行
            executor.shutdown();
        }
    }
}
