package com.sectic.sbookau.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bioz on 6/23/2017.
 */

public class AudioPartList {
    @SerializedName("data")
    public List<AudioPart> lOBookPart;
    @SerializedName("pages")
    public PaginatePages oPages;
    @SerializedName("items")
    public PaginateItems oItems;

    public AudioPartList(){
        lOBookPart = new ArrayList<>();
        oPages = new PaginatePages();
        oItems = new PaginateItems();
    }

    public boolean isHasNext(){
        boolean bResult = false;
        if( (lOBookPart != null) && (oPages != null) && (oPages.bHasNext) ){
            bResult = true;
        }
        return bResult;
    }

    public boolean isHasPre(){
        boolean bResult = false;
        if( (lOBookPart != null) && (oPages != null) && (oPages.bHasPrev) ){
            bResult = true;
        }
        return bResult;
    }
}
