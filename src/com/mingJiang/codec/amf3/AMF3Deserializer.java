package com.mingJiang.codec.amf3;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.UTFDataFormatException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mingJiang.codec.amf3.object.AMF3Constants;

public class AMF3Deserializer extends DataInputStream implements ObjectInput, AMF3Constants {

    protected final List<String> strings = new ArrayList<String>();
    protected final List<Object> objects = new ArrayList<Object>();

    public static int StingTooLongThreadHold = 100;

    public AMF3Deserializer(InputStream in) {
        super(in);
    }

    public Object readObject() throws IOException {
        return readObject(readAMF3Integer());
    }

    private Object readObject(int type) throws IOException {
        switch (type) {
            case AMF3_UNDEFINED: // 0x00;
            case AMF3_NULL:
                return null;// 0x01;
            case AMF3_BOOLEAN_FALSE:
                return Boolean.FALSE;// 0x02;
            case AMF3_BOOLEAN_TRUE:
                return Boolean.TRUE;// 0x03;
            case AMF3_INTEGER:
                return Integer.valueOf(readAMF3Integer());// 0x04;
            case AMF3_NUMBER:
                return readAMF3Double();// 0x05;
            case AMF3_STRING:
                return readAMF3String();// 0x06;
            case AMF3_ARRAY:
                return readAMF3Array();// 0x09;
            case AMF3_OBJECT:
                return readAMF3Object();// 0x0A;
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    private int readAMF3Integer() throws IOException {
        int result = 0;

        int n = 0;
        int b = readUnsignedByte();
        while ((b & 0x80) != 0 && n < 3) {
            result <<= 7;
            result |= (b & 0x7f);
            b = readUnsignedByte();
            n++;
        }
        if (n < 3) {
            result <<= 7;
            result |= b;
        } else {
            result <<= 8;
            result |= b;
            if ((result & 0x10000000) != 0) {
                result |= 0xe0000000;
            }
        }
        return result;
    }

    private double readAMF3Double() throws IOException {
        double d = readDouble();
        Double result = (Double.isNaN(d) ? null : d);
        return result.doubleValue();
    }

    private String readAMF3String() throws IOException {
        String result = null;
        int type = readAMF3Integer();
        if ((type & 0x01) == 0) // stored string
        {
            result = getStringIndex(type >> 1);
        } else {
            int length = type >> 1;
            if (length > 0) {
                if (length >= StingTooLongThreadHold) {
                    this.skip(length);
                    result = "--SkipLongString";
                } else {
                    byte[] utfBytes = new byte[length];
                    char[] utfChars = new char[length];

                    readFully(utfBytes);

                    int c, c2, c3, iBytes = 0, iChars = 0;
                    while (iBytes < length) {
                        c = utfBytes[iBytes++] & 0xFF;
                        if (c <= 0x7F) {
                            utfChars[iChars++] = (char) c;
                        } else {
                            switch (c >> 4) {
                                case 12:
                                case 13:
                                    c2 = utfBytes[iBytes++];
                                    if ((c2 & 0xC0) != 0x80) {
                                        throw new UTFDataFormatException("Malformed input around byte " + (iBytes - 2));
                                    }
                                    utfChars[iChars++] = (char) (((c & 0x1F) << 6) | (c2 & 0x3F));
                                    break;
                                case 14:
                                    c2 = utfBytes[iBytes++];
                                    c3 = utfBytes[iBytes++];
                                    if (((c2 & 0xC0) != 0x80) || ((c3 & 0xC0) != 0x80)) {
                                        throw new UTFDataFormatException("Malformed input around byte " + (iBytes - 3));
                                    }
                                    utfChars[iChars++] = (char) (((c & 0x0F) << 12) | ((c2 & 0x3F) << 6) | ((c3 & 0x3F) << 0));
                                    break;
                                default:
                                    throw new UTFDataFormatException("Malformed input around byte " + (iBytes - 1));
                            }
                        }
                    }
                    result = new String(utfChars, 0, iChars);
                }
                addString(result);
            } else {
                result = "";
            }
        }
        return result;
    }

    private Object readAMF3Array() throws IOException {
        Object result = null;
        int type = readAMF3Integer();
        if ((type & 0x01) == 0) // stored array.
        {
            result = getObjectIndex(type >> 1);
        } else {
            final int size = type >> 1;
            String key = readAMF3String();
            if (key.length() == 0) {
                Object[] objects = new Object[size];
                addObject(objects);
                for (int i = 0; i < size; i++) {
                    objects[i] = readObject();
                }
                result = objects;
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Object readAMF3Object() throws IOException {
        Object result = null;

        int type = readAMF3Integer();
        if ((type & 0x01) == 0) // stored object.
        {
            result = getObjectIndex(type >> 1);
        } else {
            if ((((type >> 1) & 0x01) != 0)) //inlineClassDef
            {
                readAMF3String();//read class name
            }
            result = new HashMap<String, Object>();

            addObject(result);
            // dynamic values...
            String name = readAMF3String();
            while (name.length() != 0) {
                byte vType = readByte();
                Object value = readObject(vType);
                ((HashMap<String, Object>) result).put(name, value);
                name = readAMF3String();
            }
        }
        return result;
    }

    private void addString(String s) {
        strings.add(s);
    }

    private String getStringIndex(int index) {
        return strings.get(index);
    }

    private int addObject(Object o) {
        int index = objects.size();
        objects.add(o);
        return index;
    }

    private Object getObjectIndex(int index) {
        return objects.get(index);
    }

}
