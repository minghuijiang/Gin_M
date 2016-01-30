package com.mingJiang.util.proxy.tester;

import java.util.ArrayList;

import com.mingJiang.util.HttpUtil;
import com.mingJiang.util.proxy.MyProxy;
import com.mingJiang.util.proxy.ProxyLibrary;
import com.mingJiang.util.threadWorker.Counter;
import com.mingJiang.util.threadWorker.ThreadLocker;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProxyTest {

    public static void main(String[] args) {
        try {
            TestProxy("cooleasy.txt", "http://www.google.com", 5);
        } catch (IOException ex) {
            Logger.getLogger(ProxyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void TestProxy(String proxyData, final String testUrl, final int testNum) throws IOException {
        final ProxyLibrary pl = new ProxyLibrary(proxyData);
        final Object locker = new Object();
        final ThreadLocker lock = new ThreadLocker(40);
        final ArrayList<String> params = new ArrayList<String>();
        final Counter count = new Counter(pl.size());
        for (int i = 0; i < pl.size(); i++) {
            lock.getWorker();
            new Thread() {
                public void run() {

                    MyProxy proxy = pl.getProxy();
                    for (int j = 0; j < testNum; j++) {
                        //	System.out.println(j);
                        HttpUtil.connect(testUrl, null, params, proxy, false);
                    }
                    if (count.isFinish()) {
                        synchronized (locker) {
                            locker.notifyAll();
                        }
                    };
                    lock.releaseWorker();
                }
            }.start();
        }
        synchronized (locker) {
            try {
                locker.wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        for (String s : pl.finalized()) {
            System.out.println(s);
        }

    }
}
