package com.mingJiang.util.xml;

import com.mingJiang.data.Pair;
import java.util.ArrayList;
import java.util.List;

public class XMLUtil {

    public static List<Pair<String, String>> splitAttri(String attr) {
        if (!attr.startsWith("<") || !attr.endsWith(">")) {
            System.out.println("unclosed attr :" + attr);
        }
        List<Pair<String, String>> result = new ArrayList<>();
        int start = attr.indexOf(" ");
        boolean dq = false;
        boolean sq = false, isVal = false;
        String at = "", val = "";
        for (int i = start; i < attr.length(); i++) {
            char ch = attr.charAt(i);
            if (!dq && !sq && ch == '=' && !isVal) {
                while (attr.charAt(i + 1) == ' ') {
                    i++;
                }
                at = attr.substring(start, i).trim();
                //System.out.println(at);
                start = i;
            } else {// " ' handle
                char prev = attr.charAt(i - 1);
                if (prev != '\\') {
                    if (ch == '"' && !sq) {
                        dq = !dq;
                        if (dq) {
                            isVal = true;
                            start = i;
                        } else {
                            isVal = false;
                            val = attr.substring(start + 1, i);
                            result.add(new Pair<>(at, val));
                            start = i + 1;
                        }
                    } else if (ch == '\'' && !dq) {
                        sq = !sq;
                        if (sq) {
                            isVal = true;
                            start = i;
                        } else {
                            isVal = false;
                            val = attr.substring(start + 1, i);
                            result.add(new Pair<>(at, val));
                            start = i + 1;
                        }
                    }
                }
            }
        }

        return result;
    }

    public static int getClosingPos(String data, int start){
    	boolean isD = false;
    	boolean isS = false;
    	for(int i=start;i<data.length();i++){
    		char c = data.charAt(i);
//    		System.out.println(c);
    		switch (c) {
			case '\'':
				isS=!isS;
				break;
			case '"':
				isD =!isD;
				break;
			case '>':
//				System.out.println("start: "+start+" i: "+i);
				if(!isD&&!isS)
					return i;
			default:
				break;
			}
    	}
    	return -1;
    }
    
    public static List<String> splitXML(String data) {
//    	System.out.println("start:  "+data);
        List<String> xmls = new ArrayList<String>();
        data =data.trim();
        int index = 0;
        int firstClose = 0;
        try {
            while (index < data.length()) {
//                System.out.println("before index: "+index+"  "+data.substring(index));

                int oldIndex = index;
                index = data.indexOf("<", index);
                if(index >0 &&index-oldIndex>0){
                	xmls.add(data.substring(oldIndex,index));
                }
//                System.out.println("index: "+index+"  old: "+oldIndex);
                if(index==-1){
                    xmls.add(data.substring(oldIndex));
                    break;
                }

                if (data.charAt(index + 1) == '!') { // comment
                    int st = getClosingPos(data,index+3);//data.indexOf("-->", index + 3) + 3;
                    xmls.add(data.substring(index, st));
                    index = st;
                } else {
                    int st = getClosingPos(data, index);//data.indexOf(">", index);
//                    System.out.println("index: "+index+"  st: "+st );
                    if(st==-1){
                        xmls.add(data.substring(index));
                        
                        break;
                    }
                    if (data.charAt(st - 1) == '/') { // single tag element
                        xmls.add(data.substring(index, st + 1));
                        index = st + 1;
                    } else {
                    	try{
	                        String openTag = data.substring(index, st).split(" ")[0];
//	                        System.out.println("open tag:  "+openTag);
	                        String closeTag = "</" + openTag.substring(1) + ">";
	                        int closeIndex  = data.indexOf(closeTag,st);

	                        if(closeIndex<0){ //single not obey.
//	                        	System.out.println("single: "+openTag);
	                        	xmls.add(data.substring(index, st + 1));
	                            index = st + 1;
	                        }else{
//		                        System.out.println("close: "+closeIndex+"  "+data.substring(closeIndex));

	                        	firstClose = closeIndex+ closeTag.length();
		                        int tmp = data.indexOf(openTag, st);
//		                        System.out.println("tmp: "+tmp);
		                        while (tmp < firstClose && tmp > 0) {
		                            char next = data.charAt(tmp + openTag.length());
		                            if ((next == ' ' || next == '>')) {
		                                st =getClosingPos(data, tmp);// tmp+openTag.length();
//		                                System.out.println("st: "+st);
		                                closeIndex  = data.indexOf(closeTag,firstClose);
			                        	firstClose = closeIndex+ closeTag.length();

			                            tmp = data.indexOf(openTag,st);

			                            next = data.charAt(tmp + openTag.length());
		                            }else{
		                                st =getClosingPos(data, tmp);// tmp+openTag.length();
//		                                System.out.println("st: "+st);
			                            tmp = data.indexOf(openTag,st);

		                            }
//		                            st = tmp+openTag.length()+2;
//		                            tmp = data.indexOf(openTag,st);
//			                        System.out.println("st: "+st+" tmp: "+tmp);

		                        }
//		                        while ((nestCount--) > 0) {
//		                            firstClose = data.indexOf(closeTag, firstClose) + closeTag.length();
//		                        	System.out.println("next close: "+nestCount+"  "+firstClose+"   "+closeTag);
//
//		                        }
		                        String d = data.substring(index, firstClose);
//		                        System.out.println("======add:  "+d);
		                        xmls.add(d);
		                        index = firstClose;
	                        }
                    	}catch(Exception e){
                    		 for (String s : xmls) {
                                 System.err.println("String: "+s);
                             }
                             e.printStackTrace();
                             System.err.println("index: "+index);
                             System.err.println("first: "+firstClose);
                             System.err.println(data.substring(index));
                    		System.exit(0);
                    	}
                    }
                }
            }
        } catch (Exception e) {
            for (String s : xmls) {
                System.err.println("String: "+s);
            }
            e.printStackTrace();
            System.err.println("index: "+index);
            System.err.println("first: "+firstClose);
            System.err.println(data.substring(index));
            
        }
        
//        System.out.println("DONE==============================");
//        for (String s : xmls) {
//            System.out.println(s);
//        }
//        System.out.println("FINISH===========================");
//        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        return xmls;
    }

    /**
     * last tag must be the close tag of first tag
     *
     * @param data string of main data
     * @return	String of data enclosed by tag.
     *
     * @throws UnclosedTagException XML data not closed properly
     */
    public static String getNestedVal(String data) throws UnclosedTagException {
        int st = data.indexOf(">");
        int en = data.lastIndexOf("<");
        String tag = data.substring(0, st).split(" ")[0];
//        System.out.println(data);
//        System.out.println(tag);
        if (data.lastIndexOf(tag.replace("<", "</")) != en) {
            // last tag must be the close tag of first tag.
            System.out.println("Unclosed : "+data);
            return "";
        }
//        System.out.println(data.substring(st + 1, en).trim());
        return data.substring(st + 1, en).trim();

    }

}
