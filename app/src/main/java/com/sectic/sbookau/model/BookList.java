package com.sectic.sbookau.model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.sectic.sbookau.ultils.DefSetting;
import com.sectic.sbookau.ultils.FileUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bioz on 6/23/2017.
 */

public class BookList {
    @SerializedName("data")
    public List<Book> lOBook;
    @SerializedName("pages")
    public PaginatePages oPages;
    @SerializedName("items")
    public PaginateItems oItems;

    public BookList(){
        lOBook = new ArrayList<>();
        oPages = new PaginatePages();
        oItems = new PaginateItems();
    }

    public boolean isHasNext(){
        boolean bResult = false;
        if( (lOBook != null) && (oPages != null) && (oPages.bHasNext) ){
            bResult = true;
        }
        return bResult;
    }

    public boolean isHasPre(){
        boolean bResult = false;
        if( (lOBook != null) && (oPages != null) && (oPages.bHasPrev) ){
            bResult = true;
        }
        return bResult;
    }

    public void loadBookHistoryFromInternalStorage(Context iOContext) {
        String sJson = FileUtils.readJsonFromInternalFile(iOContext, DefSetting.gSBookHistory);
        if( sJson != null){
            Gson gson = new Gson();
            Type collectionType = new TypeToken<ArrayList<Book>>(){}.getType();
            lOBook = gson.fromJson(sJson, collectionType);
        }
    }

    public void updateBookHistoryFromInternalStorage(Context iOContext, Book iOBook) {
        String sJson = FileUtils.readJsonFromInternalFile(iOContext, DefSetting.gSBookHistory);
        Gson gson = new Gson();
        Type collectionType = new TypeToken<ArrayList<Book>>(){}.getType();
        lOBook = gson.fromJson(sJson, collectionType);

        if( lOBook == null ){
            lOBook = new ArrayList<>();
        }

        boolean bIsReplace = false;
        int i;
        for(i = 0; i < lOBook.size(); i++){
            if(lOBook.get(i).sId.equals(iOBook.sId)){
                lOBook.set(i, iOBook);
                bIsReplace = true;
                break;
            }
        }

        if(!bIsReplace){
            if(lOBook.size() >= DefSetting.gMaxHistorySize){
                lOBook.remove(0);
                lOBook.add(iOBook);
            }else{
                lOBook.add(iOBook);
            }
        }else{
            for(int j = i; j < (lOBook.size() - 1); j++){
                Collections.swap(lOBook, j, j + 1);
            }
        }

        String sJsonBooks = gson.toJson(lOBook);
        FileUtils.writeJsonToInternalFile(iOContext, DefSetting.gSBookHistory, sJsonBooks);
    }
}
