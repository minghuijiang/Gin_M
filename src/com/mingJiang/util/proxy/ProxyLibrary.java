package com.mingJiang.util.proxy;

import com.mingJiang.util.FileUtil;

import java.net.InetSocketAddress;
import java.net.Proxy.Type;
import java.util.ArrayList;

import com.mingJiang.util.Util;

import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

public class ProxyLibrary {

	public static boolean isReset = false;
    //List contain proxies.
    private List<MyProxy> proxies;
    //List store unrespond proxy;
    private List<MyProxy> unRespondProxy;
    // track number.
    private int track;
    
    private String fileName;
    
    private final Object lock = new Object();
	// filename for tmp file, 

    private boolean hide = false;

    /**
     * private constructor
     */
    private ProxyLibrary() {
        proxies = new ArrayList<MyProxy>();
        unRespondProxy = new ArrayList<MyProxy>();
    }

    /**
     *
     * @param fileName
     */
    public ProxyLibrary(String fileName) throws IOException {
        this();
        this.fileName=fileName;
        parseData(FileUtil.readFrom(fileName));
    }

    public ProxyLibrary(String fileName, boolean hideMsg) throws IOException {
        this(fileName);
        hide = hideMsg;
    }

    public ProxyLibrary(List<String> proxyData) {
        this();
        parseData(proxyData);
    }

    private void parseData(List<String> proxyData) {
        if (proxyData == null) {
            throw new IllegalArgumentException("proxy data cannot be null");
        }
        track = 0;
        //host:port F/S
        for (String data : proxyData) {
            try {
                if (data.length() < 5) {
                    continue;
                }
				//host:port
                //F/S
                String[] split = data.trim().split(" ");

				//host
                //port
                String[] split2 = split[0].split(":");

                String host = split2[0];
                int port = Integer.parseInt(split2[1]);

				//F
                //S
                MyProxy tmp = new MyProxy(Type.HTTP, new InetSocketAddress(host, port), this);
                if (split.length > 1) {
                	try{
                    String[] split3 = split[1].split("/");
                    tmp.setTF(Integer.parseInt(split3[0]));
                    tmp.setTS(Integer.parseInt(split3[1]));
                	}catch(Exception e){
                		tmp.setTF(0);
                		tmp.setTS(0);
                	}
                }

                if(isReset){
                	tmp.setTF(0);
                	tmp.setTS(0);
                }
                if (!proxies.contains(tmp)) {
                    proxies.add(tmp);
                } else {
                    if (!hide) {
                        System.out.println("duplicate: " + tmp);
                    }
                }
            } catch (Exception e) {
                if (!hide) {
                    System.out.println(data);
                    e.printStackTrace();
                }
            }
        }
    }

    static int c= 0;
    public MyProxy getProxy() {
        synchronized (lock) {
            if (track >= proxies.size()) {
                track = 0;
//                new Thread(){
//                	public void run(){
//                        JOptionPane.showMessageDialog(null,"reset track number. "+c++);
//
//                	}
//                }.start();
            }

            return proxies.get(track++).get();
        }
    }

    public void remove(MyProxy proxy) {
        synchronized (lock) {
            proxies.remove(proxy);
        }

    }

    public void add(MyProxy proxy) {
        synchronized (lock) {
            proxies.add(proxy);
        }
    }

    public void add(ArrayList<MyProxy> pList) {
        synchronized (lock) {
            proxies.addAll(pList);
        }
    }

    public MyProxy changeProxy(MyProxy failProxy,String msg) {
        synchronized (lock) {
            //if(failProxy.isUnrespond()){
            if (!hide) {
                System.out.println(msg+"  change proxy: " + failProxy);
            }
            // first remove call, remove from list to unrespond list,
            if (proxies.contains(failProxy)) {
                //failProxy.reset();
                if (!hide) {
                    System.out.println(proxies.remove(failProxy));
                } else {
                    proxies.remove(failProxy);
                }
                track--;
                unRespondProxy.add(failProxy);
                //	update();
            }
            if (proxies.size() == 0) {
                System.out.println("=====================null=================");
                return null;
            }
            MyProxy p = getProxy();
            if (!hide) {
                System.out.println(" new Proxy: " + p);
            }
       //     FileUtil.writeTo("tmp.txt", finalized());
            return p;
        }
	//	}
        //	return failProxy;
    }

    /**
     * sort the proxies by fail rate. and return the proxies string.
     *
     * @return
     */
    public synchronized ArrayList<String> finalized() {
        synchronized (lock) {
            SortProxy.sortByFailRate(proxies);

            ArrayList<String> tmp = new ArrayList<String>();
            for (MyProxy p : proxies) {
                tmp.add(p.toString());
                //p.reset();
            }

            SortProxy.sortByTotalFailRate(unRespondProxy);

            tmp.add("");
            tmp.add("");
            if (unRespondProxy.size() > 0) {
                for (MyProxy p : unRespondProxy) {
                	if(p.getTotalFailRate()<0.80){
                		p.setTF(p.getFail());
                		p.setTS(p.getSubmit());
                	}else{
                	//	System.out.println(p+"   "+p.getTotalFailRate());
                	}
                    tmp.add(p.toString());
                }
            }
            return tmp;
        }
    }

    public int size() {
        synchronized (lock) {
            return proxies.size();
        }
    }

    public void update() {
        if (!hide) {
            System.out.println("update");
        }

    }

}
