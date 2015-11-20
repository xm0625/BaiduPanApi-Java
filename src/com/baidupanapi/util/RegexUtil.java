package com.baidupanapi.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xm on 15-11-20.
 */
public class RegexUtil {

    public static List<String> findAll(String content,String regex,int group){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        List<String> resultList = new ArrayList<>();
        while(matcher.find()) {
            resultList.add(matcher.group(group));
        }
        return resultList;
    }

    public static void main(String[] args) {
        String content = "<!DOCTYPE html><html><head><meta http-equiv=Content-Type content=\"text/html; charset=UTF-8\"></head><body><script>\n" +
                "\n" +
                "\n" +
                "\tvar href = decodeURIComponent(\"http:\\/\\/www.baidu.com\\/cache\\/user\\/html\\/v3Jump.html\")+\"?\"\n" +
                "\n" +
                "var accounts = '&accounts='\n" +
                "\n" +
                "href += \"err_no=257&callback=parent.bd__pcbs__oa36qm&codeString=jxIcaptchaservice353638356d532f51554d64773376316c656d6d5363355375315833394a45734a756146656231453947706152724f4f69696c7548446c4a56657679507858744c757976666b662b7838545a30676e68644d6145575a634c756747484c563965414b577235735268714379726f4a4c437531493273366a2f414a644d792b634c7164755a712b646f502b4c4e36516137523757614236344b736250394765364d7434544e44534638633456545158626c2f334e6b437a4f6648724a78694f4544594475587a474c463854682f322b674478492b42625954786b2b645934654a374e4d366a305846716b7077444a3742635274655142696731333348533457476d54352f3838443452474a2b6c744c5a2f77496b464b2b37785437705a744a3132383433476f35657136612b4c43537630676e714633696c4e56447a782f3555486f37677775465a714b4567&userName=402276694&phoneNumber=&mail=&hao123Param=&u=https://passport.baidu.com/&tpl=&secstate=&gotourl=&authtoken=&loginproxy=&resetpwd=&vcodetype=8226rp8rm5mC7ABBfBnsl0Di\\/S8VQO9IuKneOKYWuo3W3O14DCCFX1\\/xmj43YXYtM7h3SvkWje8JGoirZlvKwjl+rKca7UMPkp4eaw&lstr=&ltoken=&bckv=&bcsync=&bcchecksum=&code=&bdToken=&realnameswitch=&bctime=\"+accounts;\n" +
                "\n" +
                "if(window.location){\n" +
                "    window.location.replace(href);\n" +
                "}else{\n" +
                "   document.location.replace(href); \n" +
                "}\n" +
                "</script>";
        System.out.println(RegexUtil.findAll(content, "err_no=([\\d]+)",1).get(0));
    }
}
