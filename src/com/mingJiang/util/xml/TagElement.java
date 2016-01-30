package com.mingJiang.util.xml;

import com.mingJiang.data.Pair;

import java.awt.RenderingHints.Key;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.ParagraphView;

/**
 * every tag element need to be properly formated.
 *
 * @author Ming Jiang
 *
 */
public class TagElement {
	//     |<--------------------------- value ----------------------------->|
    //<name><subtag1>data1</subtag1><subtag2><nestTag>data</nestTag></subtag2></name>

    //length of the TagElement, including open tag and closing tag
    protected int size;
    //the tag name of this TagElement
    protected String name;
    //data
    protected String value = "";
    //sub tags list
    protected List<TagElement> subTags;
    //for toXMLString formatting
    protected String indent;
    //tag attribute
    protected List<Pair<String, String>> attri;
    //errorXML format.
    protected String headData;
    //errorXML format.
    protected String tailData;
    //keep data for unparsed info.
    protected String rawData;
    // track if the tagelement had been parsed.
    protected boolean isParsed;

    /**
     * initialize empty TagElement
     */
    public TagElement() {
        isParsed = true;
    }

    public TagElement(String tagData) throws UnclosedTagException {
        this(tagData, "", true);
    }

    public TagElement(String tagData, String indent) throws UnclosedTagException {
        this(tagData, indent, true);
    }

    public TagElement(String tagData, String indent, boolean doParse) throws UnclosedTagException {
        rawData = tagData;
        size = rawData.length();
        this.indent = indent;
        isParsed = doParse;
        headData = "";
        tailData = "";
        // check format
//        System.out.println(tagData);
        if (!rawData.startsWith("<") || !rawData.endsWith(">")) {
            int st = rawData.indexOf("<");
            if (st != 0) {
            	try{
                headData = rawData.substring(0, st);
            	}catch(Exception e){
            		System.out.println(e.getMessage()+" "+rawData);
            	}
                System.out.println("contain headData: " + headData);
                rawData = rawData.substring(st);
            }

            int en = rawData.lastIndexOf(">");
            if (en != rawData.length()) {
                tailData = rawData.substring(en + 1);
                System.out.println("contain tailData: " + tailData);
                rawData = rawData.substring(0, en + 1);
            }
        }

        if (isParsed) {
            parse();
        }
    }

    /**
     * initialize the TagElement
     *
     */
    public void parse() throws UnclosedTagException {
        //System.out.println("parse");
        isParsed = true;
        try {
            name = rawData.substring(1, rawData.indexOf(">")).trim();

            value = XMLUtil.getNestedVal(rawData);
            subTags = new ArrayList<>();
            parseValue();
            if (name.contains(" ")) {
                initAttribute();
            }
            //rawData = null;
        } catch (UnclosedTagException e) {
            System.out.println(rawData);
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.out.println(rawData);
            e.printStackTrace();
        }
    }

    /**
     * specified the attribute of main tag
     */
    protected void initAttribute() {
        attri = new ArrayList<>();
        //TODO need to be optimize
        int index = name.indexOf(" ");
//        System.out.println("===name: "+name);
        try{
            attri.addAll(splitAttribute(name.trim()));

        }catch(Exception e){
        	System.err.println(name);
        	e.printStackTrace();
        }

    }
    
    static List<Pair<String, String>> splitAttribute(String attribute){
    	List<Pair<String, String>> attriList = new ArrayList<>();
    	if(!attribute.contains(" "))
    		return attriList;
    	String data = attribute.split(" ",2)[1];
    	
    	int index = 0;
    	while(index<data.length()-1){
    		int nextEqual = data.indexOf("=",index);
    		String name = data.substring(index,nextEqual).trim();
    		
    		char nextChar = data.charAt(nextEqual+1);
    		int spaceCount = 0;
    		while(nextChar==' '){
    			spaceCount++;
    			nextChar = data.charAt(nextEqual+1+spaceCount);
    		}
    		String value = "";
    		char previous=' ';
    		index= nextEqual+spaceCount+1;
    		if(!(nextChar=='"'||nextChar=='\''))
    			nextChar= ' ';
    		while(index<data.length()-1){
    			index++;
    			char now = data.charAt(index);
//    			System.out.println(now);
    			if(previous!='\\'&& now == nextChar){
    				value = data.substring(nextEqual+spaceCount+2,index);
    				attriList.add(new Pair<String,String>(name,value));
//    				System.out.println("Add:  "+name+ " : "+value);
    				index++;
    				break;
    			}else{
    				previous=now;
    			}
    		}
    		spaceCount=0;
    		
    	}
    	return attriList;
    }
 

    /**
     * verify if the data contain sub tag, construct the corresponding tag
     * elements and add to subTags
     *
     * @throws UnclosedTagException      *
     */
    private void parseValue() throws UnclosedTagException {
        if (!value.startsWith("<") || value.startsWith("<![")) {

        } else {
            subTags = new ArrayList<>();
//            System.out.println(value);
            for (String sub : XMLUtil.splitXML(value)) {
//            	System.out.println("sub: "+sub);
                if (sub.startsWith("<!")) {//comment
                    subTags.add(new CommentElement(sub, indent + "\t"));
                } else {
                    if (sub.endsWith("/>")) { // singleTag
                        subTags.add(new SingleTagElement(sub, indent + "\t"));
                    } else if(!sub.startsWith("<")||!sub.endsWith(">")){ //raw data
                    	subTags.add(new PlainElement(sub, indent+"\t"));
                    }else{
                        subTags.add(new TagElement(sub, indent + "\t", false));
                    }
                }
            }
        }
    }

    /**
     * setter
     */
    /**
     * change the tag name of current tag element, attribute is not affected
     *
     * @param newName new tag name
     */
    public void setName(String newName) {
        checkParse();
        if (name.contains(" ")) {
            name = newName + name.substring(name.indexOf(" "));
        } else {
            name = newName;
        }
    }

    /**
     * add tag element into specified location, 0 is the first place
     *
     * @param tag String representation of tag element
     * @param index insert position,
     * @throws UnclosedTagException XML data not close properly
     * @throws IllegalArgumentException tag =null, index <0
     */
    public void addTagElement(String tag, int index) {
        TagElement tmp = null;
        try {
            tmp = new TagElement("<" + tag + "></" + tag + ">", indent);
        } catch (UnclosedTagException e) {
            System.out.println("Create TagElement Fail: tagName = " + tag);
            e.printStackTrace();
        }
        if (tmp != null) {
            addTagElement(tmp, index);
        }
    }

    public void addTagElement(String tag) {
        addTagElement(tag, size());
    }

    public void addTagElement(TagElement tag) {
        addTagElement(tag, size());
    }

    public void addTagElement(TagElement tag, int index) {
        checkParse();
        tag.indent = this.indent + "\t";
        if (index < 0) {
            index = 0;
        }
        //System.out.println("tag: "+tag+"\nindex: "+index);
        if (index >= size()) {
            subTags.add(tag);
        } else {
            subTags.add(index, tag);
        }
    }

    /**
     * remove first TagElement with tagName
     *
     * @param tagName tag name search for
     */
    public TagElement remove(String tagName) {
        for (int i = 0; i < size(); i++) {
            if (subTags.get(i).getName().equals(tagName)) {
                TagElement remove = subTags.remove(i);
                return remove;
            }
        }
        return new TagElement();
    }

    /**
     * remove all direct TagElement with tagName
     *
     * @param tagName tag name search for
     */
    public void removeAll(String... tagName) {
        checkParse();
        if(subTags!=null)
	        for (int i=0;i<subTags.size();i++) {
	        	TagElement tag = subTags.get(i);
	        	boolean isDelete = false;
	        	for(String t: tagName)
		            if (tag.getName().equals(t)) {
		                subTags.remove(i);
		                i--;
		                isDelete = true;
		                break;
		            }
	        	if(!isDelete)
	        		tag.removeAll(tagName);
	        }
    }

    /**
     * set the internal value of the TagElement
     *
     * @param newValue String representation of TagElement
     * @throws UnclosedTagException
     */
    public void setValue(Object newValue) {
        this.value = newValue.toString();
        try {
            this.parseValue();
        } catch (UnclosedTagException e) {// ignore error TagElement 
            value = "";
            for (TagElement tag : subTags) {
                value += (tag.toString());
            }
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * set the internal value to tag
     *
     * @param tag tag element
     */
    public void setValue(TagElement tag) {
        setValue(tag.toString());
    }

    /**
     * String representation of this Object
     */
    public String toString() {
        checkParse();
        if (name == null) {
            return "";
        }
        StringBuilder str = new StringBuilder();

        str.append(headData + "<" + name + ">");
        if (subTags == null || subTags.size() == 0) {
            str.append(value);
        } else {
            for (TagElement tag : subTags) {
                str.append(tag.toString());
            }
        }

        return str + "</" + getName() + ">";
    }

    public String toXMLString() {
        checkParse();
        StringBuilder str = new StringBuilder();
        //beginning indentation
        str.append(indent);

        str.append(headData + "<" + name + ">");
        if (subTags == null || subTags.size() == 0) {
            str.append(value);
            //	System.out.println("fail "+subTags.size());
        } else {
            str.append("\n");
            // sub tags' toXMLString()
            for (TagElement tag : subTags) {
                str.append(tag.toXMLString() + "\n");
            }
            //closing tag indentation
            str.append(indent);
        }
        //closing tag.
        return str + "</" + getName() + ">";
    }

    public void setValue(String tagName, Object newData) {
        TagElement tmp = get(tagName);

        if (tmp.name == null) {
            //	System.out.println("null +="+tagName+"  data:" +newData);
            try {
                tmp = new TagElement("<" + tagName + "></" + tagName + ">");
                //System.out.println("in setValue: "+tmp);
            } catch (UnclosedTagException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            };
            this.addTagElement(tmp);
        }

        tmp.setValue(newData);
        //System.out.println("nest: "+this.toString());
    }

	//	Getters
    /**
     * @return	ArrayList contain subTags
     */
    public List<TagElement> getSubTag() {
        checkParse();
        return subTags;
    }

    /**
     * @return	value of attribute, null if not found
     */
    public String getAttri(String attriName) {
        checkParse();
        if(attri !=null)
	        for (Pair<String, String> p : attri) {
	            if (p.getKey().equals(attriName)) {
	                return p.getObj();
	            }
	        }
        else{

        }
        return "";
    }

    /**
     * @return	the length of this tag element
     */
    public int size() {
        checkParse();
        return size;
    }

    /**
     * @return tag name without the attribute
     */
    public String getName() {
        checkParse();
        if (name.contains(" ")) {
            return name.substring(0, name.indexOf(" "));
        }
        return name;
    }

    /**
     * @return	the internal value;
     */
    public String getValue() {
        checkParse();
        return value;
    }

    /**
     * parse the internal value to integer, if fail, return 0
     *
     * @return
     */
    public int getIntValue() {
        try {
            return Integer.parseInt(getValue());
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * parse the internal value to double, return 0.0, if value is not a number
     *
     * @return
     */
    public double getDoubleValue() {
        try {
            return Double.parseDouble(getValue());
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * parse the internal value to long, return 0L, if value is not a number
     *
     * @return
     */
    public long getLongValue() {
        try {
            return Long.parseLong(getValue());
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * parse the internal value to Boolean, return false, if value is not a
     * boolean
     *
     * @return
     */
    public boolean getBoolValue() {
        try {
            return Boolean.parseBoolean(getValue());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * return the internal value of the first sub tag with name "tagName"
     *
     * @param tagName
     * @return
     */
    public String getVal(String tagName) {
        TagElement tmp = get(tagName);
        return tmp == null ? "" : tmp.getValue();
    }

    /**
     * return the internal value of the first sub tag with name "tagName" as
     * integer, return 0 if the value is not a valid number
     *
     * @param tagName
     * @return
     */
    public int getIntVal(String tagName) {
        TagElement t = get(tagName);
        if (t == null) {
            return 0;
        }
        return t.getIntValue();
    }

    /**
     * return the internal value of the first sub tag with name "tagName" as
     * double, return 0.0 if the value is not a valid number
     *
     * @param tagName
     * @return
     */
    public double getDoubleVal(String tagName) {
        return get(tagName).getDoubleValue();
    }

    /**
     * return the internal value of the first sub tag with name "tagName" as
     * long, return 0L if the value is not a valid number
     *
     * @param tagName
     * @return
     */
    public long getLongVal(String tagName) {
        return get(tagName).getLongValue();
    }

    /**
     * return the internal value of the first sub tag with name "tagName" as
     * double return false if value is not a valid boolean value.
     *
     * @param tagName
     * @return
     */
    public boolean getBoolVal(String tagName) {
        return get(tagName).getBoolValue();
    }

    /**
     * get the TagElement by tag name,	only search from direct sub tag.
     *
     * @param tagName tag name without the "<" and ">"
     * @return	the TagElemnt with specified tag name return null if not found
     */
    public TagElement get(String tagName) {
        checkParse();
        if (subTags != null) {
            for (TagElement tag : subTags) {
                if (tag.getName().equals(tagName)) {
                    return tag;
                }
            }
        }
        return new TagElement();
    }

    public ArrayList<TagElement> getAll(String tagName) {
        checkParse();
        ArrayList<TagElement> tmp = new ArrayList<TagElement>();
        if (subTags != null) {
            for (TagElement tag : subTags) {
                if (tag.getName().equals(tagName)) {
                    tmp.add(tag);
                }
            }
        }
        return tmp;
    }

    /**
     * @return	all sub tag name directly under this tag element
     */
    public ArrayList<String> getSubTagNames() {
        checkParse();
        ArrayList<String> tmp = new ArrayList<String>();

        for (TagElement tag : subTags) {
            if (!(tag instanceof CommentElement)) {
                tmp.add(tag.getName());
            }
        }
        return tmp;
    }

    public boolean contains(String tagName) {
        checkParse();
        for (TagElement tag : subTags) {
            if (tag.getName().equals(tagName)) {
                return true;
            }
        }

        return false;
    }

    public void checkParse() {
        if (!isParsed) {
            try {
                parse();
            } catch (UnclosedTagException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

	public TagElement getElementByID(String id) {
		if(subTags!=null)
			for(TagElement tag: subTags){
				if(tag.getAttri("id").equals(id)){
					return tag;
				}else{
					TagElement nest = tag.getElementByID(id);
					if(nest!=null)
						return nest;
				}
			}
		return null;
	}

	public List<TagElement> getAllByClass(String cls) {
		List<TagElement> tags = new ArrayList<TagElement>();
		if(subTags!=null)
			for(TagElement tag: subTags){
				if(contains(tag.getAttri("class"),cls)){
//					System.out.println("equal");
					tags.add(tag);
				}else{
//					System.out.println("nested");
					tags.addAll(tag.getAllByClass(cls));
				}
			}
		return tags;
	}
	
	
	public static boolean contains(String main, String search){
		return main.endsWith(search)||main.contains(search+" ");
	}
}
