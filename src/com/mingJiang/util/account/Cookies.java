package com.mingJiang.util.account;

import java.util.Date;
import java.util.Hashtable;

public class Cookies {

    public static String[] need_cookies;
    //cookieName -> cookieValue
    public Hashtable<String, String> cookies;
    private long expire;

    public static void setNeed(String[] need_cookies) {
        Cookies.need_cookies = need_cookies;
    }

    public Cookies() {
        cookies = new Hashtable<String, String>();
        expire = 0;
    }

    public Cookies(String line) {
        //expireTime;login_input_name=xxxxx;CAS_AUTO_LOGIN=xxxx
        this();
        if (line == null || line.length() < 1 || line.equalsIgnoreCase("null")) {
            return;
        }
        String[] sp = line.split(";");
        try{
        expire = Long.parseLong(sp[0]);
        }catch(Exception e){
            expire = 0; 
            System.out.println(sp[0]+" 错误");
        }
        for (int i = 1; i < sp.length; i++) {
            String sp2[] = sp[i].split("=", 2);
            addCookies(sp2[0], sp2[1]);
        }
    }

    public void addCookies(String name, String value) {
        String v = value.trim();
        if (v.equalsIgnoreCase("") || v.equals("/")||v.equals("deleted")) {
            cookies.remove(name.trim());
        } else {
            cookies.put(name.trim(), v);
        }
    }

    //Set-Cookie: checkCode6=dd33aaed1ce400aWyIxXzBfMF8xOCIsIjI1NDQzMiJdeeee6d5ed72e9e770cde9e990; 
    public void addCookies(String setCookie) {
        String central = setCookie.split(":", 2)[1].split(";", 2)[0].trim();
        String[] vals = central.split("=", 2);
        cookies.put(vals[0], vals[1]);
    }

    public void setCookies(Hashtable<String, String> newCookies) {
        cookies = newCookies;
    }

    public void setExpire(long time) {
        expire = time;
    }

    public String getVal(String name) {
        return cookies.get(name);
    }

    public boolean isExpired() {
        if (expire < System.currentTimeMillis()) {
            return true;
        }
        return false;
    }

    public long getExpired() {
        return expire;
    }

    public String toString() {
        String result = "" + expire;
        if (need_cookies == null) {
            for (String c : cookies.keySet()) {
                result += ";" + c + "=" + cookies.get(c);
            }
        } else {
            for (String c : need_cookies) {
                result += ";" + c + "=" + cookies.get(c);
            }
        }
        return result;
    }

    public String getCookie(String[] need) {
        String cookieStr = "";
        if (need == null) {
            for (String c : cookies.keySet()) {
                String val = getVal(c);
                if (val != null) {
                    cookieStr += c + "=" + val + "; ";
                }
            }
        } else {
            for (String c : need) {
                String val = getVal(c);
                if (val != null) {
                    cookieStr += c + "=" + val + "; ";
                }
            }
        }
        if (cookieStr.length() > 3) {
            return "Cookie: " + cookieStr.substring(0, cookieStr.length() - 2);
        }
        return "";
    }
}
