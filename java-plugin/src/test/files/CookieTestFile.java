package fr.greencodeinitiative.java.utils;

import javax.servlet.http.Cookie;

public class CookieTest {

	public String testKo(String[] strings) {

		//Integer test = new Integer();
		Cookie c = new Cookie("id", "674684641");
	}

	public String testNonReg(String[] strings) {

		//Integer test = new Integer();
		Cookie c = new Cookie("id", "674684641");

		// set the validity 
		c.setMaxAge(24 * 3600);
		// send cookie via HTTP
		res.addCookie(c);
	}
	
	public String testNonReg(String[] strings) {

		//Integer test = new Integer();
		Cookie c = new Cookie("id", "674684641");
		setDuration(c);
	}

	private void setDuration(Cookie c) {
		c.setMaxAge(24 * 3600);
	}
}
