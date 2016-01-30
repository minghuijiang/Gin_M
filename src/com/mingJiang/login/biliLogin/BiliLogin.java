/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mingJiang.login.biliLogin;

import com.mingJiang.gui.Messagable;
import com.mingJiang.login.Login;
import com.mingJiang.util.CaptchaUtil;
import com.mingJiang.util.EncodeUtil;
import com.mingJiang.util.HttpUtil;
import com.mingJiang.util.account.Account;
import java.util.List;

/**
 *
 * @author Ming Jiang
 */
public class BiliLogin extends Login{

    public static String loginUrl ="https://secure.bilibili.tv/login";
    public static String imageUrl="https://secure.bilibili.tv/captcha?r=";
    @Override
    public int login(Account acc, Messagable msg) {
        if(autoLogin(acc,msg)==Login.SUCCESS){
            return Login.SUCCESS;
        }
        byte[] image = HttpUtil.readImage(imageUrl+Math.random(), null, getHeader(), acc.getCookies(), null);
        String captcha = CaptchaUtil.getCaptcha(image);
        while(captcha==null){
            msg.error("null 验证码 重试");
            image = HttpUtil.readImage(imageUrl+Math.random(), null, getHeader(), acc.getCookies(), null);
            captcha = CaptchaUtil.getCaptcha(image);
        }
        String capt= captcha.split("\\|!\\|")[0];
        String data ="act=login&gourl=&keeptime=604800&userid="+EncodeUtil.encode(acc.getUser())+
                    "&pwd="+EncodeUtil.encode(acc.getPass())+"&vdcode="+capt+"&keeptime=2592000";
        List<String> result = HttpUtil.sendPost(loginUrl, data, getHeader(acc));
        HttpUtil.setCookies(acc.getCookies(), result);
        String val = result.get(result.size()-1);
        if(autoLogin(acc,msg)==Login.SUCCESS){
            return Login.SUCCESS;
        }else if(val.contains("验证码错误")){
            msg.error("验证码错误");
            CaptchaUtil.errorCaptcha(captcha);
            return login(acc,msg);
        }else {
            msg.error("密码或者账号错误？");
            System.out.println(val);
            return Login.PASSWORD_ERROR;
        }
    }

    @Override
    protected void setHeader() {
        header.add("Host: secure.bilibili.tv");
        header.add("Connection: keep-alive");
        header.add("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        header.add("User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36");
        header.add("Referer: http://www.bilibili.tv/");
        header.add("Accept-Encoding: gzip,deflate,sdch");
        header.add("Accept-Language: en-US,en;q=0.8");
    }

    @Override
    public String getCaptcha(Account acc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static String loginStatus ="http://interface.bilibili.cn/nav.js";
    @Override
    protected int autoLogin(Account acc, Messagable msg) {
        List<String> result = HttpUtil.sendGet(loginStatus, getHeader(acc));
        if(result.get(result.size()-1).contains("({\"isLogin\":true"))
            return Login.SUCCESS;
        else
            return Login.SERVER_ERROR;
    
    }
    
    
    public static void main(String[] args){
        Account acc = new Account("minghuijiang32@gmail.com","369306852@BL");
        System.out.println(new BiliLogin().login(acc));
        System.out.println(acc);
    }
}
