/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mingJiang.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.mingJiang.util.account.Cookies;

import cn.smy.dama2.Captcha;
import cn.smy.dama2.Dama2;

/**
 *
 * @author Ming Jiang
 */
public class CaptchaUtil {

	public static final String DAMA2 = "dama2";
	public static final String CHAOREN = "chaoren";
	
	public static String choice =DAMA2;
	public static Captcha dama= new Captcha();
	public static ChaorenDM cap;
	public static Map<String,String> record = new HashMap<>();
    static {
    	if(choice.equals(DAMA2)){
            dama.start();
    	}else{
    		cap = ChaorenDM.getInstance();
    	}
    }
    public static void main(String[] args) throws UnknownHostException, IOException{
    	System.out.println(dama.decodeBuf2("http://captcha.qq.com/getimage?uin=1230&aid=549000912&0.280274863820523"));
    //	for(int i=101;i<=240;i++)
    	//	System.out.println("leeann"+i+":share57:安安"+i);
//    	Socket s = new Socket(new Proxy(Type.SOCKS,new InetSocketAddress("110.77.142.241",8080)));
//    	System.out.println("connect proxy");
//    	s.connect(new InetSocketAddress("server1.dama2.com",8909));
//    	System.out.println("connect server");
    }

    private static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }

    public static String getCaptcha(byte[] buff){
    	String captcha;
    	if(cap==null){
    		if(Dama2.suc){
    			long[] seed = new long[1];
    			captcha=dama.getBufCaptcha(buff,seed);
    			String[] res =captcha.split("\\|!\\|");
    			captcha = res[0];
    			record.put(res[0], res[1]);
    		}else{
    			captcha = showCaptcha(buff);
    		}
    	}else{
    		
    		captcha= getCaptchaCR(buff);
    	}
    	return captcha;
    }
    
    public static synchronized String getCaptchaCR(byte[] buff){
        String captcha = cap.decode(buff);
        System.out.println("captcha:  "+captcha);
        String[] cps = captcha.split("\\|!\\|");
        String rawCaptcha = cps[0];
        record.put(cps[0], cps[1]);
        int count=0;
        while(rawCaptcha.length()!=CAPTCHA_LENGTH&&count++<5){
        	errorCaptcha(cps[0]);
        	 captcha = cap.decode(buff);
             System.out.println("captcha:  "+captcha);
             cps = captcha.split("\\|!\\|");
             rawCaptcha = cps[0];
        }
        return rawCaptcha;
    }
    
    public static int CAPTCHA_LENGTH = 4;
    public static void errorCaptcha(String captcha){
    	
    	try{
	    	String id = record.get(captcha);
	    	System.out.println("report: "+id);
	    	if(cap!=null){
	    		cap.report(id);
	    	}else if(Dama2.suc){
	    		System.out.println("dama report: "+id);
	    		dama.reportResult(Long.parseLong(id), false);
	    	}
    	}catch(Exception e){
    		System.out.println("report error");
    		e.printStackTrace();
    	}
    }
    public static String getCaptcha(String url){
    	return getCaptcha(CaptchaUtil.urlToByte(url));
    }
    
    
    public static String getCaptcha(String url,Cookies cookies){
    	String captcha="";
    	if(cap==null){
    		if(Dama2.suc){
    			//long[] seed = new long[1];
    			long ID = dama.decode(url);
    			captcha=dama.getResult(ID,cookies);
    			String[] res =captcha.split("\\|!\\|");
    			captcha = res[0];
    			record.put(res[0], res[1]);
    		}
    	}else{
    		
    	//	captcha= getCaptchaCR(buff);
    	}
    	return captcha;
     //   return getCaptcha(urlToByte(url));
    }
    
    
    private static synchronized String showCaptcha(String image) {
        try {
            ImageIcon castImage =new ImageIcon(new URL(image));
            String cast = (String)JOptionPane.showInputDialog(null, null, "\u9a8c\u8bc1\u7801", 1,castImage,
                    null, null);
            return cast;
        } catch (MalformedURLException ex) {
            Logger.getLogger(CaptchaUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static synchronized String showCaptcha(Image image) {
        return (String) JOptionPane.showInputDialog(null, null, "\u9a8c\u8bc1\u7801", 1, new ImageIcon(image), null, null);
    }

    private static synchronized String showCaptcha(byte[] image) {
       // return cap.getBufCast(image);
        return (String) JOptionPane.showInputDialog(null, null, "\u9a8c\u8bc1\u7801", 1, new ImageIcon(image), null, null);
    }


    
    public static void show(){
       // System.out.println(CaptchaUtil.getCaptcha("http://captcha.qq.com/getimage?aid=549000912&r="+Math.random()+"&uin=123213"));
    }
    
    public static byte[] urlToByte(String url) {
        ByteArrayOutputStream bais = new ByteArrayOutputStream();
        InputStream is = null;
        try {
        	boolean suc = false;
        	while(!suc)
        		try{
        			is = new URL(url).openStream();
        			suc = true;
        		}catch(Exception e){
        			suc = false;
        		}
            byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
            int n;

            while ((n = is.read(byteChunk)) > 0) {
                bais.write(byteChunk, 0, n);
            }
        } catch (IOException e) {
            System.err.printf("Failed while reading bytes from %s: %s", url, e.getMessage());
            e.printStackTrace();
            // Perform any other exception handling that's appropriate.
        } finally {
            Util.close(is);
        }
        return bais.toByteArray();
    }

}
