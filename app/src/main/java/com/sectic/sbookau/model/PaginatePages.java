package com.sectic.sbookau.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bioz on 1/6/2016.
 */
public class PaginatePages {
    @SerializedName("current")
    public int iCurrent;
    @SerializedName("prev")
    public int iPrev;
    @SerializedName("hasPrev")
    public boolean bHasPrev;
    @SerializedName("next")
    public int iNext;
    @SerializedName("hasNext")
    public boolean bHasNext;
    @SerializedName("total")
    public int iTotal;

    public PaginatePages() {
        iCurrent = 0;
        iPrev = 0;
        iNext = 0;
        iTotal = 0;
        bHasPrev = false;
        bHasNext = false;
    }
}
