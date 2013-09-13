package org.openntf.xtasks.misc;

public class Utils {

	public static String getUnique() {
	    String CHARLIST = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";

	    long number=System.currentTimeMillis();
	    int base=CHARLIST.length();

	    String result="";
	     while (number > 0){
	      result = CHARLIST.charAt((int)(number%base))+result;
	      number = number/base;
	     }
	     return result;
	}
	
	public static boolean isEmptyString(String value) {
		return(value==null || value.equals(""));
	}
	
}
