package com.sectic.sbookau.model;

import com.google.gson.annotations.SerializedName;
import com.sectic.sbookau.ultils.DefSetting;

/**
 * Created by bioz on 9/20/2014.
 */
public class AudioPart {
    @SerializedName("id")
    public String sId;
    @SerializedName("name")
    public String sName;
    //@SerializedName("book")
    public String sBookId;

    @SerializedName("reader")
    public String sReader;
    @SerializedName("url")
    public String sUrl;
    @SerializedName("status")
    public String sStatus;
    @SerializedName("length")
    public int iLength;
    @SerializedName("format")
    public String sFormat;

    @SerializedName("partNum")
    public int iPartNum;

    @SerializedName("downloadCount")
    public int iDownloadCount;
    @SerializedName("viewCount")
    public int iViewCount;

    public int iDownloadProgress = -1;

    public AudioPart(String sId, String sName, String sBookId, String sReader, String sUrl, String sStatus, int iLength, String sFormat, int iPartNum, int iDownloadCount, int iViewCount)
    {
        this.sId = sId;
        this.sName = sName;
        this.sBookId = sBookId;
        this.iPartNum = iPartNum;
        this.iDownloadCount = iDownloadCount;
        this.sReader = sReader;
        this.sUrl = sUrl;
        this.sStatus = sStatus;
        this.iLength = iLength;
        this.sFormat = sFormat;
        this.iViewCount = iViewCount;
        this.iDownloadProgress = -1;
    }

    public String GetMediaFileName (){
        return String.format(DefSetting.gSMediaFilePattern, sId, sFormat);
    }
}
