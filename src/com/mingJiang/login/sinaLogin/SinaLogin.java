/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mingJiang.login.sinaLogin;

import com.mingJiang.gui.Messagable;
import com.mingJiang.login.Login;
import com.mingJiang.util.account.Account;

/**
 *
 * @author Ming Jiang
 */
public class SinaLogin extends Login{
    public static String version ="ssologin.js(v1.4.14)";

    @Override
    public int login(Account acc, Messagable msg) {
        if(autoLogin(acc,msg)==Login.SUCCESS){
            return Login.SUCCESS;
        }
        
        
        
        return 1;
    }

    @Override
    protected void setHeader() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCaptcha(Account acc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    
    private static String autoInit= "http://login.sina.com.cn/sso/login.php?client="+version+"&_=";
    @Override
    protected int autoLogin(Account acc, Messagable msg) {
        return Login.UNKNOWN_ERROR;
    }
    
}
