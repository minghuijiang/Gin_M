package com.mingJiang.util.json;

import java.util.ArrayList;

public class JsonUtil {

    public static ArrayList<String> splitJson(String json) {
        ArrayList<String> list = new ArrayList<>();
        int brace = 0,
                bracket = 0,
                lastIndex = 0,
                termi = json.length();
        boolean q = false, d = false, esc = false;;
        for (int i = 0; i < json.length(); i++) {
            char ch = json.charAt(i);
            if (!q && !d) {
                if (ch == '{') {
                    brace++;
                } else if (ch == '[') {
                    bracket++;
                } else if (ch == '}') {
                    if(--brace<0){
                        termi= i;
                        break;
                    }
                } else if (ch == ']') {
                    if(--bracket<0){
                        termi=i;
                        break;
                    }
                }
                if (ch == ',') {
                    if (brace == 0 && bracket == 0) {
                        list.add(json.substring(lastIndex, i).trim());
                        lastIndex = i + 1;
                    }
                }
            }
            if (ch == '"' &&!q) {
                if(!esc)
                    d = !d;
                else{// in strange case. name end with  "xskjd\",
                    if(json.charAt(i+1)==','&&json.charAt(i+2)=='"'){
                        d=!d;
                    }
                }
            } else if (ch == '\'' && !esc&&!d) {
                q = !q;
            }

            if (ch == '\\') {
                esc = true;
            } else {
                esc = false;
            }
        }

        list.add(json.substring(lastIndex,termi));
//        for(String s: list)
//            System.out.println("\t"+s);
        return list;
    }
}
