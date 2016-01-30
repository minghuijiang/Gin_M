package com.mingJiang.util.json;

import com.mingJiang.data.Location;

public abstract class AbstractJSON {
    protected static int notFormatThreadHold = 20;
    public static String DEFAULT_INDENT ="\t";
    public static String DEFAULT_DELIMITOR = ":";
    protected String indent = DEFAULT_INDENT;
    protected int length;
    protected String delimit;
    protected String rawData;
    protected boolean isParsed = false;

    /**
     * initial a json with String
     *
     * @throws JSONException
     */
    protected abstract void initJson() throws JSONException;



    /**
     * return the json in formatted string.
     *
     * @return
     */
    public abstract String toFormatString();

    protected void checkParse() {
        if (!isParsed) {
            try {
                initJson();
            } catch (JSONException e) {
                System.out.println("error in jsons : "+this.rawData);
//            	try{
//            	for(int i=1;i<20;i++)
//            		System.out.println(Location.getCaller(i));
//            	}catch(Exception r){
//            		
//            	}
            }
        }
    }
//    
//    public static void main(String[] args){
//    	try {
//			Json json = new Json("{\"level\":\"14\",");
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    }
}
