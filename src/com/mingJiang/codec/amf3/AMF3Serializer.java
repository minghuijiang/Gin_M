package com.mingJiang.codec.amf3;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mingJiang.codec.amf3.object.AMF3Constants;

public class AMF3Serializer extends DataOutputStream implements ObjectOutput, AMF3Constants {

    protected final List<String> strings = new ArrayList<String>();
    protected final List<Object> objects = new ArrayList<Object>();
    protected final List<String> classes = new ArrayList<String>();

    public AMF3Serializer(OutputStream out) {
        super(out);
    }

    public void writeObject(Object o) throws IOException {
        if (o == null) {
            write(AMF3_NULL);
        } else {
            if (o instanceof String || o instanceof Character) {
                writeAMF3String(o.toString());
            } else if (o instanceof Boolean) {
                write(((Boolean) o).booleanValue() ? AMF3_BOOLEAN_TRUE : AMF3_BOOLEAN_FALSE);
            } else if (o instanceof Number) {
                if (o instanceof Integer || o instanceof Short || o instanceof Byte) {
                    writeAMF3Integer(((Number) o).intValue());
                } else {
                    writeAMF3Number(((Number) o).doubleValue());
                }
            } else if (o.getClass().isArray()) {
                writeAMF3Array(o);
            } else {
                writeAMF3Object(o);
            }
        }
    }

    protected void writeAMF3Integer(int i) throws IOException {

        if (i < AMF3_INTEGER_MIN || i > AMF3_INTEGER_MAX) {
            writeAMF3Number(i);
        } else {
            write(AMF3_INTEGER);
            writeAMF3IntegerData(i);
        }
    }

    protected void writeAMF3IntegerData(int i) throws IOException {
        if (i < 0 || i >= 0x200000) {
            write(((i >> 22) & 0x7F) | 0x80);
            write(((i >> 15) & 0x7F) | 0x80);
            write(((i >> 8) & 0x7F) | 0x80);
            write(i & 0xFF);
        } else {
            if (i >= 0x4000) {
                write(((i >> 14) & 0x7F) | 0x80);
            }
            if (i >= 0x80) {
                write(((i >> 7) & 0x7F) | 0x80);
            }
            write(i & 0x7F);
        }
    }

    protected void writeAMF3Number(double d) throws IOException {
        write(AMF3_NUMBER);
        writeDouble(d);
    }

    protected void writeAMF3String(String s) throws IOException {
        write(AMF3_STRING);
        writeAMF3StringData(s);
    }

    protected void writeAMF3StringData(String s) throws IOException {
        if (s.length() == 0) {
            write(0x01);
            return;
        }
        int index = getStringIndex(s);

        if (index >= 0) {
            writeAMF3IntegerData(index << 1);
        } else {
            addString(s);

            final int sLength = s.length();

            // Compute and write modified UTF-8 string length.
            int uLength = 0;
            for (int i = 0; i < sLength; i++) {
                int c = s.charAt(i);
                if ((c >= 0x0001) && (c <= 0x007F)) {
                    uLength++;
                } else if (c > 0x07FF) {
                    uLength += 3;
                } else {
                    uLength += 2;
                }
            }
            writeAMF3IntegerData((uLength << 1) | 0x01);

            // Write modified UTF-8 bytes.
            for (int i = 0; i < sLength; i++) {
                int c = s.charAt(i);
                if ((c >= 0x0001) && (c <= 0x007F)) {
                    write(c);
                } else if (c > 0x07FF) {
                    write(0xE0 | ((c >> 12) & 0x0F));
                    write(0x80 | ((c >> 6) & 0x3F));
                    write(0x80 | ((c >> 0) & 0x3F));
                } else {
                    write(0xC0 | ((c >> 6) & 0x1F));
                    write(0x80 | ((c >> 0) & 0x3F));
                }
            }
        }
    }

    protected void writeAMF3Array(Object array) throws IOException {
        write(AMF3_ARRAY);

        int index = getObjectIndex(array);
        if (index >= 0) {
            writeAMF3IntegerData(index << 1);
        } else {
            addObject(array);

            int length = Array.getLength(array);
            writeAMF3IntegerData(length << 1 | 0x01);
            write(0x01);
            for (int i = 0; i < length; i++) {
                writeObject(Array.get(array, i));
            }
        }
    }

    protected void writeAMF3Object(Object o) throws IOException {
        write(AMF3_OBJECT);

        int index = getObjectIndex(o);
        if (index >= 0) {
            writeAMF3IntegerData(index << 1);
        } else {
            addObject(o);
            String clazz = o.getClass().getName();
            int index2 = getClassIndex(clazz);

            // write class description.
            if (index2 > 0) {
                writeAMF3IntegerData(index2 << 2 | 0x01);
            } else {
                addClass(clazz);
                writeAMF3IntegerData((0 << 4) | (0x02 << 2) | 0x03);
                writeAMF3StringData(clazz);
            }
            // write object content.
            Map<?, ?> oMap = (Map<?, ?>) o;
            for (Map.Entry<?, ?> entry : oMap.entrySet()) {
                Object key = entry.getKey();
                if (key != null) {
                    String propertyName = key.toString();
                    if (propertyName.length() > 0) {
                        writeAMF3StringData(propertyName);
                        writeObject(entry.getValue());
                    }
                }
            }
            writeAMF3StringData("");
        }
    }

    protected void addString(String s) {
        if (!strings.contains(s)) {
            strings.add(s);
        }
    }

    protected int getStringIndex(String s) {
        return strings.indexOf(s);
    }

    protected void addObject(Object o) {
        if (o != null && !objects.contains(o)) {
            objects.add(o);
        }
    }

    protected int getObjectIndex(Object o) {
        return objects.indexOf(o);
    }

    protected void addClass(String clazz) {
        classes.add(clazz);
    }

    protected int getClassIndex(String clazz) {
        return classes.indexOf(clazz);
    }
}
