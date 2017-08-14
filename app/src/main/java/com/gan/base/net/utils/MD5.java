package com.gan.base.net.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	public static String md5(String input){
        String result = input;
        if(input != null) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
            try {
				md.update(input.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
            BigInteger hash = new BigInteger(1, md.digest());
            result = hash.toString(16);
            if ((result.length() % 2) != 0) {
                result = "0" + result;
            } 
            }catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
        }
        return result;
    }
}
