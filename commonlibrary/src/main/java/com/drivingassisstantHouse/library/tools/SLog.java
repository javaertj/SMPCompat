package com.drivingassisstantHouse.library.tools;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * 包名：com.drivingassisstantHouse.library.tools
 * 描述：日志工具
 * 创建者：yankebin
 * 日期：2015/12/9
 */
public class SLog {
    private static boolean DEVELOP_MODE;
    private static String tag = "[AppName]";
    private static int logLevel = Log.DEBUG;//Log.ERROR
    private static final String mClassName = "简途旅行";
    private static File logCacheDir;
    private static File logCacheFile;
    private static final long LOG_SAVE_TIME_LENGTH = new Date().getTime() - 5 * 24 * 60 * 60 * 1000L;

    private SLog() {

    }

    /**
     * 初始化
     *
     * @param enableLog 日志开关
     */
    public static void init(boolean enableLog) {
        DEVELOP_MODE = enableLog;
        if (!DEVELOP_MODE) {
            logLevel = Log.ERROR;
        }
    }

    /**
     * 初始化
     *
     * @param enableLog       日志开关
     * @param logCacheDirName 日志输出目录
     */
    public static void init(boolean enableLog, String logCacheDirName) {
        init(enableLog);
        initCacheFile(logCacheDirName);
    }

    /**
     * 初始化日志输出目录
     *
     * @param logCacheDirName 日志输出目录
     */
    private static void initCacheFile(String logCacheDirName) {
        String logCacheFileName = ToolDateTime.formatDateTime(System.currentTimeMillis(),
                ToolDateTime.DF_YYYY_MM_DD) + "_log.txt";
        logCacheDir = new File(Environment.getExternalStorageDirectory(), logCacheDirName);
        try {
            if (!logCacheDir.mkdirs()) {
                logCacheDir.mkdirs();
            }
            logCacheFile = new File(logCacheDir, logCacheFileName);
            if (!logCacheFile.exists()) {
                logCacheFile.createNewFile();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        checkLogCache();
    }

    /**
     * 日志开关
     *
     * @return
     */
    private static boolean isLogFlag() {
        return DEVELOP_MODE;
    }

    /**
     * Get The Current Function Name
     *
     * @return
     */
    private static String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(SLog.class.getName())) {
                continue;
            }
            tag = st.getFileName();
            return mClassName + "[ " + Thread.currentThread().getName() + ": "
                    + st.getFileName() + ":" + st.getLineNumber() + " "
                    + st.getMethodName() + " ]";
        }
        return null;
    }

    /**
     * The Log Level:i
     *
     * @param str
     */
    public static void i(Object str) {
        log(Log.INFO, str);
    }

    /**
     * The Log Level:d
     *
     * @param str
     */
    public static void d(Object str) {
        log(Log.DEBUG, str);
    }

    /**
     * The Log Level:V
     *
     * @param str
     */
    public static void v(Object str) {
        log(Log.VERBOSE, str);
    }

    /**
     * The Log Level:w
     *
     * @param str
     */
    public static void w(Object str) {
        log(Log.WARN, str);
    }

    /**
     * The Log Level:e
     *
     * @param str
     */
    public static void e(Object str) {
        log(Log.ERROR, str);
    }

    /**
     * The Log Level:e
     *
     * @param ex
     */
    public static void e(Exception ex) {
        log(Log.ERROR, "error", ex);
    }

    /**
     * The Log Level:e
     *
     * @param log
     * @param tr
     */
    public static void e(String log, Throwable tr) {
        String line = getFunctionName();
        String logStr = "{Thread:" + Thread.currentThread().getName() + "}"
                + "[" + mClassName + line + ":] " + log + "\n";
        log(Log.ERROR, logStr, tr);
    }

    /**
     * The Log Level:a
     *
     * @param tr
     */
    public static void a(Throwable tr) {
        String log = handleException(tr);
        String line = getFunctionName();
        String logStr = "{Thread:" + Thread.currentThread().getName() + "}"
                + "[" + mClassName + line + ":] " + log + "\n";
        log(Log.ASSERT, logStr, tr);
    }

    private static void log(int currentLogLevel, Object str) {
        String logStr = getFunctionName();
        if (logStr != null) {
            logStr = logStr + " : " + str;
        } else {
            logStr = str.toString();
        }
        log(currentLogLevel, logStr);
    }

    private static void log(int currentLogLevel, String logStr) {
        log(currentLogLevel, logStr, null);
    }

    private static void log(int currentLogLevel, String logStr, Throwable tr) {
        if (isLogFlag()) {
            if (logLevel <= Log.VERBOSE) {
                Log.v(tag, logStr);
            } else if (logLevel <= Log.DEBUG) {
                Log.d(tag, logStr);
            } else if (logLevel <= Log.INFO) {
                Log.i(tag, logStr);
            } else if (logLevel <= Log.WARN) {
                Log.w(tag, logStr);
            } else if (logLevel <= Log.ERROR) {
                Log.e(tag, logStr, tr);
            } else if (logLevel <= Log.ASSERT) {
                Log.e(tag, logStr, tr);
            }
        }
        if (currentLogLevel >= Log.ERROR) {
            writeLog(logStr);
        }
    }

    private static void writeLog(String logStr) {
        if (null != logCacheFile) {
            synchronized (mClassName) {
                try {
                    ToolFile.write(logCacheFile, logStr + "\r\n", "UTF-8");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String handleException(Throwable error) {
        if (null != error) {
            return Log.getStackTraceString(error);
        }

        return null;
    }


    /**
     * 检测日志缓存，删除5天以前的日志
     */
    private static void checkLogCache() {
        if (null != logCacheDir) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    deleteOldLog(logCacheDir);
                }
            }).start();
        }
    }

    /**
     * 删除文件
     *
     * @param dir
     */
    private static void deleteOldLog(File dir) {
        deleteOldLog(dir, new Date(LOG_SAVE_TIME_LENGTH));
    }

    /**
     * 删除文件
     *
     * @param dir
     * @param lastDate
     */
    private static void deleteOldLog(File dir, Date lastDate) {
        if (!dir.exists()) {
            return;
        }
        File fileList[] = dir.listFiles();
        if (null == fileList || fileList.length < 1) {
            return;
        }
        synchronized (mClassName) {
            for (File file : fileList) {
                String fileName = file.getName();
                if (fileName.contains("_log")) {
                    String dateStr = fileName.split("_")[0];
                    Date fileDateDate = ToolDateTime.parseDate(dateStr, ToolDateTime.DF_YYYY_MM_DD);
                    if (fileDateDate.before(lastDate)) {
                        file.delete();
                    }
                }
            }
        }
    }
}