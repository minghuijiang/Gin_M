package com.mingJiang.util.account;

import com.mingJiang.util.xml.TagElement;

public class Account {

    private String username;
//	private String rawData;
    private String password;
    private String rawPass;
    private Cookies cookies;

    /**
     * data is the information of Account, format: username:password:cookieVal
     *
     * @param data
     */
    public Account(String data) {
        this(data.split(":", 3));
    }

    /**
     * for convenience initialize
     *
     * @param sp
     */
    private Account(String[] sp) {
        this(sp[0], sp[1], new Cookies(sp[2]));
    }

    /**
     * tag in format
     * <user>
     * <account>account</account>
     * <password>password</password>
     * <cookie>cookie</cookie>
     * <otherInfo>x</otherInfo>
     * <otherInfo>x</otherInfo>
     * </user>
     *
     * @param tag
     */
    public Account(TagElement tag) {
        this(tag.getVal("account"),
                tag.getVal("password") //"520911jing"
                , new Cookies(tag.getVal("cookies")));
    }

    /**
     * set username to user, password to pass, cookies to new cookies.
     *
     * @param user
     * @param pass
     */
    public Account(String user, String pass) {
        this(user, pass, new Cookies());
    }

    public Account(String user, String pass, Cookies cookie) {
        this.username = user;
        this.password = pass;
        this.rawPass= pass;
        //this.rawData = EncodeUtil.encode(user+"!:!"+password);
        this.cookies = cookie;
    }


    public String getUser() {
        return username;
    }

    public void setUser(String user) {
        this.username = user;
    }

    public String getPass() {
        return password;
    }

    public void setPass(String pass) {
        this.password = pass;
    }

    public Cookies getCookies() {
        return cookies;
    }

    public void setCookies(Cookies cookies) {
        this.cookies = cookies;
    }

    public String toXML() {
        return "<account>" + username + "</account>"
                + "<password>" + password + "</password>"
                    +"<cookies>"+ cookies.toString()+"</cookies>";
    }

    /**
	 * @return the rawPass
	 */
	public String getRawPass() {
		return rawPass;
	}

	/**
	 * @param rawPass the rawPass to set
	 */
	public void setRawPass(String rawPass) {
		this.rawPass = rawPass;
	}

	public String toString() {
        return username + ":" + password + ":" + cookies;
    }
}
