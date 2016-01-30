package com.mingJiang.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EncodeUtil {

    private static final byte e = 0x36;
    private static final byte f = 0x74;
    private static final int add = f - e;

    public static void encode(byte[] raw) {
        split(raw);
        raw[0] = (byte) (raw[0] ^ e ^ ((add ^ -99)));
        for (int i = 1; i < raw.length; i++) {
            int previous = raw[i - 1];
            raw[i] = (byte) (raw[i] ^ e ^ ((i + add) ^ previous));
        }
    }

    public static byte[] decode(byte[] encode) {
        int previous = encode[0];
        encode[0] = (byte) (encode[0] ^ e ^ ((add ^ -99)));
        for (int i = 1; i < encode.length; i++) {
            int tmp = encode[i];
            encode[i] = (byte) (encode[i] ^ e ^ ((i + add) ^ previous));
            previous = tmp;
        }
        split(encode);
        return encode;
    }

    public static void split(byte[] arr) {
        int mid = arr.length / 2 + 1;
        int last = arr.length - 1;
        for (int i = 0; i < mid / 2; i++) {
            byte tmp = arr[mid - i];
            arr[mid - i] = arr[i];
            arr[i] = tmp;
            tmp = arr[mid + 1 + i];
            arr[mid + 1 + i] = arr[last - i];
            arr[last - i] = tmp;
        }
    }

    public static void printBA(byte[] data) {
        for (byte b : data) {
            System.out.print(b + ", ");
        }
        System.out.println();
    }

    public static String decode(String encode) {
        if (encode.contains("\\u")) {
            return deUnicode(encode);
        }
        if (encode.contains("%")) {
            try {
                return deUrl(encode, "utf8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(EncodeUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return encode;
    }

    public static String decode(String encode, String encoding) throws UnsupportedEncodingException {
        if (encode.contains("\\u")) {
            return deUnicode(encode);
        }
        if (encode.contains("%")) {
            return deUrl(encode, encoding);
        }
        return encode;
    }

    public static String deUrl(String url, String encoding) throws UnsupportedEncodingException {
        if (url.contains("%")) {
            String tmp = url;
            try {
                tmp = URLDecoder.decode(url, encoding);
            } catch (IllegalArgumentException ie) {
                System.out.println("illegal ie: "+ie.getMessage()+"  " + url);
                return tmp;
            }
            return deUrl(tmp, encoding);
        }
        return url;
    }

    public static String encode(String val) {
        try {
            return encode(val, "UTF8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(EncodeUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public static String encode(String val, String encode) throws UnsupportedEncodingException {

        return URLEncoder.encode(val, encode);

    }

    public static String deUnicode(String unicode) {
        if (!unicode.contains("\\u")) {
            return unicode;
        }
        unicode = unicode.replace("\\/", "\\");
        String[] sp = unicode.split("[\\\\]");
        String result = "";
        for (String s : sp) {
            if (s.startsWith("u")) {
                s = s.substring(1);
                try {
                    if (s.length() > 4) {
                        result += (char) Integer.parseInt(s.substring(0, 4), 16) + "" + s.substring(4);
                    } else {
                        result += (char) Integer.parseInt(s, 16);
                    }
                } catch (Exception e) {
                    result += s;
                }
            } else {
                result += s;
            }
        }
        return result;
    }
}
