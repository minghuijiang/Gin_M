/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mingJiang.data;

import com.mingJiang.log.Logging;
import com.mingJiang.util.FileUtil;
import com.mingJiang.util.PanelUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import org.apache.logging.log4j.Logger;


/**
 *
 * @author Ming Jiang
 */
public class Hide {

    static{
        Logging.initLogger();
    }
    
   
    
    public static void hide(String fileName,String outName) throws IOException{
        byte[] data = FileUtil.compress(fileName);
        String outFile ="."+source+outName+suffix;
        if(!new File(source).exists()){
            Files.createDirectories(FileUtil.getPath(source));
        }
        if(new File(outFile).exists())
            if(!PanelUtil.showComfirmation(outFile+"存在， 确定覆盖？"))
                return;
        FileUtil.writeAllBytes(outFile, data);
        
    }
    
    public static String get(String fileName) throws IOException{
        InputStream input = ClassLoader.getSystemClassLoader()
                        .getResourceAsStream(dir+fileName+suffix);
        if(input!=null)
            return FileUtil.decompress(input);
        return "";
    }
     private static final String dir ="b/";
     private static final String suffix =".ci"+"as"+"s";
     private static final String source="/src/"+dir;
    public static final String rr ="b";
    public static final String qq ="a";
    public static void main(String[] args) throws IOException{
       hide("q.js",qq);
       hide("r.js",rr);
    }

}
