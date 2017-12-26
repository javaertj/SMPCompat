package com.simpletour.lib.autoupdate.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * 包名：com.simpletour.lib.autoupdate.utils
 * 描述：更新工具类
 * 创建者：yankebin
 * 日期：2016/5/18
 */
public class UpdateUtil {

    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    /**
     * 获取当前目录
     *
     * @param context
     * @param cacheDir
     * @return
     */
    public static File getOwnCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
        }
        if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 判断当前是否是wifi网络
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        NetworkInfo networkInfo = getNetWorkInfo(context);
        if (networkInfo != null) {
            if (networkInfo.getTypeName().equalsIgnoreCase("WiFi")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断网络是否可用
     *
     * @return 是/否
     */
    public static boolean isAvailable(Context context) {
        NetworkInfo networkInfo = getNetWorkInfo(context);
        return null != networkInfo && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    private static NetworkInfo getNetWorkInfo(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (null == manager) {
            return null;
        }
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo;
    }

    /**
     * 获取文件的MD5值
     *
     * @param file 文件
     * @return 文件的MD5值
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String getFileMD5String(File file) {
        String md5Str = "";
        FileInputStream in = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            FileChannel ch = in.getChannel();
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            messageDigest.update(byteBuffer);
            md5Str = byteArrayToHex(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return md5Str;
    }

    /**
     * byte数组转换成16进制字符串
     *
     * @param byteArray 需要转换的数组
     * @return 转换后的字符串
     */
    public static String byteArrayToHex(byte[] byteArray) {
        // 首先初始化一个字符数组，用来存放每个16进制字符
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
                'E', 'F'};
        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
        char[] resultCharArray = new char[byteArray.length * 2];
        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        // 字符数组组合成字符串返回
        return new String(resultCharArray);
    }

    /**
     * 获取APK文件中的信息
     *
     * @param context
     * @param apkPath
     * @return
     */
    public static PackageInfo getApplicationInfoFromApk(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        return pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
    }

    /**
     * 是否是最新的app
     *
     * @param context
     * @param apkPath
     * @param newVersionName
     * @return
     */
    public static boolean isNewestVersion(Context context, String apkPath, String newVersionName) {
        PackageInfo packageInfo = getApplicationInfoFromApk(context, apkPath);
        if (null == packageInfo) {
            return false;
        }
        return TextUtils.equals("v".concat(packageInfo.versionName), newVersionName);
    }

    /**
     * 安装APK文件
     *
     * @param context
     * @param apkPath
     */
    public static void installApk(Context context, String apkPath) {
        File apkFile = new File(apkPath);
        if (!apkFile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkFile.getAbsolutePath()),
                "application/vnd.android.package-archive");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }


    /**
     * 生成对应位数的小数
     *
     * @param format
     * @param source
     * @return
     */
    public static String generateDecimalFormat(String format, double source) {
        return new DecimalFormat(format).format(source);
    }
}
