package com.sectic.sbookau.ultils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by bioz on 9/23/2014.
 */
public class BaseUtils {

    public static String convertInt2Time(int iILengthInSecond)
    {
        String sResult;
        int iMinutes;
        int iSeconds;
        iMinutes = iILengthInSecond / 60;
        iSeconds = iILengthInSecond % 60;

        sResult = String.format("%d:%d", iMinutes , iSeconds );

        return sResult;
    }
    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
