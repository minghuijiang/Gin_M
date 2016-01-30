/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mingJiang.login.qqLogin;

import cn.smy.dama2.Dama2;
import com.mingJiang.util.CaptchaUtil;
import com.mingJiang.util.HttpUtil;
import com.mingJiang.util.Util;
import com.mingJiang.util.account.Account;
import com.mingJiang.util.account.Cookies;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ming Jiang
 */
public class QQUnlock {
    
    public static boolean getLock(Account acc){
        List<String> result =HttpUtil.sendGet("http://aq.qq.com/cn2/login_limit/checkstate?from=1&account="+acc.getUser()+"&_="+System.currentTimeMillis(),
                null);
        HttpUtil.setCookies(acc.getCookies(), result);
        Util.print(result);
        return result.get(result.size()-1).equals("{\"if_lock\": 1}");
    }
    
    public static boolean getLockDetail(Account acc){
        //http://aq.qq.com/cn2/login_limit/limit_detail_v2?account=2518750129 
        List<String> header = new ArrayList<>();
        header.add(acc.getCookies().getCookie(null));
        List<String> result =HttpUtil.sendGet("http://aq.qq.com/cn2/login_limit/limit_detail_v2?account="+acc.getUser(),
                header);
        HttpUtil.setCookies(acc.getCookies(), result);
        Util.print(result);
        return false;
    }
    
    public static String getCaptcha(Account acc){
        List<String> header = new ArrayList<>();
        header.add(acc.getCookies().getCookie(null));
        byte[] data = HttpUtil.readImage(
                "http://captcha.qq.com/getimage?aid=523005426&"+Math.random()+"&uin="+acc.getUser(),
                null, header, acc.getCookies(),null);
        Dama2.suc=false;
        System.out.println(acc.getCookies());
        return CaptchaUtil.getCaptcha(data);
    }
    
    public static void checkStatus(Account acc, String captcha){
        
        //http://aq.qq.com/cn2/ajax/check_verifycode 
        List<String> header = new ArrayList<>();
        header.add(acc.getCookies().getCookie(null));
        List<String> result = HttpUtil.sendPost(
                "http://aq.qq.com/cn2/ajax/check_verifycode",
                "verify_code="+captcha+"&session_type=on_rand&flag=1&appid=523005426&uin="+acc.getUser(),
                header);
        Util.print(result);
    }
    
    public static String unlock(Account acc, String captcha){
        checkStatus(acc,captcha);
        List<String> header = new ArrayList<>();
        header.add(acc.getCookies().getCookie(null));
        List<String> result = HttpUtil.sendPost("http://aq.qq.com/cn2/login_limit/ulcaptcha", "verify_code="+captcha, header);
        Util.print(result);
        return "";
    }
    
    public static void getResult(Account acc){
        List<String> header = new ArrayList<>();
        header.add(acc.getCookies().getCookie(null));
        List<String> result = HttpUtil.sendGet("http://aq.qq.com/cn2/login_limit/results", header);
        Util.print(result);

    }

    
    
}
