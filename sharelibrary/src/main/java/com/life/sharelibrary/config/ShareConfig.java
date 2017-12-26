package com.life.sharelibrary.config;

import com.umeng.socialize.PlatformConfig;

/**
 * Created by simpletour on 2016/2/18.
 *
 * 有些平台的分享时通过调起本地客户端的形式完成的，因此，并不需要appid，
 * 但是诸如需要使用第三方公司api的平台，新浪，腾讯等，是需要去申请appkey的。
 * 其实新浪微博，人人网可以把appid等配置在代码中，也可在友盟后台中设置，
 * 豆瓣只能在友盟后台设置。
 * 其它需要配置appid的平台，如qq，微信，易信,twitter等都需要在代码中设置。
 * 还有一部分平台需要在mainfest中配置。
 * 友盟目前支持的国内授权的有:新浪微博，腾讯微博，人人网，豆瓣网，微信，QQ(QQ空间)
 * 需要在第三方平台创建应用并提交审核:
 *
 *
 * 各个平台的配置，建议放在全局Application或者程序入口
 */
public class ShareConfig {

    //豆瓣,人人 平台目前只能在友盟服务器端配置
    public static void config(){
        //配置支付宝appid
//        PlatformConfig.setAlipay("2015111700822536");

        //微信 appid appsecret
        PlatformConfig.setWeixin("wxbc7fc4142744f522", "d4624c36b6795d1d99dcf0547af5443d");

        //新浪微博 appkey appsecret
//        PlatformConfig.setSinaWeibo("3921700954","04b48b094faeb16683c32669824ebdad");

        // QQ和Qzone appid appkey-QQ及Qzone使用同一个AppID及Appkey
        PlatformConfig.setQQZone("1104921247", "GRQ44Cj0fc8vzfXK");

        //易信 appkey
//        PlatformConfig.setYixin("yxc0614e80c9304c11b0391514d09f13bf");
//
//        //来往 appid appkey
//        PlatformConfig.setLaiwang("laiwangd497e70d4", "d497e70d4c3e4efeab1381476bac4c5e");

    }
}
