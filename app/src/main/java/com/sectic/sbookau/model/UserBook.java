package com.sectic.sbookau.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bioz on 9/20/2014.
 */
public class UserBook {
    @SerializedName("creator")
    public String sCreator;
    @SerializedName("book")
    public String sBook;

    @SerializedName("isLikeAudio")
    public boolean bIsLikeAudio;
    @SerializedName("isLikePicture")
    public boolean bIsLikePicture;
    @SerializedName("isLikeDoc")
    public boolean bIsLikeDoc;

    @SerializedName("isReportAudio")
    public boolean bIsReportAudio;
    @SerializedName("isReportPicture")
    public boolean bIsReportPicture;
    @SerializedName("isReportDoc")
    public boolean bIsReportDoc;

    @SerializedName("isBookmarkAudio")
    public boolean bIsBookmarkAudio;
    @SerializedName("isBookmarkPicture")
    public boolean bIsBookmarkPicture;
    @SerializedName("isBookmarkDoc")
    public boolean bIsBookmarkDoc;

    public UserBook(){}
    public UserBook(String creator, String book, boolean isLikeAudio, boolean isReportAudio, boolean isBookmarkAudio)
    {
        this.sCreator = creator;
        this.sBook = book;
        this.bIsLikeAudio = isLikeAudio;
        this.bIsReportAudio = isReportAudio;
        this.bIsBookmarkAudio = isBookmarkAudio;
    }

    public void Clone(UserBook iOUserBook){
        if(iOUserBook == null){
            return;
        }
        this.sCreator = iOUserBook.sCreator;
        this.sBook = iOUserBook.sBook;
        this.bIsLikeAudio = iOUserBook.bIsLikeAudio;
        this.bIsReportAudio = iOUserBook.bIsReportAudio;
        this.bIsBookmarkAudio = iOUserBook.bIsBookmarkAudio;
    }
}
