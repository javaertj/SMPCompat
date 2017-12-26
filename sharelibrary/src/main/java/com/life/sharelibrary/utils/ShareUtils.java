package com.life.sharelibrary.utils;

import android.app.Activity;
import android.content.Intent;


import com.umeng.socialize.ShareAction;

import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import com.umeng.socialize.media.UMEmoji;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMediaObject;
import com.umeng.socialize.media.UMusic;

import java.util.List;

/**
 * Created by simpletour on 2016/2/18.
 * 分享，支持
 */
public class ShareUtils {

    /**
     * 调用系统安装了的应用分享
     *
     * @param act   Activity实例，用于发起选择框
     * @param title 标题
     * @param url   可能附带的url地址
     */
    public static void showSystemShareOption(Activity act,
                                             final String title, final String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + title);
        intent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
        act.startActivity(Intent.createChooser(intent, "选择分享到:"));
    }

    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }

    /**
     * 使用umeng分享。注意所在activity需要调用UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
     * 具体原因尚不明确。
     * 注意:同时填写标题和多媒体时，多媒体中的标题会覆盖(目前发现QQ分享时)
     *
     * @param title    分享标题，某些平台可能不支持(新浪微博)
     * @param content  分享的文字内容。不同平台支持的文字长度可能不同。
     * @param url      分享的url地址，不同平台对于url可能存在些许差异，具体查看doc目录下的分享说明docx文档
     * @param act      分享窗口依附的activity，
     * @param platfrom 平台枚举，具体查看
     * @param media    多媒体类型，通常是UMImage,UMEmoji,UMVideo,UMusic
     * @param listener 用于回调分享结果
     * @see SHARE_MEDIA
     **/
    public static void doUMshare(String title, String content, String url, Activity act, SHARE_MEDIA platfrom, UMediaObject media, UMShareListener listener) {
        if (act == null)
            return;
        ShareAction action = new ShareAction(act);
        action.setPlatform(platfrom);
        action.setCallback(listener);

        //注意文字内容长度，不同平台支持长度不同，会造成隔断，具体可以查看doc目录的"友盟平台分享说明.docx"
        if (!isEmpty(content)) {
            action.withText(content);
        }
        //分享链接
        if (!isEmpty(url)) {
            action.withTargetUrl(url);
        }

        //多媒体文件,图片，音乐，视频，不可共存。如果设置了多种，那么最后设置的将覆盖之前的,因此这里使用单个
        if (media != null) {
            if (media instanceof UMImage) {
                action.withMedia((UMImage) media);
            } else if (media instanceof UMEmoji) {
                action.withMedia((UMEmoji) media);
            } else if (media instanceof UMVideo) {
                action.withMedia((UMVideo) media);
            } else {
                action.withMedia((UMusic) media);
            }
        }
        //QQ,QQZONE,朋友圈，邮件，支持title
        if (platfrom == SHARE_MEDIA.QQ || platfrom == SHARE_MEDIA.QZONE || platfrom == SHARE_MEDIA.WEIXIN_CIRCLE
                || platfrom == SHARE_MEDIA.EMAIL) {
            if (!isEmpty(title))
                action.withTitle(title);
        }
        action.share();
    }


}
