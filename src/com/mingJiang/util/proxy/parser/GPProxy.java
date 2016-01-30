package com.mingJiang.util.proxy.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mingJiang.util.HttpUtil;
import com.mingJiang.util.proxy.MyProxy;

public class GPProxy extends ProxySite {

    public static String GP = "http://www.getproxy.jp/cn/china";

    public GPProxy() {
        super(GP);
    }

    @Override
    public List<MyProxy> getProxies() {
        Set<MyProxy> proxies = new HashSet<>();
        String html;
        while ((html = grabNextPage()) != null) {

        }
        return null;
    }

    @Override
    protected void nextPage() {
		// TODO Auto-generated method stub

    }

}
