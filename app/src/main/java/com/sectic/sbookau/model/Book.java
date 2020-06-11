package com.sectic.sbookau.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bioz on 9/20/2014.
 */
public class Book {
    @SerializedName("id")
    public String sId;
    @SerializedName("name")
    public String sName;
    @SerializedName("partCount")
    public int iPartCount;

    @SerializedName("author")
    public String sAuthor;
    @SerializedName("translator")
    public String sTranslator;
    @SerializedName("isRecommend")
    public boolean bIsRecommend;
    @SerializedName("status")
    public String sStatus;

    @SerializedName("viewCount")
    public int iViewCount;
    @SerializedName("likeCount")
    public int iLikeCount;

    public Book(String id){
        this.sId = id;
    }
    public Book(String sId, String sName, int iPartCount, String sAuthor, String sTranslator, boolean bIsRecommend, String sStatus, int iViewCount, int iLikeCount)
    {
        this.sId = sId;
        this.sName = sName;
        this.iPartCount = iPartCount;
        this.sAuthor = sAuthor;
        this.sTranslator = sTranslator;
        this.bIsRecommend = bIsRecommend;
        this.sStatus = sStatus;
        this.iViewCount = iViewCount;
        this.iLikeCount = iLikeCount;
    }
}
