package com.sectic.sbookau.ultils;

/**
 * Created by bioz on 6/23/2017.
 */

public class AuthSetting {
    public static String gLoginType;

    public static String gGIdToken;
    public static String gGPhotoUrl;
    public static String gGDisplayName;
    public static String gGEmail;

    public static final String gSUsername = "anonymous";
    public static final String gSPassword = "anonymous";

    public static String gToken;
    public static String gUsername;
    public static String gDisplayName;
    public static String gUserRight;
    public static String gUserId;
    public AuthSetting(){
        gToken = "";
        gUsername = "";
        gDisplayName = "";
        gUserRight = DefSetting.USER_RIGHT_ANONYMOUS;
        gLoginType = DefSetting.ENUM_LOGIN_BASE;
        gGPhotoUrl = "";
        gGDisplayName = "";
        gGEmail = "";
    }

    public static void SetContent(String iSToken, String iSUsername, String iSDisplayName, String iSUserRight, String iSUserId){
        gToken = iSToken;
        gUsername = iSUsername;
        gDisplayName = iSDisplayName;
        gUserRight = iSUserRight;
        gUserId = iSUserId;
    }

    public static boolean IsNotAnonymous(){
        if(gUserRight == null || gUserRight.isEmpty() || gUserRight.equals(DefSetting.USER_RIGHT_ANONYMOUS) ){
            return false;
        }else{
            return true;
        }
    }
}
