package com.sectic.sbookau.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bioz on 6/23/2017.
 */

public class Catalog {
    @SerializedName("id")
    public String id;
    @SerializedName("value")
    public String value;
    @SerializedName("tagType")
    public String tagType;

    @SerializedName("refCount")
    public int refCount;
    @SerializedName("status")
    public String status;

    public int iOrder;

    public Catalog(){
    }

    public Catalog(int iIOrder, String iSId, String iSName, String iSCatalogType, int iITotalBook, String iSStatus){
        this.id = iSId;
        this.value = iSName;
        this.tagType = iSCatalogType;
        this.refCount = iITotalBook;
        this.status = iSStatus;
        this.iOrder = iIOrder;
    }
}
