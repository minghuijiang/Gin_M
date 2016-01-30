package com.mingJiang.util.proxy.parser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;

public class Parser {

    public static ArrayList<String> coolEasyTxtParse(ArrayList<String> rawData) {
        ArrayList<String> proxy = new ArrayList<String>();

        for (String line : rawData) {
            String[] sp = line.split("\t");
            try {
                Integer.parseInt(sp[0]);
                proxy.add(sp[1] + ":" + sp[2]);
            } catch (Exception e) {
            }
        }
        return proxy;
    }

    public static ArrayList<String> parse56Proxy(ArrayList<String> rawData) {
        ArrayList<String> proxy = new ArrayList<String>();

        for (String line : rawData) {
            String[] sp = line.split("@")[0].split(":");
            try {
                Integer.parseInt(sp[1]);
                proxy.add(sp[0] + ":" + sp[1]);
            } catch (Exception e) {
            }
        }
        return proxy;
    }

    public static void main(String[] args) throws IOException {
        ArrayList<String> raw = (ArrayList<String>) Files.readAllLines(new File("cooleasy.txt").toPath(), Charset.forName("UTF8"));

        ArrayList<String> done = parse56Proxy(raw);
        for (String line : done) {
            System.out.println(line);
        }

    }
}
