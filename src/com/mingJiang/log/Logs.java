package com.mingJiang.log;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Logs{

    static{
        System.setProperty("logFile", "log-"+System.currentTimeMillis()+".log");
        
    }
        
    public static Logger log = getLogger(Logs.class);

    public static void main(String[] args){
        log.entry();
        log.error("fail");
        log.exit();
    }
    
    public static Logger getLogger(Class<?> clazz){
        return LogManager.getLogger(clazz.getName());
    }
}
