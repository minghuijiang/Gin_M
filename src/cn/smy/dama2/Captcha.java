/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.smy.dama2;

import com.mingJiang.util.FileUtil;
import com.mingJiang.util.PanelUtil;
import com.mingJiang.util.Util;
import com.mingJiang.util.account.Cookies;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ming Jiang
 */
public class Captcha {
    private static final String softwareName = "java调试";
    private static final String softwareKey = "6e12569b9b3265cd453e5f244918d5f8";
    private boolean isInit = false;
    private boolean isLogin = false;
    private Dama2 dama;
    public static String accountFile = "dama.dat";
    private String userName,password;
    public Captcha() {
        dama = new Dama2();
    }

    public int getOrigError() {
        return dama.getOrigError();
    }

    public void start() {
    	try{
	    	String[] data = FileUtil.readToLine(accountFile).split(":");
	    	userName = data[0];
	    	password = data[1];
	    	System.out.println(userName+"  "+password);
    	}catch(Exception e){
    		Dama2.suc=false;
    		return;
    	}
        if (!isInit) {
            int result = dama.init(softwareName, softwareKey);
            System.out.println("init= "+result);
            if (result != Dama2.ERR_CC_SUCCESS) {
                PanelUtil.showInfo("初始化错误: " + result);
            } else {
                isInit = true;
            }
        }
        if (isInit) {
            String[] sys = new String[1], app = new String[1];
            int result = dama.login(userName, password, "", sys, app);
            System.out.println("login result: "+result);
            System.out.println("Sys Anno: " + sys[0]);
            System.out.println("App Anno: " + app[0]);
            if (result != 0) {
                System.out.println("登陆错误: " + result);
            } else {
                isLogin = true;
                isInit= false;
            }
        }
        if(!isLogin)
            Dama2.suc=false;
        else
        	System.out.println("剩余点数:  " + this.queryBalance());
    }

    public void exit() {
        if (isLogin) {
            int result = dama.logoff();
            if (result == 0) {
                isLogin = false;
            } else {
                PanelUtil.showInfo("登出错误 " + result);
            }
        } else {
            System.out.println("用户未登陆");
        }
        if (isInit) {
            int result = dama.uninit();
            if (result == 0) {
                isInit = false;
            } else {
                PanelUtil.showInfo("解除注册错误 " + result);
            }
        } else {
            System.out.println("软件未注册 ");
        }
    }

    public boolean register(String userName, String userPassword, String qq, String telNo, String email, int nDyncVCodeSendMode) {
        int result = dama.register(userName, userPassword, qq, telNo, email, nDyncVCodeSendMode);
        if (result == 0) {
            return true;
        } else {
            PanelUtil.showInfo("注册" + userName + " 失败 " + result);
            return false;
        }
    }

    public boolean register(String userName, String password, String qq, String email) {
        return register(userName, password, qq, "", email, 2);
    }

    public boolean register(String userName, String password, String email) {
        return register(userName, password, "", email);
    }

    public void recharge(String userName, String cardNo, long[] balance) {
        int result = dama.recharge(userName, cardNo, balance);
        if (result == 0) {
            PanelUtil.showInfo("充值 " + userName + " 成功,余额 " + balance[0]);
        } else {
            PanelUtil.showInfo("充值失败 " + userName + "  " + cardNo);
        }
    }

    public long queryBalance() {
        long[] res = new long[1];
        int result = dama.queryBalance(res);
        if (result == 0) {
            return res[0];
        } else {
            PanelUtil.showInfo("查询余额错误. " + result);
            return 0;
        }

    }

    public int readInfo(String[] userName, String[] qq, String[] telNo, String[] email, int[] nDyncVCodeSendMode) {
        return dama.readInfo(userName, qq, telNo, email, nDyncVCodeSendMode);
    }

    public int changeInfo(String oldPassword, String newPassword, String qq, String telNo, String email, String dyncVCode, int nDyncVCodeSendMode) {
        return dama.changeInfo(oldPassword, newPassword, qq, telNo, email, dyncVCode, nDyncVCodeSendMode);
    }

    public long decode(String url, String cookie, String referer, byte vcodeLen, short timeout, long vcodeTypeID, boolean downloadFromLocalMachine) {
        long[] requestID = new long[1];
        int result = dama.decode(url, cookie, referer, vcodeLen, timeout, vcodeTypeID, downloadFromLocalMachine, requestID);
        System.out.println(result + "   " + requestID[0]);
        switch (result) {
            case Dama2.ERR_CC_SUCCESS:
                return requestID[0];
            case Dama2.ERR_CC_TIMEOUT_ERR:
                return decode(url, cookie, referer, vcodeLen, timeout, vcodeTypeID, downloadFromLocalMachine);
            case Dama2.EER_CC_REQUEST_TOO_MUCH:

                try {
                    System.out.println("线程太多错误: ");
                    Thread.sleep((long) (Math.random() * 10000));
                } catch (InterruptedException ex) {
                    Logger.getLogger(Captcha.class.getName()).log(Level.SEVERE, null, ex);
                }
                return decode(url, cookie, referer, vcodeLen, timeout, vcodeTypeID, downloadFromLocalMachine);

            case Dama2.ERR_CC_BALANCE_NOT_ENOUGH:
                PanelUtil.showInfo("余额不足. 余额为" + this.queryBalance());
                return 0;
            case Dama2.ERR_CC_FILE_URL_ERR:
                PanelUtil.showInfo("url 错误: " + url);
                return 0;
            case Dama2.ERR_CC_COOKIE_ERR:
                PanelUtil.showInfo("cookie 错误: " + cookie);
                return 0;
            case Dama2.ERR_CC_REFERER_ERR:
                PanelUtil.showInfo("referer 错误: " + referer);
                return 0;
            case Dama2.ERR_CC_VCODE_LEN_ERR:
                System.out.println("ERR_CC_VCODE_LEN_ERR 错误: ");
                return decode(url, cookie, referer, (byte) 0, timeout, vcodeTypeID, downloadFromLocalMachine);
            case Dama2.ERR_CC_VCODE_TYPE_ID_ERR:
                PanelUtil.showInfo("typeId 错误: " + vcodeTypeID);
                return 0;
            default:
                PanelUtil.showInfo("其他错误 " + result);
                return 0;
        }
    }

    public long decode(String url, String cookie, String referer, byte vcodeLen, short timeout) {
        return decode(url, cookie, referer, vcodeLen, timeout, 7227, false);
    }

    public long decode(String url, String cookie, String referer, byte vcodeLen) {
        return decode(url, cookie, referer, vcodeLen, (short) 60);
    }

    public long decode(String url, String cookie, String referer) {
        return decode(url, cookie, referer, (byte) 0);
    }

    public long decode(String url) {
        return decode(url, "", "");
    }

    public String getResult(long id) {
        String[] res = new String[1];
        long[] v = new long[1];
        String[] c = new String[1];
        int result =0;
        while((result=dama.getResult(id, 60000, res, v, c))==Dama2.ERR_CC_NO_RESULT)
            System.out.println("链接超时 重试");
        System.out.println("result : " + result + "   " + id + "  " + res[0] + "  " + v[0] + "  " + c[0]);
        return res[0]+"|!|"+v[0];
    }

    public String getResult(long id, Cookies cookie) {
        String[] res = new String[1];
        long[] v = new long[1];
        String[] c = new String[1];
        int result =0;
        while((result=dama.getResult(id, 60000, res, v, c))==Dama2.ERR_CC_NO_RESULT)
            System.out.println("链接超时 重试");
        System.out.println("result : " + result + "   " + id + "  " + res[0] + "  " + v[0] + "  " + c[0]);
        if(result ==0)
        	if(cookie!=null)
        		cookie.addCookies("Set-Cookie:"+c[0]);
        return res[0]+"|!|"+v[0];
    }
    public String getCaptcha(String url) {
        return getResult(decode(url));
    }
    
    public String getBufCaptcha(String url){
        return getResult(decodeBuf(url));
    }
     public String getBufCaptcha(byte[] image, long[] seed){
    	 seed[0]=decodeBuf(image);
        return getResult(seed[0]);
    }
     
     public String decodeBuf2(String url) {
         ByteArrayOutputStream bais = new ByteArrayOutputStream();
         InputStream is = null;
         try {
             is = new URL(url).openStream();
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
         return decodeBuf2(bais.toByteArray());
     }

     public String decodeBuf2(byte[] data, String extName, byte vcodeLen) {
         return decodeBuf2(data, extName, vcodeLen, (short) 600, 7227);
     }

     public String decodeBuf2(byte[] data, String extName) {
         return decodeBuf2(data, extName, (byte) 0);
     }

     public String decodeBuf2(byte[] data) {
         return decodeBuf2(data, "jpg");
     }
     
    public String decodeBuf2(byte[] data,String extName, byte vcodeLen, short timeout,long vcodeTypeID){
    	 long[] requestID = new long[1];
         String[] cast=new String[1];
         int result = dama.d2Buf(softwareName, userName, password, data, timeout, vcodeTypeID, cast);
        // int result = dama.decodeBuf(data, extName, vcodeLen, timeout, vcodeTypeID, requestID);
         System.out.println(result + "   " + requestID[0]);
         switch (result) {
             case Dama2.ERR_CC_SUCCESS:
                 return cast[0];
             case Dama2.ERR_CC_TIMEOUT_ERR:
                 return decodeBuf2(data, extName, vcodeLen, timeout, vcodeTypeID);
             case Dama2.EER_CC_REQUEST_TOO_MUCH:

                 try {
                     System.out.println("线程太多错误: ");
                     Thread.sleep((long) (Math.random() * 10000));
                 } catch (InterruptedException ex) {
                     Logger.getLogger(Captcha.class.getName()).log(Level.SEVERE, null, ex);
                 }
                 return decodeBuf2(data, extName, vcodeLen, timeout, vcodeTypeID);

             case Dama2.ERR_CC_BALANCE_NOT_ENOUGH:
                 PanelUtil.showInfo("余额不足. 余额为" + this.queryBalance());
                 return "";

             case Dama2.ERR_CC_VCODE_LEN_ERR:
                 System.out.println("ERR_CC_VCODE_LEN_ERR 错误: ");
                 return decodeBuf2(data, extName, (byte) 0, timeout, vcodeTypeID);
             case Dama2.ERR_CC_VCODE_TYPE_ID_ERR:
                 PanelUtil.showInfo("typeId 错误: " + vcodeTypeID);
                 return "";
             default:
                 PanelUtil.showInfo("其他错误 " + result);
                 return "";
         }
    }
     
    public long decodeBuf(byte[] data, String extName, byte vcodeLen, short timeout, long vcodeTypeID) {
        long[] requestID = new long[1];
        int result = dama.decodeBuf(data, extName, vcodeLen, timeout, vcodeTypeID, requestID);
        System.out.println(result + "   " + requestID[0]);
        switch (result) {
            case Dama2.ERR_CC_SUCCESS:
                return requestID[0];
            case Dama2.ERR_CC_TIMEOUT_ERR:
                return decodeBuf(data, extName, vcodeLen, timeout, vcodeTypeID);
            case Dama2.EER_CC_REQUEST_TOO_MUCH:

                try {
                    System.out.println("线程太多错误: ");
                    Thread.sleep((long) (Math.random() * 10000));
                } catch (InterruptedException ex) {
                    Logger.getLogger(Captcha.class.getName()).log(Level.SEVERE, null, ex);
                }
                return decodeBuf(data, extName, vcodeLen, timeout, vcodeTypeID);

            case Dama2.ERR_CC_BALANCE_NOT_ENOUGH:
                PanelUtil.showInfo("余额不足. 余额为" + this.queryBalance());
                return 0;

            case Dama2.ERR_CC_VCODE_LEN_ERR:
                System.out.println("ERR_CC_VCODE_LEN_ERR 错误: ");
                return decodeBuf(data, extName, (byte) 0, timeout, vcodeTypeID);
            case Dama2.ERR_CC_VCODE_TYPE_ID_ERR:
                PanelUtil.showInfo("typeId 错误: " + vcodeTypeID);
                return 0;
            default:
                PanelUtil.showInfo("其他错误 " + result);
                return 0;
        }
    }

    public long decodeBuf(byte[] data, String extName, byte vcodeLen) {
        return decodeBuf(data, extName, vcodeLen, (short) 60, 7227);
    }

    public long decodeBuf(byte[] data, String extName) {
        return decodeBuf(data, extName, (byte) 0);
    }

    public long decodeBuf(byte[] data) {
        return decodeBuf(data, "jpg");
    }

    public long decodeBuf(String url) {
        ByteArrayOutputStream bais = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            is = new URL(url).openStream();
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
        return decodeBuf(bais.toByteArray());
    }

    public int decodeWnd(String wndDef, int x, int y, int cx, int cy, byte vcodeLen, short timeout, long vcodeTypeID, long[] requestID) {
        return dama.decodeWnd(wndDef, x, y, cx, cy, vcodeLen, timeout, vcodeTypeID, requestID);
    }

    public int getResult(long requestID, long waitTimeout, String[] vcode, long[] vcodeID, String[] retCookie) {
        return dama.getResult(requestID, waitTimeout, vcode, vcodeID, retCookie);
    }

    public int reportResult(long vcodeID, boolean correct) {
        return dama.reportResult(vcodeID, correct);
    }

    public int d2Buf(String softwareID, String userName, String userPassword, byte[] data, short timeout, long vcodeTypeID, String[] retVCodeText) {
        return dama.d2Buf(softwareID, userName, userPassword, data, timeout, vcodeTypeID, retVCodeText);
    }

    public int d2File(String softwareID, String userName, String userPassword, String fileName, short timeout, long vcodeTypeID, String[] retVCodeText) {
        return dama.d2File(softwareID, userName, userPassword, fileName, timeout, vcodeTypeID, retVCodeText);
    }

}
