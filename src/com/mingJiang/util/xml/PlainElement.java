package com.mingJiang.util.xml;

public class PlainElement extends TagElement{
	 /**
     * represent a comment tag <![CDATA[<code>you know</code>]]>
     *
     * @param data data string
     * @param indent
     */
    public PlainElement(String data, String indent) {

        name = data;
        value = null;
        size = name.length();
        this.indent = indent;
    }

    public String toString() {
        return name;
    }

    public String toXMLString() {
        return indent + name ;
    }

}
