/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mingJiang.login.sinaLogin;

import com.mingJiang.data.Hide;
import com.mingJiang.login.rrLogin.RSAEncrypt;
import com.mingJiang.util.Base64;
import com.mingJiang.util.EncodeUtil;
import com.mingJiang.util.FileUtil;
import com.mingJiang.util.HttpUtil;
import com.mingJiang.util.Util;
import com.mingJiang.util.account.Account;
import com.mingJiang.util.json.JSONException;
import com.mingJiang.util.json.Json;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author Ming Jiang
 */
public class RSAEncrpt {
    private static List<String> header;
    private static String scriptVersion ="ssologin.js(v1.4.14)";
    private static String preLogin ="http://login.sina.com.cn/sso/prelogin.php?entry=homepage"
            + "&callback=pluginSSOController.preloginCallBack&su=%s&rsakt=mod&client=%s&_=%d";
    private static String key="EB2A38568661887FA180BDDB5CABD5F21C7BFD59C090CB2D245A87AC253062882729293E5506350508E7F9AA3BB77F4333231490F915F6D63C55FE2F08A49B353F444AD3993CACC02DB784ABBB8E42A9B1BBFFFB38BE18D78E87A0E41B9B8F73A928EE0CCEE1F6739884B9777E4FE9E88A1BBE495927AC4A799B3181D6442443";
    private static String exp ="10001";
    
    public static String getPreLoginUrl(Account acc){
        String encode =EncodeUtil.encode(Base64.encodeToString(EncodeUtil.encode(acc.getUser()).getBytes(), false));
        return String.format(preLogin, encode,scriptVersion,System.currentTimeMillis());
    }
    
    private static List<String> getHeader(){
        if(header == null){
            header = new ArrayList<>();
            header.add("Host: login.sina.com.cn");
            header.add("Connection: keep-alive");
            header.add("Accept: */*");
            header.add("User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36");
            header.add("Referer: http://www.sina.com.cn/");
            header.add("Accept-Encoding: gzip,deflate,sdch");
            header.add("Accept-Language: en-US,en;q=0.8");
        }
        return header;
    }
    
    
    public static ScriptEngine rsaEngine = new ScriptEngineManager().getEngineByName("JavaScript");
    public static Invocable rsaInv;
    public static boolean isEncrypt = true;
    static {
        try {
            rsaEngine.eval(FileUtil.readToLine("sina.js"));
            rsaInv = (Invocable) rsaEngine;

        } catch (ScriptException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(RSAEncrpt.class.getName()).log(Level.SEVERE, null, ex);
        } 

    }


//    public static String parseData(Account acc, String cas) {
//        String pass = (acc.getPass().length() == 64 ? acc.getPass() : RSAEncrypt.encrypt(acc.getPass()));
//        String data = "email=" + EncodeUtil.encode(acc.getUser()) + "&autoLogin=true&icode="
//                + (cas == null ? "" : cas) + "&origURL=http%3A%2F%2Fwww.renren.com%2Fhome&domain=renren.com&"
//                + "key_id=1&captcha_type=web_login&password="
//                + pass
//                + "&rkey=" + rKey;
//        return data;
//    }


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
