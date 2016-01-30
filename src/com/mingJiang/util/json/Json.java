package com.mingJiang.util.json;

import com.mingJiang.data.Location;
import com.mingJiang.util.EncodeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.mingJiang.util.Util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Json extends AbstractJSON {

    private Map<String, Object> map;

    public Json(String json) throws JSONException {
        this(json, "", DEFAULT_DELIMITOR, true);
    }

    public Json(String json, String deli) throws JSONException {
        this(json, "", deli, true);
    }

    public Json(String json, String indent, String deli, boolean doParse) throws JSONException {
        delimit = deli;
        rawData = json;
        if (!(rawData.startsWith("{") && rawData.endsWith("}"))) {
            int st = rawData.indexOf("{");
            int en = rawData.lastIndexOf("}");
        //    System.out.println(st+": "+en);
            if (st < 0 || en < 0) {
                //System.out.println(rawData);
                throw new JSONException("json not complete! \n" + rawData);
            }
            rawData = rawData.substring(st, en+1);
         //   System.out.println(rawData);
        }

        map = new HashMap<>();

        this.indent = indent;
        length = rawData.length();

        if (doParse) {
            initJson();
        }

    }

    @Override
    protected void initJson() throws JSONException {
        if (isParsed) {
            return;
        }
     //  System.out.println("init");
        rawData = rawData.substring(1, length - 1);
        isParsed = true;
        ArrayList<String> jsons = JsonUtil.splitJson(rawData);
        for (String subJson : jsons) {
            String[] sp = subJson.split(delimit, 2);
            if (sp.length != 2) {
                if (delimit.equals(AbstractJSON.DEFAULT_DELIMITOR)) {
                    sp = subJson.split(DEFAULT_DELIMITOR, 2);
                }
                if (sp.length != 2) {
                    return;
                }
            }
            String key = sp[0].trim().replace("\"", "");
            Object val;

            String value = sp[1].trim();
            if (value.startsWith("{")) {
                val = new Json(value, indent + DEFAULT_INDENT, delimit, false);
            } else if (value.startsWith("[")) {
                val = new JsonArray(value, indent + DEFAULT_INDENT, delimit, false);
            } else {
                value =value.replace("\"", "");
                    try {
                        // System.out.println("init: "+ value);
                        val = Integer.parseInt(value);
                    } catch (Exception e) {

                        val = value;
                    
                    } 
            }
        //      System.out.println("ttt   "+val+"   "+val.getClass());
            map.put(key, val);
        }
        rawData = null;//help gc
    }

    public boolean contains(String val){
        return get(val)!=null;
    }
    
    /**
     * get Methods
     */
    public Object get(String val) {
        checkParse();
        return map.get(val);
    }

    public int getInt(String key, int failReturn){
        Object o = get(key);
        if(o !=null)
        try {
            return (Integer)o;
        } catch (Exception e) {
            try{
                double d = Double.parseDouble(o.toString());
                return (int)d;
            }catch(Exception ee){
                
            }
            
        }
        return failReturn;
    }
    
    public String getString(String key, String fail){
        Object o = get(key);
        if(o ==null)
            return fail;
        try {
            return EncodeUtil.decode(o.toString());
        } catch (Exception e) {
            return fail;
        }
    }
    
    public int getInt(String val) {
        Object tmp = get(val);
        try {
            return Integer.parseInt(tmp.toString());
        } catch (Exception e) {
            try{
                double d = Double.parseDouble(tmp.toString());
                return (int)d;
            }catch(Exception ee){
                
            }
         //   System.out.println("search for : "+val+" fail");
            // e.printStackTrace();
        }
        return 0;
    }

    public double getDouble(String val) {
        try {
            return Double.parseDouble(get(val).toString());
        } catch (Exception e) {
            return 0.0;
        }
    }

    public long getLong(String val) {
        try {
            return Long.parseLong(get(val).toString());
        } catch (Exception e) {
            return 0;
        }
    }

    public String getString(String val) {
        Object o = get(val);
        return o == null ? null : EncodeUtil.decode(o.toString());
    }

    public Json getJson(String val) {
        try {
            return (Json) get(val);
        } catch (Exception e) {
//          try {
//            return new Json("{}");
//            } catch (JSONException ex) {
//                Logger.getLogger(Json.class.getName()).log(Level.SEVERE, null, ex);
//            }
        //	System.out.println("in Json --getJson fail "+val+"  ");
        //	e.printStackTrace();
            return null;
        }

    }

    public JsonArray getArray(String val) {
        try {
            return (JsonArray) get(val);
        } catch (Exception e) {
//            try {
//                return new JsonArray("[]");
//            } catch (JSONException ex) {
//                Logger.getLogger(Json.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
        return null;// never happened
    }

    public Object remove(String val) {
        checkParse();
        return map.remove(val);
    }

    public int removeInt(String val) {
        try {
            return Integer.parseInt(remove(val).toString());
        } catch (NumberFormatException e) {
            System.out.println("search for : " + val + " fail");
            e.printStackTrace();
        }
        return 0;
    }

    public double removeDouble(String val) {
        return Double.parseDouble(remove(val).toString());
    }

    public long removeLong(String val) {
        return Long.parseLong(remove(val).toString());
    }

    public String removeString(String val) {
        return EncodeUtil.decode(remove(val).toString());
    }

    public Json removeJson(String val) {
        Object o = null;
        try {
            o = remove(val);
            return (Json) o;
        } catch (Exception e) {
            System.out.println("cast to json fail: key : " + val + " " + o.toString());
            e.printStackTrace();
        }
        return null;
    }

    public JsonArray removeArray(String val) {
        Object o = null;
        try {
            o = remove(val);
            return (JsonArray) o;
        } catch (Exception e) {
            System.out.println("cast to JsonArray fail: key: " + val + " " + o.toString());
            e.printStackTrace();
        }
        return null;
    }

    public int size() {
        checkParse();
        return map.size();
    }

    public Set<String> keyset() {
        checkParse();
        return map.keySet();
    }

    public void setObject(String val, Object obj) {
        checkParse();
        map.put(val, obj);
    }

    @Override
    public String toString() {
        checkParse();
        StringBuilder sb = new StringBuilder(1024);
        for (String key : map.keySet()) {
            Object o = map.get(key);
            sb.append("\"").append(key).append("\":");
            if (o instanceof AbstractJSON) {
                sb.append(o).append(",");
            } else {
                //   System.out.println((o instanceof Integer)+"  "+o +"  "+o.getClass());
                if (o instanceof Number || o.toString().equals("null")) {
                    sb.append(o).append(",");
                } else {
                    sb.append("\"").append(o).append("\",");
                }
            }
        }
        if (sb.length() < 1) {
            return "{}";
        }
        return "{" + sb.substring(0, sb.length() - 1) + "}";
    }

//    public static void main(String[] args) throws JSONException {
//        String j = "{\"sub\":0}";
//        Json jj = new Json(j);
//        System.out.println(jj.toString());
//      //  String val = "1",val2="0";
//        //  Object o = Integer.parseInt(val), o2 = Integer.parseInt(val2);
//        //  System.out.println((o instanceof Number));
//        //  System.out.println((o2 instanceof Number));
//    }

    @Override
    public String toFormatString() {
        checkParse();
        if (length < notFormatThreadHold) {
            return toString();
        }
        StringBuilder sb = new StringBuilder("{\n");
   //     System.out.println(map.size());
        for (String key : map.keySet()) {
            Object obj = map.get(key);
            if (obj instanceof AbstractJSON) {
                sb.append(indent).append(DEFAULT_INDENT).append(key).append("=")
                        .append(((AbstractJSON) obj).toFormatString()).append(",\n");
            } else {
                sb.append(indent).append(DEFAULT_INDENT).append(key).append("=")
                        .append(EncodeUtil.decode(obj.toString())).append(",\n");
            }
        }
        if(sb.length()<notFormatThreadHold){
            return toString();
        }
        return sb.substring(0, sb.length() - 2) + "\n" + indent + "}";
    }
    
    public Map<String, Object> getMap(){
    	checkParse();
    	return map;
    }

}
