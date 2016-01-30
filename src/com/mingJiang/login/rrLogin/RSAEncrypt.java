package com.mingJiang.login.rrLogin;

import com.mingJiang.data.Hide;
import com.mingJiang.util.HttpUtil;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.mingJiang.util.EncodeUtil;
import com.mingJiang.util.Util;
import com.mingJiang.util.account.Account;
import com.mingJiang.util.json.JSONException;
import com.mingJiang.util.json.Json;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
public class RSAEncrypt {

    public static ScriptEngine rsaEngine = new ScriptEngineManager().getEngineByName("JavaScript");
    public static Invocable rsaInv;
    public static boolean hasSet = false;
    public static String rKey = "d0cf42c2d3d337f9e5d14083f2d52cb2";
    public static boolean isEncrypt = true;
    static {
        try {
            rsaEngine.eval(Hide.get(Hide.rr));
            rsaInv = (Invocable) rsaEngine;
            ArrayList<String> header = new ArrayList<String>();
//            header.add("Host: www.renren.com");
//            header.add("Connection: keep-alive");
//            header.add("Origin: http://www.renren.com");
//            header.add("X-Requested-With: XMLHttpRequest");
//            header.add("User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36");
//            header.add("Content-Type: application/x-www-form-urlencoded");
//            header.add("Accept: */*");
//            header.add("Referer: http://www.renren.com/");
//            header.add("Accept-Encoding: gzip,deflate,sdch");
//            header.add("Accept-Language: en-US,en;q=0.8");
            List<String> data = HttpUtil.sendGet("http://login.renren.com/ajax/getEncryptKey",
                    header);
            /*
             * do parse the encrypt info
             */
            /*			System.out.println("============RSAEncrypt=======");
             Util.print(data);
             System.out.println("=============================");*/
            Json json = new Json(data.get(data.size() - 1));
            // {"isEncrypt":"false"} in some retard case;
            if(json.getString("isEncrypt").equalsIgnoreCase("false")){
                isEncrypt = false;
            }else{
            System.out.println(json.toFormatString());
            rsaInv.invokeFunction("setGeneralInfo", json.getString("n"), json.getString("e"),
                    json.getString("maxdigits"));
            rKey = json.getString("rkey");
            }
        } catch (ScriptException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(0);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(RSAEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static String parseData(Account acc, String cas) {
        String pass = (acc.getPass().length() == 64 ? acc.getPass() : RSAEncrypt.encrypt(acc.getPass()));
        String data = "email=" + EncodeUtil.encode(acc.getUser()) + "&autoLogin=true&icode="
                + (cas == null ? "" : cas) + "&origURL=http%3A%2F%2Fwww.renren.com%2Fhome&domain=renren.com&"
                + "key_id=1&captcha_type=web_login&password="
                + pass
                + "&rkey=" + rKey;
        return data;
    }


    public static void setGeneralInfo(String nVal, String eVal, int maxDigits) {
        try {
            rsaInv.invokeFunction("setGeneralInfo", nVal, eVal, maxDigits);
        } catch (NoSuchMethodException | ScriptException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static synchronized String encrypt(String password) {
        if(!isEncrypt){
            return password;
        }
        if (password.length() < 64) {
            try {
                String pass = (String) rsaInv.invokeFunction("getKey", password);
                return pass;
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ScriptException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return "";
    }
}
