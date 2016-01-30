package com.mingJiang.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

import com.mingJiang.util.Util;
import com.mingJiang.util.account.Cookies;
import com.mingJiang.util.proxy.MyProxy;
import com.mingJiang.util.proxy.ProxyLibrary;

public class HttpUtil {

    /**
     * start connection auto select Http and Https by url protocol
     *
     * @param urlStr destination url
     * @param data data need be send, null if use HTTPGET
     * @param list set the connection's request header
     * @param cookies set the cookies if need
     * @return
     */
	public static List<String> connect(String urlStr, String data,
            List<String> list, Proxy p, boolean retryWhenFail) {
		return connect(urlStr, data, list, p, retryWhenFail, true);
	}
	
    public static List<String> connect(String urlStr, String data,
            List<String> list, Proxy p, boolean retryWhenFail,boolean autoRedirect) {
        if (urlStr == null) {
            System.out.println("null url");
            return null;
        }
        BufferedWriter bw = null;
        BufferedReader br = null;
        HttpURLConnection conn = null;
	//	Util.print(list);
        //results will contain set-cookies: locations: httpPage in order,  
        // set-cookies will be on seperate line,
        // whole httpPage will be on the same line.
        List<String> results = new ArrayList<>();
        try {
            //initial http connection
            URL url = new URL(urlStr);
            String protocol = url.getProtocol();
            //determine the http or https been use
            if (protocol.equalsIgnoreCase("Https")) {
                //		System.out.println("https");
                if (p != null) {
                    conn = (HttpsURLConnection) url.openConnection(p);
                } else {
                    conn = (HttpsURLConnection) url.openConnection();
                }
            } else if (protocol.equalsIgnoreCase("http")) {
                if (p != null) {
                    conn = (HttpURLConnection) url.openConnection(p);
                } else {
                    conn = (HttpURLConnection) url.openConnection();
                }
            } else {
                // ftp and other not support
                Util.debug(url + "  is not http or https, return ");
                return null;
            }
            conn.setInstanceFollowRedirects(autoRedirect);

            /*		if(conn instanceof HttpsURLConnection)
             conn.setChunkedStreamingMode(64);*/
			//if data!=null and data.length >0 do with post.
            //else do GET
            //buffer reader may not be required.
            if (data != null && data.length() > 0) {
                Util.debug("post");
               // System.out.println("post " + data.length());
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                String s = "Content-Length: " + data.length();
                if (!list.contains(s)) {
                    Util.debug("not contain " + s + "\n" + data);
                    list.add(s);
                }

            }
			//set the header and fileds
            try{
            setHeader(conn, list);
            }catch(Exception e){
            	e.printStackTrace();
            	System.exit(0);
            }
            /*			System.out.println(url+" \n"+data);
             Map<String,List<String>> head = conn.getRequestProperties();
             for(String s: head.keySet()){
             System.out.print(s+": ");
             for(String d: head.get(s))
             System.out.println(d);
             }*/
            //send data to sever
            if (conn.getDoOutput() && data != null) {
                bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                bw.write(data);
                bw.flush();
            }
            String encoding = conn.getContentEncoding();
            if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
                br = new BufferedReader(new InputStreamReader(
                        new GZIPInputStream(conn.getInputStream(), 512), "UTF8"));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF8"));
            }

            //begin retrive the header info from server, 
            Map<String, List<String>> map = conn.getHeaderFields();
            if (map.get("Set-Cookie") != null) {
                for (String line : map.get("Set-Cookie")) {
                    results.add("Set-Cookie" + ": " + line);
                }
            }
            if (map.get("Location") != null) {
                results.add("Location: " + map.get("Location"));
            }
            //get http page data
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            if(sb.length()>0)
            results.add(sb.toString());
            if (p instanceof MyProxy) {
                ((MyProxy) p).success();
            }
            return results;
        } catch (UnknownHostException e) {
            System.out.println("url: " + urlStr);
            e.printStackTrace();
        } catch (Exception e) {
        	System.out.println("Error: "+e.getMessage()+"\nurl: " + urlStr+"\n"+data);
        	e.printStackTrace();
            if (p == null) {
           //     System.out.println(e.getMessage()+" "+p+"\n"+urlStr);
                Util.debug(e.getMessage() + " no proxy" + "\n" + urlStr);
            } else {
                //	System.out.println(e.getMessage()+" "+p+"\n"+urlStr);
                if (p instanceof MyProxy) {
                    ((MyProxy) p).fail();
                }
            }
            //return null;
            Util.debug(data);
            if (retryWhenFail) {
                return connect(urlStr, data, list, p, retryWhenFail,autoRedirect);
            }
        } finally {
            Util.close(br);
            Util.close(bw);
        }
        return null;
    }

    public static byte[] readImage(String urlStr, String data,
            List<String> properties, Cookies cookie, Proxy p) {
        return readImage(urlStr, data, properties, cookie, p, true);
    }

//    public static ProxyLibrary pl;
//    public static MyProxy global;
//    static{
//    	try {
//			pl= new ProxyLibrary("proxy.txt");
//			global = pl.getProxy();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    }
//    public static synchronized void change(){
//    	global = pl.getProxy();
//    }
//    
    
    public static byte[] readImage(String urlStr, String data,
            List<String> properties, Cookies cookie, Proxy p, boolean retry) {
    //	if(p ==null)
    //		p = global;
        BufferedWriter bw = null;
        BufferedReader br = null;
        HttpURLConnection conn = null;
		//results will contain set-cookies: locations: httpPage in order,  
        // set-cookies will be on seperate line,
        // whole httpPage will be on the same line.

        List<String> results = new ArrayList<>();

        try {
            //initial http connection
            URL url = new URL(urlStr);
            String protocol = url.getProtocol();

            //determine the http or https been use
            if (protocol.equalsIgnoreCase("Https")) {
                if (p != null) {
                    conn = (HttpsURLConnection) url.openConnection(p);
                } else {
                    conn = (HttpsURLConnection) url.openConnection();
                }
            } else if (protocol.equalsIgnoreCase("http")) {
                if (p != null) {
                    conn = (HttpURLConnection) url.openConnection(p);
                } else {
                    conn = (HttpURLConnection) url.openConnection();
                }
            } else {
                // ftp and other not support
                System.out.println(url + "  is not http or https, return ");
                return null;
            }

		//if data!=null and data.length >0 do with post.
            //else do GET
            //buffer reader may not be required.
            if (data != null && data.length() > 0) {
                conn.setRequestMethod("POST");
            } else {
                conn.setRequestMethod("GET");
            }
            conn.setDoInput(true);
            conn.setDoOutput(true);

            //use chunk mode for https
            if (conn instanceof HttpsURLConnection) {
                conn.setChunkedStreamingMode(64);
            }

            //set the header and fileds
            setHeader(conn, properties);
		//send data to sever
            //	System.out.println(url+"  "+conn.getDoOutput());
            if (conn.getDoOutput() && data != null) {
                bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                bw.write(data);
                bw.flush();
            }

            InputStream is = conn.getInputStream();

            //begin retrive the header info from server, 
            Map<String, List<String>> map = conn.getHeaderFields();
//            for(String s: map.keySet())
//                for(String ss: map.get(s))
//                    System.out.println(s+"\t:\t"+ss);
            if (map.get("Set-Cookie") != null) {
                for (String line : map.get("Set-Cookie")) {
                    results.add("Set-Cookie" + ": " + line);
                }
            }
            if (map.get("Location") != null) {
                results.add("Location: " + map.get("Location"));
            }
            HttpUtil.setCookies(cookie, results);
          //  System.out.println("get image");
           // Util.print(results);
            int leng = is.available();
            try{
                leng= Integer.parseInt(map.get("Content-Length").get(0));
            }catch(Exception e){
                System.out.println(e.toString());
            }
            byte[] b = new byte[leng + 1024];
            is.read(b);

            return b;
        } catch (UnknownHostException e) {
            Util.debug("Unknown Host: " + urlStr);
            e.printStackTrace();
        } catch (IOException e) {
            Util.debug("IOException: " + urlStr);
            System.out.println("IOException: " + e.getMessage());
            System.out.println("urlStr: " + urlStr);
            System.out.println("data: " + data+ " "+p);
			//e.printStackTrace();
            //	return null;
            if (retry) {
 
//            		if(p!=null&&p instanceof MyProxy)
//            			change();
            	
                return readImage(urlStr, data, properties, cookie, p, false);
            } else {
                return null;
            }
        } finally {
            Util.close(br);
            Util.close(bw);
        }
        return null;
    }

    public static byte[] connectGetByte(String urlStr, byte[] data,
            List<String> properties, Proxy p, boolean retryWhenFail) {
        if (urlStr == null) {
            return null;
        }
        BufferedWriter bw = null;
        BufferedReader br = null;
        HttpURLConnection conn = null;
		// results will contain set-cookies: locations: httpPage in order,
        // set-cookies will be on seperate line,
        // whole httpPage will be on the same line.
        try {
            // initial http connection
            URL url = new URL(urlStr);
            String protocol = url.getProtocol();
            // determine the http or https been use
            if (protocol.equalsIgnoreCase("Https")) {
                if (p != null) {
                    conn = (HttpsURLConnection) url.openConnection(p);
                } else {
                    conn = (HttpsURLConnection) url.openConnection();
                }
            } else if (protocol.equalsIgnoreCase("http")) {
                if (p != null) {
                    conn = (HttpURLConnection) url.openConnection(p);
                } else {
                    conn = (HttpURLConnection) url.openConnection();
                }
            } else {
                // ftp and other not support
                Util.debug(url + "  is not http or https, return ");
                return null;
            }
            conn.setInstanceFollowRedirects(false);
		//	conn.setChunkedStreamingMode(0);
            // if data!=null and data.length >0 do with post.
            // else do GET
            // buffer reader may not be required.
            if (data != null && data.length > 0) {
                Util.debug("post");
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                String s = "Content-Length: " + data.length;
                if (!properties.contains(s)) {
                    Util.debug("not contain");
                    properties.add(s);
                }

            }
            conn.setDoInput(true);
            conn.setDoOutput(true);
            // set the header and fileds

            setHeader(conn, properties);

            // send data to sever
            if (conn.getDoOutput() && data != null) {
                OutputStream os = conn.getOutputStream();
                os.write(data);
                os.flush();
            }
            InputStream openStream = conn.getInputStream();
            /*			Map<String, List<String>> fields =conn.getHeaderFields();
             for(String key: fields.keySet()){
             System.out.println("key: "+key);
             for(String val: conn.getHeaderFields().get(key))
             System.out.println(val);
             }
             Util.debug("content length: "+openStream.available());*/
            byte[] binaryData = new byte[openStream.available()];
            openStream.read(binaryData);

            if (p instanceof MyProxy) {
                ((MyProxy) p).success();
            }
            return binaryData;
        } catch (UnknownHostException e) {
            Util.debug("url: " + urlStr);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            if (p == null) {
                Util.debug(e.getMessage() + " no proxy" + "\n" + urlStr);
            } else {
                // System.out.println(e.getMessage()+" "+p+"\n"+urlStr);
                if (p instanceof MyProxy) {
                    ((MyProxy) p).fail();
                }
            }
            // return null;
            if (retryWhenFail) {
                return connectGetByte(urlStr, data, properties, p, retryWhenFail);
            }
        } finally {
            Util.close(br);
            Util.close(bw);
        }
        return null;
    }

    public static List<String> sendPostOnce(String url, String data,
            List<String> fields, Proxy p) {
        //	System.out.println("post "+url);
        return connect(url, data, fields, p, false);
    }

    public static List<String> sendGetOnce(String url,
            List<String> fields, Proxy p) {
        return connect(url, null, fields, p, false);
    }
    
    public static String getWebpage(String url,int failCount){
        List<String> result = connect(url,null,new ArrayList<String>(),null,false);
        while(result ==null&&failCount-->0){
            result = connect(url,null,new ArrayList<String>(),null,false);
        }
        return result==null?"":result.get(result.size()-1);
    }

    public static List<String> sendPost(String url, String data,
            List<String> fields) {
        //	System.out.println("post "+url);
        return connect(url, data, fields, null, true);
    }

    public static byte[] sendPostGetByte(String url, byte[] data,
            List<String> fields) {
        //	System.out.println("post "+url);
        return connectGetByte(url, data, fields, null, true);
    }

    public static List<String> sendPost(String url, String data,
            List<String> fields, Proxy p) {
        //	System.out.println("post "+url);
        return connect(url, data, fields, p, true);
    }
    
    public static String sendPostNoCookie(String url, String data,
            List<String> fields, Proxy p) {
        List<String> result = connect(url, data, fields, p, true);
        return result==null?"":result.get(result.size()-1);
    }
    

    public static List<String> sendGet(String url,
            List<String> list) {
        return connect(url, null, list, null, true);
    }

    public static List<String> sendGet(String url,
            List<String> fields, Proxy p) {
        //	System.out.println("post "+url);
        return connect(url, null, fields, p, true);
    }

    public static List<String> sendGet(String url,
            List<String> fields, Proxy p, boolean retry) {
        return connect(url, null, fields, p, retry);
    }

    /**
     * set header of urlconnection
     *
     * @param conn connection need to be set
     * @param list the header properties store in arraylist, in format field:
     * value;
     */
    public static void setHeader(HttpURLConnection conn, List<String> list) {
        if(list ==null)
            return;
        for (int i=0;i<list.size();i++) {
        	String line = list.get(i);
            //	System.out.println(line);
            String[] sp = line.split(":", 2);
            if (sp.length == 2) {
                conn.setRequestProperty(sp[0].trim(), sp[1].trim());
            }
        }
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(15000);
    }

    /**
     * set cookies with the http respond
     *
     * @param cookies
     * @param result
     */
    public static void setCookies(Cookies cookies, List<String> result) {
        if (result == null || cookies == null) {
            System.out.println("set cookies can not be null.");
            return;
        }
        //	System.out.println("setCookies!!!");
        for (String line : result) {

            if (line.startsWith("Set-Cookie")) {
            	if(line.contains("expires=Thu, 01-Dec-1994 16:00:00"))
            		continue;
                //	System.out.println("in cookie: "+line);
                String[] dd = line.split(":", 2)[1].trim().split(";");
                /*			for(String s: dd)
                 System.out.println("nest- "+s);*/
                String[] sp = dd[0].split("=", 2);
                //System.out.println(sp[0]+"  "+sp[1]);
                if (sp.length == 2) {
                    cookies.addCookies(sp[0], sp[1]);
                }
                if (sp[1].length() < 2) {
                    cookies.cookies.remove(sp[0]);
                }
                /*if(dd[0].contains("CAS_AUTO_LOGIN")&&dd[dd.length-1].startsWith("Expires")){
                 //	System.out.println(dd[dd.length-1]);
                 //Expires=Thu, 28-Nov-13 06:29:24 GMT
                 //		System.out.println("isExpired?："+cookies.isExpired()+"\n"+line);
                 //		System.out.println(cookies.getExpired()+"\n"+System.currentTimeMillis());
                 cookies.setExpire(getTime(dd[dd.length-1].split("=",2)[1]));
                 }*/
            } else if (line.startsWith("Location")) {
                cookies.addCookies("Location", line.replace("Location: [", "").replace("]", ""));
            }
        }
    }

    public static long getTime(String val) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("EEE, d-MMM-yy HH:mm:ss Z");
            return format.parse(val).getTime();
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println(e.getMessage() + "  :  " + val);
            //e.printStackTrace();
        }
        return 0;
    }

    public static void printMsg(Exception e,
            List<String> header,
            List<String> respond,
            String data) {
        e.printStackTrace();
        System.out.println("header: ");
        Util.print(header);
        System.out.println("respond: ");
        Util.print(respond);
        if (data != null) {
            System.out.println("data:\n" + data);
        }

    }
}
