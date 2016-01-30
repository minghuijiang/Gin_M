package com.mingJiang.util.json;

import java.util.ArrayList;
import java.util.List;

import com.mingJiang.util.EncodeUtil;

public class JsonArray extends AbstractJSON {

    private ArrayList<Object> items;
    private String delimit;

    public JsonArray(String json) throws JSONException {
        this(json, "", DEFAULT_DELIMITOR, true);
    }

    public JsonArray(String json, String deli) throws JSONException {
        this(json, "", deli, true);
    }

    public JsonArray(String json, String indent, String deli, boolean doParse) throws JSONException {
        delimit = deli;
        rawData = json;
        if (!(rawData.startsWith("[") && rawData.endsWith("]"))) {
            int st = rawData.indexOf("[");
            int en = rawData.lastIndexOf("]");
            if (st < 0 || en < 0) {
                throw new JSONException("json array not complete! \n" + rawData);
            }
            rawData = rawData.substring(st, en);
        }
        items = new ArrayList<>();
        this.indent = indent;
        length = rawData.length();
        if (doParse) {
            initJson();
        }
    }

    @Override
    public void initJson() throws JSONException {
        if (isParsed) {
            return;
        }
        if (rawData.length() <= 2) {
            isParsed = true;
            return;
        }
        rawData = rawData.substring(1, length - 1);
        isParsed = true;
        ArrayList<String> jsons = JsonUtil.splitJson(rawData);
        for (String subJson : jsons) {
            if (subJson.startsWith("{")) {
                items.add(new Json(subJson, indent + DEFAULT_INDENT, delimit, false));
            } else if (subJson.startsWith("[")) {
                items.add(new JsonArray(subJson, indent + DEFAULT_INDENT, delimit, false));
            } else {
                subJson = subJson.replace("\"", "");
                
                    try {
                        items.add(Integer.parseInt(subJson));
                    } catch (NumberFormatException e) {
                        items.add(subJson);
                    }
                
                

            }
        }
        rawData = null;//help gc
    }

    /**
     * get Methods
     */
    public int getInt(int index) {
        return Integer.parseInt(get(index).toString());
    }

    public double getDouble(int index) {
        return Double.parseDouble((String) get(index));
    }

    public long getLong(int index) {
        return Long.parseLong((String) get(index));
    }

    public String getString(int index) {
        return (String) get(index);
    }

    public Json getJson(int index) {
        return (Json) get(index);
    }

    public JsonArray getArray(int index) {
        return (JsonArray) get(index);
    }

    public Object get(int index) {
        checkParse();
        return items.get(index);
    }

    public List<Object> getItems() {
        checkParse();
        return items;
    }
    
    public int size(){
        checkParse();
    	return items.size();
    }

    @Override
    public String toString() {
        checkParse();
        StringBuilder sb = new StringBuilder();
        for (Object key : items) {
            sb.append(key).append(",");
        }
        String result = "";
        if (sb.length() > 1) {
            result = sb.substring(0, sb.length() - 1);
        }
        return "[" + result + "]";
    }

    @Override
    public String toFormatString() {
        checkParse();
        if (length < notFormatThreadHold) {
            String s = toString();
            return s;
        }
        //	System.out.println(this.getClass()+" "+toString().substring(0,10));
        StringBuilder sb = new StringBuilder("[\n");
        for (Object obj : items) {
            if (obj instanceof AbstractJSON) {
                sb.append(indent).append(DEFAULT_INDENT)
                        .append(((AbstractJSON) obj).toFormatString()).append(",\n");
            } else {
                if(obj instanceof Integer){
                    sb.append(indent).append(DEFAULT_INDENT).append(obj).append(",\n");
                }else
                sb.append(indent).append(DEFAULT_INDENT)
                        .append(EncodeUtil.decode((String) obj)).append(",\n");
            }
        }
        return sb.substring(0, sb.length() - 2) + "\n" + indent + "]";
    }
}
