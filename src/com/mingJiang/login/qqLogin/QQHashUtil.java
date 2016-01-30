package com.mingJiang.login.qqLogin;

import com.mingJiang.data.Hide;
import com.mingJiang.util.PanelUtil;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.mingJiang.util.account.Account;
import com.mingJiang.util.account.Cookies;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QQHashUtil {

    public static ScriptEngine rsaEngine = new ScriptEngineManager().getEngineByName("JavaScript");
    public static Invocable rsaInv;

    static {
        try {
            rsaEngine.eval(Hide.get(Hide.qq));
        } catch (ScriptException | IOException e) {
           PanelUtil.showInfo("文件出错. b ");

           System.exit(0);
        }
        rsaInv = (Invocable) rsaEngine;
    }
    public static String getSubmitUrl(Account acc, String verify) {
        try {
            if (acc.getPass().length() < 32) {
                acc.setPass((String) rsaInv.invokeFunction("getEncrpt", acc.getUser(), acc.getPass()));
            }
            return (String) rsaInv.invokeFunction("getUrl", acc.getUser(), acc.getPass(), verify);
        } catch (NoSuchMethodException |ScriptException e) {
           PanelUtil.showInfo("文件出错. b");
           System.exit(0);
        }
        return "";
    }

}
