/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mingJiang.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 *
 * @author Ming Jiang
 */
public class ChaorenDM {

    public static String USERNAME = ""; //QQ超人打码账号
    public static String PASSWORD = ""; //QQ超人打码密码
    public static final String SOFTID = "3965"; //缺省为"0"  软件作者一定要提交软件ID，以保证作者提成
    public static boolean suc = true;
    private static DC dama;

    static {
        try {
            dama = DC.instance;
        } catch (Exception e) {
            System.out.println("exception");
            e.printStackTrace();
        } catch(Error er){
            System.out.println("error");
            er.printStackTrace();
        }
        if (dama != null) {
            try {

                String[] data = FileUtil.readToLine("chaoren.dat").split(":");
                USERNAME = data[0];
                PASSWORD = data[1];
                System.out.println("剩余点数:  " + dama.GetUserInfo(USERNAME, PASSWORD));
            } catch (Exception e) {
                System.out.println("init captcha fail: " + e.getMessage());
                suc = false;
            }
        }else
            suc = false;
    }

    public static void main(String[] args) {

    }
    private static ChaorenDM dama2;

    public static ChaorenDM getInstance() {
        if (dama2 == null) {
            if (suc) {
                dama2 = new ChaorenDM();
//               System.out.println(dama.GetUserInfo(USERNAME, PASSWORD));
            }
        }
        return dama2;
    }

    public String decode(byte[] image) {
        String result = dama.RecByte_A(image, image.length, USERNAME, PASSWORD, SOFTID);
        while (result.startsWith("Error")) {
            System.out.println("出错- - 重试");
            result = dama.RecByte_A(image, image.length, USERNAME, PASSWORD, SOFTID);
        }
        return result;
    }

    public void report(String error) {
    //    String[] sp = error.split("|!|");
    //    if (sp.length == 2) {
            dama.ReportError(USERNAME, error);
    //    }
    }

}
