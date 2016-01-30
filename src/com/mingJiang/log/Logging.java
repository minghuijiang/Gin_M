package com.mingJiang.log;

import com.mingJiang.util.Util;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;

public class Logging extends PrintStream {

    static {
        File file = new File("log/");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Util.pop("创建文件夹 " + file.getAbsolutePath() + " 失败");
                System.exit(1);
            }
        }
    }

    private static Logging log;

    public static void initLogger() {
        if (log == null) {
            try {
                log = new Logging();
            } catch (FileNotFoundException ex) {
                java.util.logging.Logger.getLogger(Logging.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }
    private final PrintStream sysPS;

    private Logging() throws FileNotFoundException {
        super(new FileOutputStream("log/log-" + System.currentTimeMillis() + ".log", true));
        super.println();
        sysPS = System.out;
        super.println("=================" + getTime() + "===================");
        setSystem();
    }

    public static void main(String[] args){
        initLogger();
    }
    private void setSystem() {
        System.setOut(this);
        System.setErr(this);
    }

    private void printTime() {
    //    print(System.currentTimeMillis() + "\t:\t");
    }

    public static String getTime() {
        Calendar c = Calendar.getInstance();
        return c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) + "-"
                + c.get(Calendar.DATE) + " "
                + c.get(Calendar.HOUR) + ":"
                + c.get(Calendar.MINUTE) + ":"
                + c.get(Calendar.SECOND) + ":"
                + c.get(Calendar.MILLISECOND);
    }
//
//    @Override
//    public void flush() {
//        sysPS.flush();
//        super.flush();
//    }
//
//    @Override
//    public void close() {
//        sysPS.close();
//        super.close();
//    }
//
//    @Override
//    public boolean checkError() {
//        return sysPS.checkError() || super.checkError();
//    }
//
//    @Override
//    public void write(int b) {
//        sysPS.write(b);
//        super.write(b);
//    }
//
//    @Override
//    public void write(byte buf[], int off, int len) {
//        sysPS.write(buf, off, len);
//        super.write(buf, off, len);
//    }

//    @Override
//    public void print(boolean b) {
//        sysPS.print(b);
//        super.print(b);
//    }
//
//    @Override
//    public void print(char c) {
//        sysPS.print(c);
//        super.print(c);
//    }
//
//    @Override
//    public void print(int i) {
//        sysPS.print(i);
//        super.print(i);
//    }
//
//    @Override
//    public void print(long l) {
//        sysPS.print(l);
//        super.print(l);
//    }
//
//    @Override
//    public void print(float f) {
//        sysPS.print(f);
//        super.print(f);
//    }
//
//    @Override
//    public void print(double d) {
//        sysPS.print(d);
//        super.print(d);
//    }
//
//    @Override
//    public void print(char s[]) {
//        sysPS.print(s);
//        super.print(s);
//    }
//
//    @Override
//    public void print(String s) {
//        sysPS.print(s);
//        super.print(s);
//    }
//
//    @Override
//    public void print(Object obj) {
//        sysPS.print(obj);
//        super.print(obj);
//    }

    @Override
    public void println() {
        sysPS.println();
        printTime();
        super.println();
    }

    @Override
    public void println(boolean x) {
        sysPS.println(x);
        printTime();
        super.println(x);
    }

    @Override
    public void println(char x) {
        sysPS.println(x);
        printTime();
        super.println(x);
    }

    @Override
    public void println(int x) {
        sysPS.println(x);
        printTime();
        super.println(x);
    }

    @Override
    public void println(long x) {
        sysPS.println(x);
        printTime();
        super.println(x);
    }

    @Override
    public void println(float x) {
          sysPS.println(x);
        printTime();
        super.println(x);
    }

    @Override
    public void println(double x) {
        sysPS.println(x);
        printTime();
        super.println(x);
    }

    @Override
    public void println(char x[]) {
        sysPS.println(x);
        printTime();
        super.println(x);
    }

    @Override
    public void println(String x) {
        sysPS.println(x);
        printTime();
        super.println(x);
    }

    @Override
    public void println(Object x) {
        sysPS.println(x);
        printTime();
        super.println(x);
    }
//
//    @Override
//    public PrintStream printf(String format, Object... args) {
//        String s = String.format(format, args);
//        sysPS.println(s);
//        printTime();
//        super.println(s);
//        return this;
//    }
//
//    @Override
//    public PrintStream printf(Locale l, String format, Object... args) {
//        String s = String.format(l, format, args);
//        sysPS.println(s);
//        printTime();
//        super.println(s);
//        return this;
//    }
//
//    @Override
//    public PrintStream format(String format, Object... args) {
//        String s = String.format(format, args);
//        sysPS.println(s);
//        printTime();
//        super.println(s);
//        return this;
//    }
//
//    @Override
//    public PrintStream format(Locale l, String format, Object... args) {
//        String s = String.format(l, format, args);
//        sysPS.println(s);
//        printTime();
//        super.println(s);
//        return this;
//    }

}
