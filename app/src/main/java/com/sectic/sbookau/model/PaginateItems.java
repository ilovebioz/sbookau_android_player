package com.sectic.sbookau.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bioz on 1/6/2016.
 */
public class PaginateItems {
    @SerializedName("begin")
    public int iBegin;
    @SerializedName("end")
    public int iEnd;
    @SerializedName("total")
    public int iTotal;

    public PaginateItems() {
        iBegin = 0;
        iEnd = 0;
        iTotal = 0;
    }
}
