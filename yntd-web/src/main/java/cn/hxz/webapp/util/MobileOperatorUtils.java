package cn.hxz.webapp.util;

import java.util.regex.Pattern;


public class MobileOperatorUtils {
	 /**
     * 中国电信号码格式验证 手机段： 133,153,180,181,189,177,1700,173,199
     **/
	private static final String CHINA_TELECOM_PATTERN = "(^1(33|53|77|73|99|8[019])\\d{8}$)|(^1700\\d{7}$)";
 
	public static boolean isChineTelecom(String tel) {
		boolean b1 = tel == null || tel.trim().equals("") ? false : match(CHINA_TELECOM_PATTERN, tel);
	        if (b1) {
	            return true;
	        }else {
	        	return false;
	        }
	 }

	private static boolean match(String regex, String tel) {
		 return Pattern.matches(regex, tel);  
	}

 }
