package com.mingJiang.parser;

import com.mingJiang.data.Pair;
import com.mingJiang.util.FileUtil;
import com.mingJiang.util.json.JSONException;
import com.mingJiang.util.json.Json;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JavaScriptParser {

    public static String parse(String input) {
        StringBuilder sb = new StringBuilder((int) (input.length() * 1.5));
        int brace = 0;
        boolean doubleQuote = false;
        boolean singleQuote = false;

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (ch == '{' && !singleQuote && !doubleQuote) {
                brace++;
                sb.append(ch);
                appendNewLine(sb, brace);
            } else if (ch == '}' && !singleQuote && !doubleQuote) {
                brace--;
                appendNewLine(sb, brace).append(ch);
                appendNewLine(sb, brace);
            } else if (ch == '"' && !singleQuote) {
                doubleQuote = !doubleQuote;
                sb.append(ch);
            } else if (ch == '\'' && !doubleQuote) {
                singleQuote = !singleQuote;
                sb.append(ch);
            } else if (ch == ';' && !singleQuote && !doubleQuote) {
                appendNewLine(sb.append(ch), brace);
            } else {
                sb.append(ch);
            }

        }
        return sb.toString();
    }

    public static StringBuilder appendNewLine(StringBuilder sb, int indent) {
        sb.append("\n");
        for (int i = 0; i < indent; i++) {
            sb.append("\t");
        }
        return sb;
    }

    /*	public static String stackParse(String data){
		
     }*/
    public static void main(String[] args) throws IOException, JSONException {
        String name = "C:\\Users\\Ming Jiang\\Desktop\\init3b052e4c9a7.json";
        Json j =new Json(FileUtil.readToLine(name),"=").getJson("itemcfgList");
        System.out.println(j);
        List<Pair<String, Integer>> tmp = new ArrayList<>();
        for(String key: j.keyset()){
            Json jj = j.getJson(key);
            String n = jj.getString("itemname");
            int c =0;
            try{
                for(Object o : jj.getJson("mergecfgInfo").getArray("arrReqcfg").getItems()){
                    Json jjj = (Json)o;
                    if(jjj.getString("type").equals("item")){
                        c = jjj.getInt("num");
                        break;
                    }
                }
            }catch(Exception e){
                
            }
            tmp.add(new Pair<>(n,c));
        }
        
        Collections.sort(tmp, new Comparator<Pair<String,Integer>>(){
            @Override
            public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
                if(o1.getObj()>o2.getObj())
                    return -1;
                if(o1.getObj()<o2.getObj())
                    return +1;
                return 0;
            }
            
        });
        for(Pair<String, Integer> p: tmp){
            System.out.println(p.getObj()+"\t"+p.getKey());
        }
     //   FileUtil.writeTo(name, parse(FileUtil.readFrom(name).get(0)));
    }
}
