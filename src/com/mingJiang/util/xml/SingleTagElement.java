package com.mingJiang.util.xml;

public class SingleTagElement extends TagElement {

    /**
     * represent a single tag
     * <name attri="value" attri2="value2"/>
     *
     * @param data data string
     * @param indent
     */
    public SingleTagElement(String data, String indent) {
        this.indent = indent;

        name = data.substring(1, data.indexOf("/>"));
        value = null;

        size = name.length() + 3;
        if (name.contains(" ")) {
            initAttribute();
        }
    }

    public String toString() {
        return "<" + name + "/>";
    }

    public String toXMLString() {
        return indent + "<" + name + "/>";
    }

}
