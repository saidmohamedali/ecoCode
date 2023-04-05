package fr.greencodeinitiative.java.utils;

import javax.servlet.http.Cookie;

public class CookieTest {


    public String testKo(String[] strings) {


        Integer test =  new Integer() ;
        Cookie C = new Cookie("id","674684641"); // Noncompliant {{Use System.arraycopy to copy arrays}}

        // set the validity 

    }


    public String testNonReg(String[] strings) {


        Integer test =  new Integer() ;


        Cookie C = new Cookie("id","674684641");

       // set the validity 
        C.setMaxAge(24*3600);
        // send cookie via HTTP
        res.addCookie(C);
    }


}
