package com.sectic.sbookau.ultils;

/**
 * Created by bioz on 6/22/2017.
 */

public class DefSetting {
    public static String sRestServerUrl = "https://dev-api.wetop.me/sbookau/v1/";
    public static String gGoogleStoreUrl ="http://play.google.com/store/apps/details?id=%s";
    public static String gSystemMessage = "";
    public static int gLatestCodeStep = 0;
    public static String gCodeStepMessage = "";

    public static final int RC_SIGN_IN = 9001;
    public static final String SERVER_CLIENT_ID_FOR_GOOGLE_SERVICE = "393842627090-5acn4hfn1j7f776gni8sgjhequp8dh5i.apps.googleusercontent.com";

    // dropbox account : bionezero@gmail.com
    public static final String gSConfigJsonUrl = "https://www.dropbox.com/s/f1ofl6e85179t10/slab-sbookau-config.json?dl=1";
    public static final String gSBookHistory = "book_history.json";
    public static final String gSJSONDataFilePattern = "%s.json";
    public static final String gSMediaFilePattern = "%s.%s";
    public static final String gSSBookAuSDRoot = "/sbookau";
    public static int gPageSize = 15;
    public static int gMaxHistorySize = 15;

    public static String ENUM_LOGIN_BASE = "BASE";
    public static String ENUM_LOGIN_GOOGLE = "GOOGLE";

    public static String USER_RIGHT_ADMIN = "ADMIN";
    public static String USER_RIGHT_ANONYMOUS = "ANONYMOUS";

    public static final int PLAY_WITHOUT_PLAN = 0;
    public static final int PLAY_CHOOSING_TO_END = 1;
    public static final int PLAY_N_PART_FROM_CHOOSING = 2;
    public static final int PLAY_PERIOD_FROM_CHOOSING = 4;
}
