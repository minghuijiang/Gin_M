package com.mingJiang.util.proxy;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

/**
 * MyProxy extends Proxy, added variable to trace the total time been used, and
 * total time failed, and calculated the fail rate.
 *
 * @author Ming Jiang
 *
 */
public class MyProxy extends Proxy {

    private int seqFail;// continue fail count;
    private int tSubmit;// total number of submit since the last reset
    private int tFail;// total number of submit since the last reset
    private int submit;//number of time been called
    private int fail;//number of time failed.
    private int count;//number of object that bind to this proxy
    private int sC;
    private long tendency;
    private long rT;
    private int rC;
    private ProxyLibrary parent;

 
    public MyProxy(Type type, SocketAddress sa, ProxyLibrary parent) {
        super(type, sa);
        seqFail = 0;
        count = 0;
        submit = 0;
        fail = 0;
        tSubmit = 0;
        tFail = 0;
        sC = 0;
        tendency = 0;
        this.parent = parent;
    }

    public MyProxy(Type type, String val, ProxyLibrary p) {
        this(type, getSA(val), p);
        String[] split = val.split(" ");
        if (split.length > 1) {
            String[] split3 = split[1].split("/");
            setTF(Integer.parseInt(split3[0]));
            setTS(Integer.parseInt(split3[1]));
        }
    }

    private static SocketAddress getSA(String val) {
        //host:port anythingElse
        String[] sp = val.split(" ")[0].split(":");
        return new InetSocketAddress(sp[0], Integer.parseInt(sp[1]));
    }

    /**
     * set the total submit
     */
    public void setTS(int ts) {
        tSubmit = ts;
    }

    /**
     * set the total fail
     */
    public void setTF(int tf) {
        tFail = tf;
    }

    /**
     * return the current fail rate.
     *
     * @return
     */
    public double getFailRate() {

        return submit == 0 ? -0.1 : (double) fail / submit;
    }

    public double getTotalFailRate() {

        return this.tSubmit == 0 ? -0.1 : (double) tFail / tSubmit;
    }
    
    
    public int getTotalFail(){
    	return tFail;
    }
    
    public int getTotalSubmit(){
    	return tSubmit;
    }

    /**
     * return current submit count
     */
    public int getSubmit() {
        return submit;
    }

    /**
     * return current fail count
     */
    public int getFail() {
        return fail;
    }

    /**
     * called when object bind to this proxy, return current proxy object to
     * enable chaining in proxyLibrary.
     */
    public MyProxy get() {
        count++;
        return this;
    }

    /**
     * reset the object binding count
     */
    public void reset() {
        count = 0;
    }

    /**
     * use synchronized to avoid multi-threading problem
     */
    /**
     * called only if exception been caught
     */
    public synchronized boolean fail() {
        submit();
        fail++;
        seqFail++;
        tFail++;

        return isUnrespond();
    }

    /**
     * true if user define critirial indicate the proxy not function properly.
     *
     * @return
     */
    public boolean isUnrespond() {
        return seqFail > 10
                || (fail > 40 && getFailRate() > 0.5);
    }

    /**
     * indicate the success of connection through proxy reset seqFail to 0;
     */
    public synchronized void success() {
        submit();
        seqFail = 0;
    }

    /**
     * called on this.type();
     */
    private synchronized void submit() {
        submit++;
        tSubmit++;
    }

    /**
     * return current information proxyAddress tF/tS TFR: xx.xxx F/S FR: xx.xxx
     * C
     */
    public String toString() {
        return address().toString().substring(1) + " "
                + String.format("%d/%d TFR: %2.3f %d/%d FR: %2.3f %d tendency %d Sc %d  read: %d  rC  %d",
                        tFail, tSubmit, tSubmit == 0 ? 0.899 : (double) tFail / tSubmit, fail, submit, getFailRate(), count, getTendency(), sC, getRT(), rC);
    }

    public synchronized void addTendency(long time) {
        sC++;
        tendency += time;
    }

    public long getTendency() {
        if (sC == 0) {
            return -1L;
        }
        return tendency / sC;
    }

    public synchronized void addRead(long time) {
        rC++;
        rT += time;
    }

    public long getRT() {
        if (rC == 0) {
            return -1L;
        }
        return rT / rC;
    }

    public MyProxy changeProxy(String msg) {
        return parent.changeProxy(this,msg);
    }
}
