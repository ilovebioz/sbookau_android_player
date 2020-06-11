package com.sectic.sbookau.handler;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.sectic.sbookau.R;
import com.sectic.sbookau.model.PlayingInfo;

/**
 * Created by bioz on 6/18/2017.
 */

public class PlayingInfoHandler {
    public PlayingInfo playingInfo;

    public PlayingInfoHandler(){
        playingInfo = new PlayingInfo();
    }

    public PlayingInfoHandler(Activity iOGui){
        SharedPreferences oSettings = iOGui.getSharedPreferences(iOGui.getString(R.string.s_res_prefs_name), 0);
        LoadPlayingInfo(oSettings);
    }

    public void LoadPlayingInfo(SharedPreferences iOOSetting)
    {
        String sPlaySetting = iOOSetting.getString("playing_info", "");
        Gson gson = new Gson();
        playingInfo = gson.fromJson(sPlaySetting, PlayingInfo.class);
        if(playingInfo == null){
            playingInfo = new PlayingInfo();
        }
    }

    public void SavePlayingInfo(Activity iOGui){
        SharedPreferences oSettings = iOGui.getSharedPreferences(iOGui.getString(R.string.s_res_prefs_name), 0);
        Gson gson = new Gson();
        String sPlayInfo = gson.toJson(playingInfo);
        SharedPreferences.Editor oEditor = oSettings.edit();
        oEditor.putString("playing_info", sPlayInfo);
        oEditor.apply();
    }

    public int getNextPart()
    {
        int iReturn;

        if ( (playingInfo.getCurPartIndex() < 0) || (playingInfo.getCurPartIndex() >= (playingInfo.getBookTotalPart() - 1)) )
        {
            iReturn = -1;
        }else{
            iReturn = playingInfo.getCurPartIndex() + 1;
        }

        return iReturn;
    }

    public int getPrePart()
    {
        int iReturn;
        if( (playingInfo.getCurPartIndex()  <= 0) || (playingInfo.getCurPartIndex() > (playingInfo.getBookTotalPart() - 1)) )
        {
            iReturn = -1;
        }else{
            iReturn = playingInfo.getCurPartIndex() - 1;
        }
        return iReturn;
    }

    public int getMaxNPart()
    {
        int iReturn;
        if(playingInfo.getCurPartIndex() < 0)
        {
            iReturn = playingInfo.getBookTotalPart();
        }else{
            iReturn = playingInfo.getBookTotalPart() - playingInfo.getCurPartIndex();
        }

        return iReturn;
    }

}
