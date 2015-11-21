package com.baidupanapi;


import com.baidupanapi.util.HttpClientHelper;
import com.baidupanapi.util.TimeUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BaseData {

    /** 所有API URL **/
    /** 获取最快的pcs服务器 **/
    public static final String GET_FASTEST_PCS_SERVER_API = "http://pcs.baidu.com/rest/2.0/pcs/file?app_id=250528&method=locateupload";
    /** Cookies初始化 **/
    public static final String COOKIES_INIT_API = "http://www.baidu.com";
    /** 获取Token(formatstring,需要调用静态方法获取实际url) **/
    private static final String GET_TOKEN_API = "https://passport.baidu.com/v2/api/?getapi&tpl=mn&apiver=v3&class=login&tt=%s&logintype=dialogLogin&callback=0";
    /** 获取PublicKey信息(formatstring,需要调用静态方法获取实际url) **/
    private static final String GET_PUBLICKEY_API = "https://passport.baidu.com/v2/getpublickey?token=%s";
    /** 登陆 **/
    public static final String LOGIN_API = "https://passport.baidu.com/v2/api/?login";
    /** 获取验证码的接口前缀 **/
    public static final String GET_VERIFY_CODE_API = "https://passport.baidu.com/cgi-bin/genimage?";

    public static final String BAIDUPAN_SERVER = "pan.baidu.com";
    public static String BAIDUPCS_SERVER = "pcs.baidu.com";

    public static Map<String,String> baidupanHeaders;
    static{
        baidupanHeaders = new HashMap<String, String>();
        baidupanHeaders.put("Referer", "http://pan.baidu.com/disk/home");
        baidupanHeaders.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.80 Safari/537.36");
    }

    public static String apiTemplate;
    static{
        apiTemplate = String.format("http://%s/api/%s", BAIDUPAN_SERVER,"%s");
    }

    public static Map<String,String> errorMessageMap = new HashMap<>();
    static{
        errorMessageMap.put("-1", "系统错误, 请稍后重试");
        errorMessageMap.put("1", "您输入的帐号格式不正确");
        errorMessageMap.put("3", "验证码不存在或已过期,请重新输入");
        errorMessageMap.put("4", "您输入的帐号或密码有误");
        errorMessageMap.put("5", "请在弹出的窗口操作,或重新登录");
        errorMessageMap.put("6", "验证码输入错误");
        errorMessageMap.put("16", "您的帐号因安全问题已被限制登录");
        errorMessageMap.put("257", "需要验证码");
        errorMessageMap.put("100005", "系统错误, 请稍后重试");
        errorMessageMap.put("120016", "未知错误 120016");
        errorMessageMap.put("120019", "近期登录次数过多, 请先通过 passport.baidu.com 解除锁定");
        errorMessageMap.put("120021", "登录失败,请在弹出的窗口操作,或重新登录");
        errorMessageMap.put("500010", "登录过于频繁,请24小时后再试");
        errorMessageMap.put("400031", "账号异常，请在当前网络环境下在百度网页端正常登录一次");
        errorMessageMap.put("401007", "您的手机号关联了其他帐号，请选择登录");
    }



    public static void main(String[] args) throws Exception {
        BaiduPanService baiduPanService = new BaiduPanService("","",null);
        System.out.println(baiduPanService.quota(null));
    }

    public static String getTokenApi(){
        return String.format(BaseData.GET_TOKEN_API, TimeUtil.getSecondTime());
    }

    public static String getPublicKeyApi(String token){
        return String.format(BaseData.GET_PUBLICKEY_API,token);
    }

}
