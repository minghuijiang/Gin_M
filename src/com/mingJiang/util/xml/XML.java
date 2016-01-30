package com.mingJiang.util.xml;

import com.mingJiang.data.Pair;
import com.mingJiang.util.FileUtil;
import java.io.IOException;

import com.mingJiang.util.Util;

public class XML {

    private TagElement tags;
    private String version;
    private String encoding;

    /**
     * Construct a XML object and read file data from the filePath
     *
     * @param filePath the file path
     * @throws IOException file not found, no permission, encoding error, etc.
     * @throws UnclosedTagException: file format error, when tag not fully
     * closed.
     */
    public XML(String filePath) throws IOException {
        readXML(filePath);
    }

    /**
     * read from filePath and construct a TagElement represent the file data
     *
     * @param filePath the file path
     * @throws IOException file not found, no permission, encoding error, etc.
     * @throws UnclosedTagException: file format error, when tag not fully
     * closed.
     *
     */
    public void readXML(String filePath) throws IOException {
        String data = FileUtil.readToLine(filePath);
        int index = 0;
        if (data.startsWith("<?xml")) {
            index = data.indexOf("?>") + 2;
            String xml = data.substring(0, index);
            readHeader(xml);
        } else {
            System.out.println("xml no header. filename=" + filePath
                    + " \ndefault version=1.0\ndefault encoding=UTF-8");
        }
        System.out.println(data.substring(index));
        if(data.length()>0)
        tags = new TagElement(data.substring(index));
        else
            tags = new TagElement();

    }

    private void readHeader(String xml) {
        version = "1.0";
        encoding = "UTF-8";
        for (Pair<String, String> p : XMLUtil.splitAttri(xml)) {
            if (p.getKey().equals("version")) {
                version = p.getObj();
            } else if (p.getKey().equals("encoding")) {
                version = p.getObj();
            }
        }
    }

    private String getHeader() {
        //<?xml version="1.0" encoding="UTF-8"?>
        return "<?xml version=\"" + version + "\" encoding=\"" + encoding + "\"?>";
    }

    public String toString() {
        return getHeader() + tags.toString();
    }

    public String toXMLString() {
        return getHeader() + "\n" + tags.toXMLString();
    }

    /**
     * return the TagElement that represent xml data
     *
     * @return	TagElement represent the data
     */
    public TagElement getTags() {
        return tags;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setTags(TagElement tags) {
        this.tags = tags;
    }

}
