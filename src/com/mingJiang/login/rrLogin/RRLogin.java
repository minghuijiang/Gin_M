package com.mingJiang.login.rrLogin;

import com.mingJiang.util.HttpUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.omg.CORBA.PUBLIC_MEMBER;

import com.mingJiang.gui.Messagable;
import com.mingJiang.login.Login;
import com.mingJiang.util.CaptchaUtil;
import com.mingJiang.util.EncodeUtil;
import com.mingJiang.util.Util;
import com.mingJiang.util.account.Account;
import com.mingJiang.util.account.Cookies;

public class RRLogin extends Login {

    private String loginURL;
    public static String xwyUrl = "http://apps.renren.com/xiawuyu?origin=50115";

    public RRLogin() {
        super();
        Calendar c = Calendar.getInstance();
        loginURL = "http://www.renren.com/ajaxLogin/login?1=1&uniqueTimestamp="
                + c.get(Calendar.YEAR) + c.get(Calendar.MONTH)
                + c.get(Calendar.DATE) + c.get(Calendar.HOUR);
    }

    @Override
    public int login(Account acc, Messagable msg) {
	//	System.out.println("renren login");
        //check autologin
        if (acc.getCookies().getVal("t") != null) {
            int result = autoLogin(acc,msg);
            if (result == SUCCESS) {
                return SUCCESS;
            }else if(result ==FREEZE){
                System.out.println("账号冻结- ----"+ acc.getUser()+"  : "+acc.getPass());
                System.out.println(acc);
                return Login.FREEZE;
                
            }
        }
        acc.setCookies(new Cookies());
		// no autoLogin. regular login
        //	System.out.println("send post");
        String cas = getCaptcha(acc);
        String pass = acc.getPass();
        //	System.out.println(pass.length());
        pass = (pass.length() == 64 ? acc.getPass() : RSAEncrypt.encrypt(acc.getPass()));
        String data = "email=" + EncodeUtil.encode(acc.getUser()) + "&autoLogin=true&icode="
                + (cas == null ? "" : cas) + "&origURL=http%3A%2F%2Fwww.renren.com%2Fhome&domain=renren.com&"
                + "key_id=1&captcha_type=web_login&password="
                + pass + "&rkey=" + RSAEncrypt.rKey;

        List<String> head = getHeader(acc.getCookies());
        	System.out.println(data);
        List<String> result = HttpUtil.sendPost(loginURL, data,
                head);
        String last = result.get(result.size() - 1);
        	Util.print(result);
        if (last.contains("您的用户名和密码不匹配")) {
            return PASSWORD_ERROR;
        }else if(last.contains("您输入的验证码不正")){
        	CaptchaUtil.errorCaptcha(cas);
        	return login(acc,msg);
        }
        HttpUtil.setCookies(acc.getCookies(), result);
        //TODO implement check if login success.
        //acc.setPass();
        return 0;
    }

    @Override
    protected int autoLogin(Account acc, Messagable msg) {
        //autologin
        ArrayList<String> head = new ArrayList<String>(header);
        head.add(acc.getCookies().getCookie(null));
        List<String> result = HttpUtil.connect(
        		"http://www.renren.com/Login.do?rf=r&domain=renren.com&origURL=http%3A%2F%2Fwww.renren.com%2Fhome",null, head
        		,null,true,false
        		);
        while(result==null){
        	System.out.println("Null result in Auto Login: "+acc);
        	result = HttpUtil.connect(
            		"http://www.renren.com/Login.do?rf=r&domain=renren.com&origURL=http%3A%2F%2Fwww.renren.com%2Fhome",null, head
            		,null,true,false
            		);
        }
        //System.out.println(acc.getCookies().getVal("t"));
        acc.getCookies().addCookies("t", "");//hack
        HttpUtil.setCookies(acc.getCookies(), result);
      // Util.print(result,"autologin");
        String last = result.get(result.size() - 1);
       // System.out.println(acc.getCookies().getVal("t"));
        if(acc.getCookies().getVal("t")!=null)
        	return SUCCESS;
        if (last.length() < 100) {
            System.out.println(acc.getUser() + " length <100");
            Util.print(result);
            return Login.FREEZE;
        }
        return UNKNOWN_ERROR;
    }

    @Override
    protected void setHeader() {
        header.add("Host: www.renren.com");
        header.add("Connection: keep-alive");
        header.add("Origin: http://www.renren.com");
        header.add("X-Requested-With: XMLHttpRequest");
        header.add("User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36");
        header.add("Content-Type: application/x-www-form-urlencoded");
        header.add("Accept: */*");
        header.add("Referer: http://www.renren.com/");
        header.add("Accept-Encoding: gzip,deflate,sdch");
        header.add("Accept-Language: en-US,en;q=0.8");
    }

    public static String captchaCheck
            = "http://www.renren.com/ajax/ShowCaptcha";
    public static String imageUrl
            = "http://icode.renren.com/getcode.do?t=web_login&rnd=Math.random()";

    @Override
    public String getCaptcha(Account acc) {

        List<String> head = getHeader(acc.getCookies());
        //head.set(0, "Host: icode.renren.com");
        List<String> result = HttpUtil.sendPost(captchaCheck,
                "email=" + acc.getUser() + "&_rtk=f4687e71", head);
        //System.out.println("getcaptcha: "+result.get(result.size()-1));
        if (result.get(result.size() - 1).equals("0"))// no cast need
        {
            return null;
        }
        //	System.out.println("captcha!!!");
        head.set(0, "Host: icode.renren.com");
        byte[] image = HttpUtil.readImage(imageUrl, null, head, acc.getCookies(), null);
        if (image == null) {
            return null;
        }
        String cas = CaptchaUtil.getCaptcha(image); //TODO impelements cas;
        while (cas == null) {
            System.out.println("cast not enter, retry");
            image = HttpUtil.readImage(imageUrl, null, head, acc.getCookies(), null);
            cas = CaptchaUtil.getCaptcha(image); //TODO impelements cas;
        }
        return cas;
    }
    
    public static String secureLink = "http://safe.renren.com/security/protect/14/12";
    public static String setQ = "http://safe.renren.com/actions/changesafequestions";
    public static String image = "http://safe.renren.com/https/icode.renren.com/getcode.do?rk=800&t=%s&rnd=";
    public void setSecurity(Account acc, String value){
    	 List<String> head = getHeader(acc.getCookies());
         List<String> result = HttpUtil.sendGet(secureLink, head);
         
         String html = result.get(result.size()-1);
         String[] sp= html.split("action_token\" value=\"");
         if(sp.length==2){
        	 String token = sp[1].split("\"",2)[0];
        	 String pageToken = sp[1].split("var page_token = \"",2)[1].split("\"")[0];
        	 String answer = EncodeUtil.encode(value);
        	 System.out.println(answer);
        	 String req = sp[0].split("get_check:\'",2)[0].split("\'")[0];
        	 String rtk = sp[0].split("get_check_x:\'",2)[1].split("\'")[0];
        	 String cap = html.split("name=\"_captcha_type\" value=\"",2)[1].split("\"")[0];
        	 String nowImage =String.format(image, cap)+System.currentTimeMillis();
        	 byte[] image = HttpUtil.readImage(nowImage, null, head, acc.getCookies(), null);
             if (image == null) {
            	 image = HttpUtil.readImage(nowImage, null, head, acc.getCookies(), null);
            	 if(image == null){
            		 System.out.println("Fail image");
            		 return ;
            	 }
             }
             String cas = CaptchaUtil.getCaptcha(image); //TODO impelements cas;

             String data ="action_token="+token+"&password="+acc.getRawPass()+
        			 "&question=42&answer="+answer
        			 + "&question=19&answer="+answer
        			 + "&question=20&answer="+answer
        			 + "&_captcha_type="+cap+"&captcha="+cas
        			 + "&requestToken="+req+"&_rtk="+rtk+"&ajax-type=json&token="+pageToken;
        	 
        	 result = HttpUtil.sendPost(setQ,data, head);
        	 
        	 html = result.get(result.size()-1);
        	 while(html.contains("验证码不正确")){
        		 System.out.println("验证码错误");
        		 image = HttpUtil.readImage(nowImage, null, head, acc.getCookies(), null);
        		 cas = CaptchaUtil.getCaptcha(image); //TODO impelements cas;

        		 data ="action_token="+token+"&password="+acc.getRawPass()+
            			 "&question=42&answer="+answer
            			 + "&question=19&answer="+answer
            			 + "&question=20&answer="+answer
            			 + "&_captcha_type="+cap+"&captcha="+cas
            			 + "&requestToken="+req+"&_rtk="+rtk+"&ajax-type=json&token="+pageToken;
            	 
            	 result = HttpUtil.sendPost(setQ,data, head);
            	 html = result.get(result.size()-1);
        	 }
        	 
        	 cap = html.split("rk=800&t=",2)[1].split("&",2)[0];
        	 nowImage =String.format(RRLogin.image, cap)+System.currentTimeMillis();
        	 image = HttpUtil.readImage(nowImage, null, head, acc.getCookies(), null);
        	 cas = CaptchaUtil.getCaptcha(image); //TODO impelements cas;

    		 data ="action_token="+token+"&password="+acc.getRawPass()+
        			 "&question=42&answer="+answer
        			 + "&question=19&answer="+answer
        			 + "&question=20&answer="+answer
        			 + "&_captcha_type="+cap+"&captcha="+cas
        			 + "&requestToken="+req+"&_rtk="+rtk+"&ajax-type=json&token="+pageToken;
        	 
        	 result = HttpUtil.sendPost(setQ+"/check",data, head);
        	 html = result.get(result.size()-1);
        	 while(html.contains("验证码不正确")){
        		 System.out.println("验证码错误");
        		 image = HttpUtil.readImage(nowImage, null, head, acc.getCookies(), null);
        		 cas = CaptchaUtil.getCaptcha(image); //TODO impelements cas;

        		 data ="action_token="+token+"&password="+acc.getRawPass()+
            			 "&question=42&answer="+answer
            			 + "&question=19&answer="+answer
            			 + "&question=20&answer="+answer
            			 + "&_captcha_type="+cap+"&captcha="+cas
            			 + "&requestToken="+req+"&_rtk="+rtk+"&ajax-type=json&token="+pageToken;
            	 
        		 result = HttpUtil.sendPost(setQ+"/check",data, head);
            	 html = result.get(result.size()-1);
        	 }
         }
         Util.print(result);
    }
   
    

}
