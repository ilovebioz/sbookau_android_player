package com.sectic.sbookau.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bioz on 9/25/2014.
 */
public class PlayingInfo {
    @SerializedName("bookId")
    private String bookId;
    @SerializedName("bookName")
    private String bookName;
    @SerializedName("curPlayingPos")
    private int curPlayingPos;
    @SerializedName("curPartIndex")
    private int curPartIndex;

    @SerializedName("bookPartUrl")
    private String bookPartUrl;
    @SerializedName("bookPartFileInExternal")
    private String bookPartFileInExternal;
    @SerializedName("bookPartFileInInternal")
    private String bookPartFileInInternal;

    @SerializedName("bookTotalPart")
    private int bookTotalPart;

    public PlayingInfo()
    {
        bookId = "";
        bookPartUrl = "";
        bookName = "";
        bookTotalPart = 0;

        curPlayingPos = -1;
        curPartIndex = -1;
    }

    public String getBookPartFileInInternal() {
        return bookPartFileInInternal;
    }

    public void setBookPartFileInInternal(String bookPartFileInInternal) {
        this.bookPartFileInInternal = bookPartFileInInternal;
    }


    public String getBookPartFileInExternal() {
        return bookPartFileInExternal;
    }

    public void setBookPartFileInExternal(String bookPartFileInExternal) {
        this.bookPartFileInExternal = bookPartFileInExternal;
    }


    public int getBookTotalPart() {
        return bookTotalPart;
    }

    public void setBookTotalPart(int bookTotalPart) {
        this.bookTotalPart = bookTotalPart;
    }


    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }


    public String getBookPartUrl() {
        return bookPartUrl;
    }

    public void setBookPartUrl(String bookPartUrl) {
        this.bookPartUrl = bookPartUrl;
    }


    public int getCurPlayingPos() {
        return curPlayingPos;
    }

    public void setCurPlayingPos(int curPlayingPos) {
        this.curPlayingPos = curPlayingPos;
    }


    public int getCurPartIndex() {
        return curPartIndex;
    }

    public void setCurPartIndex(int curPartIndex) {
        this.curPartIndex = curPartIndex;
    }
}
