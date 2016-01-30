package com.mingJiang.reg;

import com.mingJiang.util.account.Account;
import com.mingJiang.util.account.Cookies;

public abstract class Register {

    protected Account account;
    protected boolean regiSuc;

    public Register(String username, String password) {
        account = new Account(username, password, new Cookies());
    }

}
