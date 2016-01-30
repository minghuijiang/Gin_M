package com.mingJiang.util.proxy.parser;

import java.util.ArrayList;
import java.util.List;

import com.mingJiang.util.HttpUtil;
import com.mingJiang.util.proxy.MyProxy;

public abstract class ProxySite {

    protected int page;
    protected String nextUrl;

    public ProxySite(String url) {
        nextUrl = "";
        page = 1;
    }

    protected abstract void nextPage();

    protected String grabNextPage() {
        List<String> result = HttpUtil.sendGet(nextUrl, new ArrayList<String>());
        return result == null ? null : result.get(result.size() - 1);
    }

    public abstract List<MyProxy> getProxies();
}
