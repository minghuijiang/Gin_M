/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mingJiang.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author Ming Jiang
 */
public class FileUtil {
	
	public static List<File> getAllFiles(File dir){
		List<File> files = new ArrayList<>();
		getAllFiles(dir, files);
		return files;
	}
	
	private static void getAllFiles(File dir, List<File> list){
		if(dir.isDirectory()){
			for(File f: dir.listFiles())
				if(f.isDirectory())
					getAllFiles(f,list);
				else {
					list.add(f);
				}
		}else {
			list.add(dir);
		}
	}

    public static byte[] compress(byte[] input) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gos = null;
        try {
            gos = new GZIPOutputStream(baos);
            gos.write(input);
            gos.close();
            byte[] r = baos.toByteArray();
            EncodeUtil.encode(r);
            return r;
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Util.close(baos);
            Util.close(gos);
        }
        return new byte[1];
    }

    public static byte[] compress(String fileName) throws IOException {
        return compress(Files.readAllBytes(getPath(fileName)));
    }

    public static byte[] inputToByte(InputStream input) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            
            int nRead;
            byte[] data = new byte[16384];
            
            while ((nRead = input.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            
            buffer.flush();
            
            return buffer.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

   

    public static String decompress(String file) {
        GZIPInputStream gis = null;
        try {
            byte[] val = Files.readAllBytes(new File(file).toPath());
            EncodeUtil.decode(val);
            ByteArrayInputStream bais = new ByteArrayInputStream(val);
            gis = new GZIPInputStream(bais);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buff = new byte[20480];
            int read;
            while ((read = gis.read(buff)) != -1) {
                baos.write(buff, 0, read);
            }
            gis.close();
            baos.flush();

            return new String(baos.toByteArray());
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            Util.close(gis);
        }
        return null;
    }
    
    public static String decompress(InputStream input) {
        GZIPInputStream gis = null;
        try {
            byte[] val = FileUtil.inputToByte(input);
            EncodeUtil.decode(val);
            ByteArrayInputStream bais = new ByteArrayInputStream(val);
            gis = new GZIPInputStream(bais);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buff = new byte[20480];
            int read;
            while ((read = gis.read(buff)) != -1) {
                baos.write(buff, 0, read);
            }
            gis.close();
            baos.flush();

            return new String(baos.toByteArray());
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            Util.close(gis);
        }
        return null;
    }

    public static Path getPath(String link) {
        return new File(link).toPath();
    }

    public static List<String> readFrom(String link, String encode) throws IOException {
        if (link == null) {
            Util.pop("link null");
            return new ArrayList<>();
        }

        File read = new File(link);
        if (!read.exists()) {
            Util.pop(read.toString() + " file not exist");
            return new ArrayList<>();
        }

        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(read), encode));
            ArrayList<String> data = new ArrayList<String>();
            String line = "";
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#")) {
                    data.add(line);
                }
            }

            return data;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Util.close(br);
        }
        Util.pop(read.toString() + " Unknown error");
        return new ArrayList<>();
    }

    public static List<String> readFrom(String link) throws IOException {
        return readFrom(link, "UTF-8");
    }

    public static byte[] readAllBytes(String link) throws IOException {
        return Files.readAllBytes(getPath(link));
    }

    public static String readToLine(String link) throws IOException {
        StringBuilder sb = new StringBuilder();
    //    System.out.println(link);
        for (String s : readFrom(link)) {
            sb.append(s.trim());
        }
        return sb.toString();
    }

    public static <T extends Object> void writeTo(String file, List<T> data, String encode) throws UnsupportedEncodingException, IOException {

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encode));
        for (int i = 0; i < data.size() - 1; i++) {
            bw.write(data.get(i) + "\n");
        }
        if (data.size() > 0) {
            bw.write(data.get(data.size() - 1).toString());
        }
        bw.close();

    }

    public static void writeObject(String fileName, Object obj){
    	try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
			out.writeObject(obj);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static Object readObject(String fileName){
    	try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))){
    		return in.readObject();
    	}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
    public static <T extends Object> void writeTo(String file, List<T> data) {
        try {
            writeTo(file, data, "UTF-8");
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
//    public static void writeTo(String file, List<String> data) {
//        try {
//            writeTo(file, data, "UTF-8");
//        } catch (IOException ex) {
//            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    public static void writeTo(String file, String data) {
        List<String> tmp = new ArrayList<>();
        tmp.add(data);
        writeTo(file, tmp);
    }

    public static void writeAllBytes(String link, byte[] data) throws IOException {
        Files.write(getPath(link), data);
    }

}
