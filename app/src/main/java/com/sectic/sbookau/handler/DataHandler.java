package com.sectic.sbookau.handler;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sectic.sbookau.model.BookList;
import com.sectic.sbookau.model.AudioPartList;
import com.sectic.sbookau.model.CatalogList;
import com.sectic.sbookau.model.User;
import com.sectic.sbookau.model.UserBook;
import com.sectic.sbookau.ultils.BaseUtils;
import com.sectic.sbookau.ultils.DefSetting;
import com.sectic.sbookau.ultils.FileUtils;

import java.lang.reflect.Type;

/**
 * Created by bioz on 6/18/2017.
 */

public class DataHandler {
    public DataHandler(){}

    public static String genAudioPartListKey(String iSFilter, String iSPage, String iSPageSize){
        String sResult = "getAudioParts" + iSFilter + iSPage + iSPageSize;
        return BaseUtils.md5(sResult);
    }
    public static AudioPartList loadAudioPartList(Context iOContext, String iSKey) {
        AudioPartList oResult = null;
        try{
            String sJson = FileUtils.readJsonFromInternalFile(iOContext, String.format(DefSetting.gSJSONDataFilePattern, iSKey));
            if( sJson != null){
                Gson gson = new Gson();
                Type collectionType = new TypeToken<AudioPartList>(){}.getType();
                oResult = gson.fromJson(sJson, collectionType);
            }
        }catch(Exception e){
            oResult = null;
        }
        return oResult;
    }

    public static boolean saveAudioPartList(Context iOContext, AudioPartList iOAudioPartList, String iSKey) {
        boolean bResult = true;
        try {
            if(iOAudioPartList != null) {
                Gson gson = new Gson();
                String sJsonBooks = gson.toJson(iOAudioPartList);
                FileUtils.writeJsonToInternalFile(iOContext, String.format(DefSetting.gSJSONDataFilePattern, iSKey), sJsonBooks);
            }else{
                bResult = false;
            }
        }catch(Exception e){
            bResult = false;
        }
        return bResult;
    }

    public static String genBookListKey(String iSFilter, String iSQuery, String iSPage, String iSPageSize){
        String sResult = "getBooks" + iSFilter + iSQuery + iSPage + iSPageSize;
        return BaseUtils.md5(sResult);
    }

    public static BookList loadBookList(Context iOContext, String iSKey) {
        BookList oResult = null;
        try{
            String sJson = FileUtils.readJsonFromInternalFile(iOContext, String.format(DefSetting.gSJSONDataFilePattern, iSKey));
            if( sJson != null){
                Gson gson = new Gson();
                Type collectionType = new TypeToken<BookList>(){}.getType();
                oResult = gson.fromJson(sJson, collectionType);
            }
        }catch(Exception e){
            oResult = null;
        }
        return oResult;
    }

    public static boolean saveBookList(Context iOContext, BookList iOBookList, String iSKey) {
        boolean bResult = true;
        try {
            if(iOBookList != null){
                Gson gson = new Gson();
                String sJsonBooks = gson.toJson(iOBookList);
                FileUtils.writeJsonToInternalFile(iOContext, String.format(DefSetting.gSJSONDataFilePattern, iSKey), sJsonBooks);
            }else{
                bResult = false;
            }
        }catch(Exception e){
            bResult = false;
        }
        return bResult;
    }

    public static String genCatalogListKey(String iSFilter){
        String sResult = "getTags" + iSFilter;
        return BaseUtils.md5(sResult);
    }

    public static CatalogList loadCatalogList(Context iOContext, String iSKey) {
        CatalogList oResult = null;
        try{
            String sJson = FileUtils.readJsonFromInternalFile(iOContext, String.format(DefSetting.gSJSONDataFilePattern, iSKey));
            if( sJson != null){
                Gson gson = new Gson();
                Type collectionType = new TypeToken<CatalogList>(){}.getType();
                oResult = gson.fromJson(sJson, collectionType);
            }
        }catch(Exception e){
            oResult = null;
        }
        return oResult;
    }

    public static boolean saveCatalogList(Context iOContext, CatalogList iOCatalogList, String iSKey) {
        boolean bResult = true;
        try {
            if(iOCatalogList != null){
                Gson gson = new Gson();
                String sJsonBooks = gson.toJson(iOCatalogList);
                FileUtils.writeJsonToInternalFile(iOContext, String.format(DefSetting.gSJSONDataFilePattern, iSKey), sJsonBooks);
            }else{
                bResult = false;
            }
        }catch(Exception e){
            bResult = false;
        }
        return bResult;
    }

    public static String genUserKey(String iSUsername, String iSPassword){
        String sResult = "login" + iSUsername + iSPassword;
        return BaseUtils.md5(sResult);
    }

    public static User loadUser(Context iOContext, String iSKey) {
        User oResult = null;
        try{
            String sJson = FileUtils.readJsonFromInternalFile(iOContext, String.format(DefSetting.gSJSONDataFilePattern, iSKey));
            if( sJson != null){
                Gson gson = new Gson();
                Type collectionType = new TypeToken<User>(){}.getType();
                oResult = gson.fromJson(sJson, collectionType);
            }
        }catch(Exception e){
            oResult = null;
        }
        return oResult;
    }

    public static boolean saveUser(Context iOContext, User iOUser, String iSKey) {
        boolean bResult = true;
        try {
            if(iOUser != null){
                Gson gson = new Gson();
                String sJsonBooks = gson.toJson(iOUser);
                FileUtils.writeJsonToInternalFile(iOContext, String.format(DefSetting.gSJSONDataFilePattern, iSKey), sJsonBooks);
            }else{
                bResult = false;
            }
        }catch(Exception e){
            bResult = false;
        }
        return bResult;
    }

    public static String genUserBookKey(String iSCreator, String iSBook){
        String sResult = "getUserBook" + iSCreator + iSBook;
        return BaseUtils.md5(sResult);
    }

    public static UserBook loadUserBook(Context iOContext, String iSKey) {
        UserBook oResult = null;
        try{
            String sJson = FileUtils.readJsonFromInternalFile(iOContext, String.format(DefSetting.gSJSONDataFilePattern, iSKey));
            if( sJson != null){
                Gson gson = new Gson();
                Type collectionType = new TypeToken<UserBook>(){}.getType();
                oResult = gson.fromJson(sJson, collectionType);
            }
        }catch(Exception e){
            oResult = null;
        }
        return oResult;
    }

    public static boolean saveUserBook(Context iOContext, UserBook iOUserBook, String iSKey) {
        boolean bResult = true;
        try {
            if(iOUserBook != null){
                Gson gson = new Gson();
                String sJsonBooks = gson.toJson(iOUserBook);
                FileUtils.writeJsonToInternalFile(iOContext, String.format(DefSetting.gSJSONDataFilePattern, iSKey), sJsonBooks);
            }else{
                bResult = false;
            }
        }catch(Exception e){
            bResult = false;
        }
        return bResult;
    }
}
