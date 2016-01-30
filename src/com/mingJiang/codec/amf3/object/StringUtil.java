package com.mingJiang.codec.amf3.object;

import java.util.HashMap;
import java.util.Set;

public class StringUtil {

    public static String toString(Object o) {
        return toString(o, -1);
    }

    public static String toString(Object o, int maxItems) {

        if (o == null) {
            return "null";
        }
        if (o instanceof String) {
            return ("\"" + o + "\"");
        }
        if (o instanceof Character || o.getClass() == Character.TYPE) {
            return ("'" + o + "'");
        }
        if (o instanceof Number) {
            if (o instanceof Byte || o instanceof Short || o instanceof Integer || o instanceof Long) {
                return o + "";
            }
            Double d = new Double(((Number) o).toString());
            if (d.longValue() == d) {
                return d.longValue() + "";
            }
            return "" + d;
        }

        if (o.getClass().isArray()) {
            if (maxItems < 0) {
                Object[] a = (Object[]) o;
                int iMax = a.length - 1;
                if (iMax == -1) {
                    return "[]";
                }

                StringBuilder b = new StringBuilder(1024);
                b.append('[');
                for (int i = 0;; i++) {
                    b.append(StringUtil.toString(a[i]));
                    if (i == iMax) {
                        return b.append(']').toString();
                    }
                    b.append(", ");
                }
            }
        } else if (o instanceof HashMap) {

            @SuppressWarnings("unchecked")
            HashMap<String, Object> map = (HashMap<String, Object>) o;
            StringBuilder b = new StringBuilder(2048);
            b.append('{');
            Set<String> keys = map.keySet();
            int iMax = keys.size();
            int i = 1;
            for (String key : keys) {
                b.append(key + "=" + StringUtil.toString(map.get(key)));
                if (i == iMax) {
                    return b.append('}').toString();
                }
                b.append(", ");
                i++;
            }
        }
        return String.valueOf(o);
    }
}
