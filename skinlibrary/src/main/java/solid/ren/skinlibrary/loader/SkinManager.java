package solid.ren.skinlibrary.loader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import solid.ren.skinlibrary.ISkinUpdate;
import solid.ren.skinlibrary.SkinConfig;
import solid.ren.skinlibrary.SkinLoaderListener;
import solid.ren.skinlibrary.utils.ResourcesCompat;
import solid.ren.skinlibrary.utils.SkinFileUtils;
import solid.ren.skinlibrary.utils.SkinL;
import solid.ren.skinlibrary.utils.TypefaceUtils;


/**
 * Created by _SOLID
 * Date:2016/4/13
 * Time:21:07
 */
public class SkinManager implements ISkinLoader {
    private static final String TAG = "SkinManager";
    private List<ISkinUpdate> mSkinObservers;
    @SuppressLint("StaticFieldLeak")
    private static volatile SkinManager mInstance;
    private Context context;
    private Resources mResources;
    private boolean isDefaultSkin = false;

    /**
     * skin package name
     */
    private String skinPackageName;

    private SkinManager() {
    }

    public static SkinManager getInstance() {
        if (mInstance == null) {
            synchronized (SkinManager.class) {
                if (mInstance == null) {
                    mInstance = new SkinManager();
                }
            }
        }
        return mInstance;
    }

    public void destroy() {
        checkConfiguration();
        synchronized (SkinManager.class) {
            if (null != mSkinObservers) {
                mSkinObservers.clear();
                mSkinObservers = null;
            }
            isDefaultSkin = false;
            skinPackageName = null;
            mResources = null;
            context = null;
            mInstance = null;
        }
    }

    public void init(Context ctx) {
        if (null == ctx) {
            throw new IllegalArgumentException("SkinManager context can not be initialized with null");
        }
        if (null == context) {
            context = ctx.getApplicationContext();
            TypefaceUtils.CURRENT_TYPEFACE = TypefaceUtils.getTypeface(context);
            setUpSkinFile(context);
            if (SkinConfig.isInNightMode(ctx)) {
                SkinManager.getInstance().nightMode();
            } else {
                if (SkinConfig.isDefaultSkin(context)) {
                    return;
                }
//                String skin = SkinConfig.getCustomSkinPath(context);
//                loadSkin(skin, null);
            }
        } else {
            SkinL.w(TAG, "Try to initialize SkinManager which had already been initialized " +
                    "before.");
        }
    }

    private void setUpSkinFile(Context context) {
        try {
            String[] skinFiles = context.getAssets().list(SkinConfig.SKIN_DIR_NAME);
            for (String fileName : skinFiles) {
                File file = new File(SkinFileUtils.getSkinDir(context), fileName);
//                if (!file.exists()) {
                SkinFileUtils.copySkinAssetsToDir(context, fileName,
                        SkinFileUtils.getSkinDir(context));
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getColorPrimaryDark() {
        checkConfiguration();
        if (mResources != null) {
            int identify = mResources.getIdentifier("colorPrimaryDark", "color",
                    skinPackageName);
            if (identify > 0) {
                return mResources.getColor(identify);
            }
        }
        return -1;
    }

    public boolean isExternalSkin() {
        checkConfiguration();
        return !isDefaultSkin && mResources != null;
    }

    public String getCurSkinPackageName() {
        checkConfiguration();
        return skinPackageName;
    }

    public Resources getResources() {
        checkConfiguration();
        return mResources;
    }

    public void restoreDefaultTheme() {
        checkConfiguration();
        SkinConfig.saveSkinPath(context, SkinConfig.DEFAULT_SKIN);
        isDefaultSkin = true;
        SkinConfig.setNightMode(context, false);
        mResources = context.getResources();
        skinPackageName = context.getPackageName();
        notifySkinUpdate();
    }

    @Override
    public void attach(ISkinUpdate observer) {
        checkConfiguration();
        if (mSkinObservers == null) {
            mSkinObservers = new ArrayList<>();
        }
        if (!mSkinObservers.contains(observer)) {
            mSkinObservers.add(observer);
        }
    }

    @Override
    public void detach(ISkinUpdate observer) {
        checkConfiguration();
        if (mSkinObservers != null && mSkinObservers.contains(observer)) {
            mSkinObservers.remove(observer);
        }
    }

    @Override
    public void notifySkinUpdate() {
        checkConfiguration();
        if (mSkinObservers != null) {
            for (ISkinUpdate observer : mSkinObservers) {
                observer.onThemeUpdate();
            }
        }
    }

    public boolean isNightMode() {
        checkConfiguration();
        return SkinConfig.isInNightMode(context);
    }

    //region Load skin or font

    /**
     * 加载皮肤的线程
     */
    private static final class LoadSkinAsyncTask extends AsyncTask<String, Void, Resources> {
        private SkinLoaderListener callback;

        public LoadSkinAsyncTask(SkinLoaderListener callback) {
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            if (null != callback) {
                callback.onStart();
            }
        }

        @Override
        protected Resources doInBackground(String... params) {
            try {
                if (params.length == 1) {
                    final String skinPath = params[0];
                    if (TextUtils.isEmpty(skinPath)) {
                        return null;
                    }
                    boolean isRemoteSkin = skinPath.startsWith("http") ||
                            skinPath.startsWith("https");
                    Resources skinResource;
                    if (isRemoteSkin) {
                        String fileName = skinPath.substring(skinPath.lastIndexOf("/") + 1);
                        File file = new File(SkinFileUtils.getSkinDir(SkinManager.getInstance()
                                .context), fileName);
                        //已经下载，直接加载
                        if (file.exists()) {
                            skinResource = loadLocalSkin(fileName);
                        }
                        //未下载，先下载
                        else {
                            skinResource = loadRemoteSkin(file.getAbsolutePath(), skinPath);
                        }
                    } else {
                        skinResource = loadLocalSkin(skinPath);
                    }

                    if (null != skinResource) {
                        SkinManager.getInstance().isDefaultSkin = false;
                        return skinResource;
                    }
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Resources result) {
            SkinManager.getInstance().mResources = result;
            if (result != null) {
                SkinConfig.setNightMode(SkinManager.getInstance().context, false);
                SkinManager.getInstance().notifySkinUpdate();
                if (callback != null) {
                    callback.onSuccess();
                }
            } else {
                SkinManager.getInstance().isDefaultSkin = true;
                if (callback != null) {
                    callback.onFailed("没有获取到资源");
                }
            }
        }

        /**
         * 加载网络皮肤
         *
         * @param fileName
         * @param downloadUrl
         * @return
         */
        private Resources loadRemoteSkin(String fileName, String downloadUrl) {
            boolean success = false;
            try {
                URL url = new URL(downloadUrl);
                //打开连接
                URLConnection conn = url.openConnection();
                //打开输入流
                InputStream is = conn.getInputStream();
                //获得长度
                int contentLength = conn.getContentLength();
                if (contentLength <= 0) {
                    return null;
                }
                //创建字节流
                byte[] bs = new byte[1024];
                float progress = 0;
                int len;
                OutputStream os = new FileOutputStream(fileName);
                //写数据
                while ((len = is.read(bs)) != -1) {
                    os.write(bs, 0, len);
                    if (null != callback) {
                        progress += len;
                        callback.onProgress((int) (progress / contentLength * 100));
                    }
                }
                //完成后关闭流
                os.close();
                is.close();

                success = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return success ? loadLocalSkin(fileName.substring(fileName.lastIndexOf("/") + 1))
                    : null;
        }

        /**
         * 加载本地皮肤
         *
         * @param fileName
         * @return
         */
        private Resources loadLocalSkin(String fileName) {
            try {
                Context context = SkinManager.getInstance().context;
                String skinPkgPath = SkinFileUtils.getSkinDir(context) + File.separator + fileName;
                SkinL.i(TAG, "skinPackagePath:" + skinPkgPath);
                File file = new File(skinPkgPath);
                if (!file.exists()) {
                    return null;
                }
                PackageManager mPm = context.getPackageManager();
                PackageInfo mInfo = mPm.getPackageArchiveInfo(skinPkgPath,
                        PackageManager.GET_ACTIVITIES);
                SkinManager.getInstance().skinPackageName = mInfo.packageName;
                AssetManager assetManager = AssetManager.class.newInstance();
                Method addAssetPath = assetManager.getClass().getMethod("addAssetPath",
                        String.class);
                addAssetPath.invoke(assetManager, skinPkgPath);

                Resources superRes = context.getResources();
                Resources skinResource = ResourcesCompat.getResources(assetManager,
                        superRes.getDisplayMetrics(), superRes.getConfiguration());
                SkinConfig.saveSkinPath(context, fileName);

                return skinResource;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    /**
     * load skin form local
     * <p>
     * eg:theme.skin
     * </p>
     *
     * @param skinName the name of skin(in assets/skin)
     * @param callback load Callback
     */
    public void loadSkin(@NonNull String skinName, SkinLoaderListener callback) {
        checkConfiguration();
        LoadSkinAsyncTask loadSkinAsyncTask = new LoadSkinAsyncTask(callback);
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            loadSkinAsyncTask.executeOnExecutor(executor, skinName);
            //不在接受新任务,已添加的任务继续执行
            executor.shutdown();
            if (!executor.awaitTermination(40, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                executor.shutdownNow();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }


    /**
     * load font
     *
     * @param fontName font name in assets/fonts
     */
    public void loadFont(String fontName) {
        checkConfiguration();
        Typeface tf = TypefaceUtils.createTypeface(context, fontName);
        TextViewRepository.applyFont(tf);
    }

    public void nightMode() {
        checkConfiguration();
        if (!isDefaultSkin) {
            restoreDefaultTheme();
        }
        SkinConfig.setNightMode(context, true);
        notifySkinUpdate();
    }


    private void checkConfiguration() {
        if (null == context) {
            throw new IllegalStateException("SkinManager must be init with context before using");
        }
    }

    //endregion

    //region Resource obtain
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public int getColor(int resId) {
        int originColor = ContextCompat.getColor(context, resId);
        if (mResources == null || isDefaultSkin) {
            return originColor;
        }

        String resName = context.getResources().getResourceEntryName(resId);

        int trueResId = mResources.getIdentifier(resName, "color", skinPackageName);
        int trueColor;
        if (trueResId == 0) {
            trueColor = originColor;
        } else {
            trueColor = mResources.getColor(trueResId);
        }
        return trueColor;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public ColorStateList getNightColorStateList(int resId) {

        String resName = mResources.getResourceEntryName(resId);
        String resNameNight = resName + "_night";
        int nightResId = mResources.getIdentifier(resNameNight, "color", skinPackageName);
        if (nightResId == 0) {
            return ContextCompat.getColorStateList(context, resId);
        } else {
            return ContextCompat.getColorStateList(context, nightResId);
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public int getNightColor(int resId) {

        String resName = mResources.getResourceEntryName(resId);
        String resNameNight = resName + "_night";
        int nightResId = mResources.getIdentifier(resNameNight, "color", skinPackageName);
        if (nightResId == 0) {
            return ContextCompat.getColor(context, resId);
        } else {
            return ContextCompat.getColor(context, nightResId);
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public Drawable getNightDrawable(String resName) {

        String resNameNight = resName + "_night";

        int nightResId = mResources.getIdentifier(resNameNight, "drawable", skinPackageName);
        if (nightResId == 0) {
            nightResId = mResources.getIdentifier(resNameNight, "mipmap", skinPackageName);
        }
        Drawable color;
        if (nightResId == 0) {
            int resId = mResources.getIdentifier(resName, "drawable", skinPackageName);
            if (resId == 0) {
                resId = mResources.getIdentifier(resName, "mipmap", skinPackageName);
            }
            color = mResources.getDrawable(resId);
        } else {
            color = mResources.getDrawable(nightResId);
        }
        return color;
    }

    /**
     * get drawable from specific directory
     *
     * @param resId res id
     * @param dir   res directory
     * @return drawable
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public Drawable getDrawable(int resId, String dir) {
        Drawable originDrawable = ContextCompat.getDrawable(context, resId);
        if (mResources == null || isDefaultSkin) {
            return originDrawable;
        }
        String resName = context.getResources().getResourceEntryName(resId);
        int trueResId = mResources.getIdentifier(resName, dir, skinPackageName);
        Drawable trueDrawable;
        if (trueResId == 0) {
            trueDrawable = originDrawable;
        } else {
            if (android.os.Build.VERSION.SDK_INT < 22) {
                trueDrawable = mResources.getDrawable(trueResId);
            } else {
                trueDrawable = mResources.getDrawable(trueResId, null);
            }
        }
        return trueDrawable;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public Drawable getDrawable(int resId) {
        Drawable originDrawable = ContextCompat.getDrawable(context, resId);
        if (mResources == null || isDefaultSkin) {
            return originDrawable;
        }
        String resName = context.getResources().getResourceEntryName(resId);
        int trueResId = mResources.getIdentifier(resName, "drawable", skinPackageName);
        Drawable trueDrawable;
        if (trueResId == 0) {
            trueResId = mResources.getIdentifier(resName, "mipmap", skinPackageName);
        }
        if (trueResId == 0) {
            trueDrawable = originDrawable;
        } else {
            if (android.os.Build.VERSION.SDK_INT < 22) {
                trueDrawable = mResources.getDrawable(trueResId);
            } else {
                trueDrawable = mResources.getDrawable(trueResId, null);
            }
        }
        return trueDrawable;
    }

    /**
     * 加载指定资源颜色drawable,转化为ColorStateList，保证selector类型的Color也能被转换。
     * 无皮肤包资源返回默认主题颜色
     * author:pinotao
     *
     * @param resId resources id
     * @return ColorStateList
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public ColorStateList getColorStateList(int resId) {
        boolean isExternalSkin = true;
        if (mResources == null || isDefaultSkin) {
            isExternalSkin = false;
        }

        String resName = context.getResources().getResourceEntryName(resId);
        if (isExternalSkin) {
            int trueResId = mResources.getIdentifier(resName, "color", skinPackageName);
            ColorStateList trueColorList;
            if (trueResId == 0) { // 如果皮肤包没有复写该资源，但是需要判断是否是ColorStateList

                return ContextCompat.getColorStateList(context, resId);
            } else {
                trueColorList = mResources.getColorStateList(trueResId);
                return trueColorList;
            }
        } else {
            return ContextCompat.getColorStateList(context, resId);
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public int getDimen(int resId) {
        int originDimen = context.getResources().getDimensionPixelSize(resId);
        if (mResources == null || isDefaultSkin) {
            return originDimen;
        }
        String resName = context.getResources().getResourceEntryName(resId);
        int trueResId = mResources.getIdentifier(resName, "dimen", skinPackageName);
        int trueDimen;
        if (trueResId == 0) {
            trueDimen = originDimen;
        } else {
            trueDimen = mResources.getDimensionPixelSize(trueResId);
        }
        return trueDimen;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public int getGravity(int resId) {
        int originInteger = context.getResources().getInteger(resId);
        if (mResources == null || isDefaultSkin) {
            return originInteger;
        }
        String resName = context.getResources().getResourceEntryName(resId);
        int trueResId = mResources.getIdentifier(resName, "gravity", skinPackageName);
        int trueInteger;
        if (trueResId == 0) {
            trueInteger = originInteger;
        } else {
            trueInteger = mResources.getInteger(trueResId);
        }
        return trueInteger;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public int getResId(String resType, int resId) {
        String resName = context.getResources().getResourceEntryName(resId);
        int originResId = context.getResources().getIdentifier(resName, resType,
                context.getPackageName());
        if (mResources == null || isDefaultSkin) {
            return originResId;
        }
        int trueResId = mResources.getIdentifier(resName, resType, skinPackageName);
        if (trueResId == 0) {
            trueResId = originResId;
        }
        return trueResId;
    }
    //endregion
}
