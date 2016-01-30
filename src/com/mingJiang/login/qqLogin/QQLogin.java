package com.mingJiang.login.qqLogin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mingJiang.gui.Messagable;
import com.mingJiang.util.HttpUtil;
import com.mingJiang.login.Login;
import com.mingJiang.util.CaptchaUtil;
import com.mingJiang.util.Util;
import com.mingJiang.util.account.Account;
import com.mingJiang.util.proxy.MyProxy;
import com.mingJiang.util.proxy.ProxyLibrary;

public class QQLogin extends Login {

    public static String checkCaptchaUrl
            = "http://check.ptlogin2.qq.com/check?uin=%s&appid=549000912&ptlang=2052&js_type=2&js_ver=10009&r=%f";


   
    @Override
    public int login(Account acc, Messagable msg) {

        String verify = getCaptcha(acc);
        if (verify == null) {//验证码出错
            msg.setMsg(acc.getUser() + "： 验证码为null");
            return UNKNOWN_ERROR;
        }
        List<String> prop = getHeader(acc.getCookies());
        prop.add("Host: ptlogin2.qq.com");
        String oldPass = acc.getPass();
        String link = QQHashUtil.getSubmitUrl(acc, verify);

        List<String> result = HttpUtil.sendGet(link, prop);
        String str = result.get(result.size() - 1);
        System.out.println(str);
        if (str.contains("您输入的帐号或密码不正确，请重新输入")) {
            msg.setMsg(acc.getUser() + "： 您输入的帐号或密码不正确，请重新输入 "+acc.getUser()+":"+oldPass+":"+acc.getPass());
            acc.setPass("");
            return PASSWORD_ERROR;
        }else if(str.contains("验证码不正确")){
        	msg.setMsg(acc.getUser() + "：您输入的验证码不正确，请重新输入。 ");
        	CaptchaUtil.errorCaptcha(verify);
        	return login(acc,msg);
        }else if(str.contains("您的帐号暂时无法登录")||str.contains("已经冻结")){
        	msg.setMsg(acc.getUser() + "：您的帐号暂时无法登录,账号冻结 ");
        	acc.setPass("");
        	return Login.FREEZE;
        }
        HttpUtil.setCookies(acc.getCookies(), result);
        if (acc.getCookies().getVal("skey") == null) {
            msg.setMsg(acc.getUser() + ": " + str);
            msg.setMsg("重新登陆.");
            return login(acc);
        }
        Util.print(result);

        return SUCCESS;

    }

    @Override
    public String getCaptcha(Account acc) {
        List<String> prop = getHeader(acc.getCookies());
        prop.add("Host: check.ptlogin2.qq.com");
        
        String url =String.format(checkCaptchaUrl, acc.getUser(), Math.random());
        List<String> result = HttpUtil.sendGet(
               url , prop);

        String data = result.get(result.size() - 1).split("'")[3];
        HttpUtil.setCookies(acc.getCookies(), result);
        if (data.length() == 4)// 免验证码
        {
            return data;
        } else {
            return enterCode(acc);
        }
    }

    public static String captchaUrl
            = "http://captcha.qq.com/getimage?aid=549000912&r=%f&uin=%s";

    protected String enterCode(Account acc) {

        List<String> prop = getHeader(acc.getCookies());
        prop.add("Host: captcha.qq.com");

        String url=  String.format(captchaUrl, Math.random(), acc.getUser());
//        byte[] result = HttpUtil.readImage(
//               url,
//                null, prop, acc.getCookies(), null);

        String s = CaptchaUtil.getCaptcha(url,acc.getCookies());
        
        System.out.println(s+"  "+acc.getCookies().toString());
        while (s == null) {
//            result = HttpUtil.readImage(
//                String.format(captchaUrl, Math.random(), acc.getUser()),
//                null, prop, acc.getCookies(), null);
            s = CaptchaUtil.getCaptcha(url,acc.getCookies());
        }
        
        return s.split("\\|!\\|")[0];
    }

    @Override
    protected void setHeader() {
        header.add("Connection: keep-alive");
        header.add("User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11");
        header.add("Accept: */*");
        header.add("Referer: http://qzone.qq.com/");
        header.add("Accept-Encoding: gzip,deflate,sdch");
        header.add("Accept-Language: en-US,en;q=0.8");
        header.add("Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.3");
    }

    @Override
    protected int autoLogin(Account acc, Messagable msg) {
		// TODO Auto-generated method stub
        //DO nothing, no autologin for tencent.
        return 0;
    }

}
