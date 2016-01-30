package com.mingJiang.util.xml;

public class CommentElement extends TagElement {

    /**
     * represent a comment tag <![CDATA[<code>you know</code>]]>
     *
     * @param data data string
     * @param indent
     */
    public CommentElement(String data, String indent) {

        name = data.substring(1, data.indexOf("-->") + 2);
        value = null;
        size = name.length() + 2;
        this.indent = indent;
    }

    public String toString() {
        return "<" + name + ">";
    }

    public String toXMLString() {
        return indent + "<" + name + ">";
    }

}
