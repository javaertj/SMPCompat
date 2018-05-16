package com.drivingassisstantHouse.library.tools;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

/**
 * Description：存储页面的缓存
 * Creator：yankebin
 * CreatedAt：2018/5/16
 */
public final class PageCache {
    private final static LinkedList<WeakReference<Activity>> CACHED_PAGES = new LinkedList<>();

    public static void put(@NonNull Activity activity) {
        synchronized (CACHED_PAGES) {
            CACHED_PAGES.addFirst(new WeakReference<>(activity));
        }
    }

    public static void remove(@NonNull Activity activity) {
        synchronized (CACHED_PAGES) {
            WeakReference<Activity> find = null;
            for (WeakReference<Activity> weakReference : CACHED_PAGES) {
                if (null == weakReference || null == weakReference.get()) {
                    continue;
                }
                if (weakReference.get() == activity) {
                    find = weakReference;
                }
            }
            if (null != find) {
                if (!find.get().isFinishing()) {
                    find.get().finish();
                }
                CACHED_PAGES.remove(find);
            }
        }
    }

    public static void clear() {
        synchronized (CACHED_PAGES) {
            if (!CACHED_PAGES.isEmpty()) {
                for (WeakReference<Activity> activityWeakReference : CACHED_PAGES) {
                    if (null == activityWeakReference || null == activityWeakReference.get()) {
                        continue;
                    }
                    if (!activityWeakReference.get().isFinishing()) {
                        activityWeakReference.get().finish();
                    }
                }
            }
            CACHED_PAGES.clear();
        }
    }

    public static boolean isSurvival(Activity source) {
        synchronized (CACHED_PAGES) {
            if (CACHED_PAGES.isEmpty()) {
                return false;
            }

            for (WeakReference<Activity> activityWeakReference : CACHED_PAGES) {
                if (null == activityWeakReference || null == activityWeakReference.get()) {
                    continue;
                }
                if (activityWeakReference.get() == source) {
                    return true;
                }
            }
        }

        return false;
    }

    public LinkedList<WeakReference<Activity>> getCachedPages() {
        return CACHED_PAGES;
    }
}
