package com.mingJiang.login;

import java.util.ArrayList;
import java.util.List;

import com.mingJiang.gui.DefaultMessagable;
import com.mingJiang.gui.Messagable;
import com.mingJiang.util.account.Account;
import com.mingJiang.util.account.Cookies;

/**
 * General Login procedure.
 *
 * Login.getInstance().login(Account, Messagable) --GetCastCode(Account)
 * <-- programmer need to check if cast code is necessary. if yes, prompt user
 * for cast input-->
 * --show any necessary message or warning using Messagable. --update Account
 * Object after login.
 * <-- the username and password should not be change in anyway. only update
 * cookies-->
 * --return indication int.
 *
 * @author Ming Jiang
 *
 */
public abstract class Login {

    public static final int SUCCESS = 0;
    public static final int PASSWORD_ERROR = 1;
    public static final int SERVER_ERROR = 2;
    public static final int UNKNOWN_ERROR = 3;
    public static final int CAPTCHA_ERROR = 4;
    public static final int FREEZE = 5;

    protected List<String> header;

    public Login() {
        header = new ArrayList<>();
        setHeader();
    }

    public int login(Account acc) {
        if(acc.getUser().length()<1|| acc.getPass().length()<1)
            return PASSWORD_ERROR;
        return login(acc, new DefaultMessagable());
    }

    public abstract int login(Account acc, Messagable msg);

    protected abstract void setHeader();

    public abstract String getCaptcha(Account acc);

    protected abstract int autoLogin(Account acc, Messagable msg);

    public List<String> getHeader() {
        return new ArrayList<>(header);
    }
    
    public List<String> getHeader(Cookies cookie){
        List<String> head = getHeader();
        if(cookie!=null)
            head.add(cookie.getCookie(null));
        return head;
    }
    
    public List<String> getHeader(Account acc){
        if(acc!=null)
            return getHeader(acc.getCookies());
        return getHeader();
    }

}
