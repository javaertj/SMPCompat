package solid.ren.skinlibrary.utils;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import solid.ren.skinlibrary.loader.SkinManager;

/**
 * Created by _SOLID
 * Date:2016/7/9
 * Time:13:56
 */
public class SkinResourcesUtils {

    public static int getResId(String resType, int resId) {
        return SkinManager.getInstance().getResId(resType, resId);
    }

    public static int getGravity(int resId) {
        return SkinManager.getInstance().getGravity(resId);
    }

    public static int getDimen(int resId) {
        return SkinManager.getInstance().getDimen(resId);
    }

    public static int getColor(int resId) {
        return SkinManager.getInstance().getColor(resId);
    }

    public static ColorStateList getNightColorStateList(int resId) {
        return SkinManager.getInstance().getNightColorStateList(resId);
    }

    public static int getNightColor(int resId) {
        return SkinManager.getInstance().getNightColor(resId);
    }

    public static Drawable getDrawable(int resId) {
        return SkinManager.getInstance().getDrawable(resId);
    }

    public static Drawable getNightDrawable(String resName) {
        return SkinManager.getInstance().getNightDrawable(resName);
    }

    /**
     * get drawable from specific directory
     *
     * @param resId res id
     * @param dir   res directory
     * @return drawable
     */
    public static Drawable getDrawable(int resId, String dir) {
        return SkinManager.getInstance().getDrawable(resId, dir);
    }

    public static ColorStateList getColorStateList(int resId) {
        return SkinManager.getInstance().getColorStateList(resId);
    }

    public static int getColorPrimaryDark() {
        Resources resources = SkinManager.getInstance().getResources();
        int identify = -1;
        if (resources == null) {
            return identify;
        }
        if (!isNightMode()) {
            identify = resources.getIdentifier(
                    "colorPrimaryDark",
                    "color",
                    SkinManager.getInstance().getCurSkinPackageName());
        } else {
            identify = resources.getIdentifier(
                    "colorPrimaryDark_night",
                    "color",
                    SkinManager.getInstance().getCurSkinPackageName());
        }
        if (identify > 0) {
            return resources.getColor(identify);
        }
        return identify;
    }

    public static boolean isNightMode() {
        return SkinManager.getInstance().isNightMode();
    }
}
