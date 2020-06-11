package com.sectic.sbookau.handler;

import android.app.Activity;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.sectic.sbookau.R;
import com.sectic.sbookau.model.PlaySetting;
import com.sectic.sbookau.ultils.DefSetting;

/**
 * Created by bioz on 6/18/2017.
 */

public class PlaySettingHandler {

    @SerializedName("playSetting")
    public PlaySetting playSetting;

    private int startPartIndex;
    private int endPartIndex;
    private int secondCounter;

    private boolean audioPreparing;
    private boolean audioSeeking;
    private boolean loadingBookPart;

    public boolean isLoadingBookPart() {
        return loadingBookPart;
    }

    public boolean iSSystemBusy()
    {
        return (audioSeeking || audioPreparing || loadingBookPart);
    }

    public void setLoadingBookPart(boolean loadingBookPart) {
        this.loadingBookPart = loadingBookPart;
    }

    public PlaySettingHandler( Activity iOGui, int iIStartPartIndex, int iIEndPartIndex, boolean iBIsAudioPreparing, boolean iBIsAudioSeeking) {
        SharedPreferences oSettings = iOGui.getSharedPreferences(iOGui.getString(R.string.s_res_prefs_name), 0);
        LoadPlayingSetting(oSettings);

        this.startPartIndex = iIStartPartIndex;
        this.endPartIndex = iIEndPartIndex;
        this.audioPreparing = iBIsAudioPreparing;
        this.audioSeeking = iBIsAudioSeeking;
    }

    public void LoadPlayingSetting(SharedPreferences iOOSetting){
        String sPlaySetting = iOOSetting.getString("play_setting", "");
        Gson gson = new Gson();
        playSetting = gson.fromJson(sPlaySetting, PlaySetting.class);
        if(playSetting == null){
            playSetting = new PlaySetting();
        }
    }

    public void SavePlayingSetting(Activity iOGui){
        SharedPreferences oSettings = iOGui.getSharedPreferences(iOGui.getString(R.string.s_res_prefs_name), 0);
        Gson gson = new Gson();
        String sPlaySetting = gson.toJson(playSetting);
        SharedPreferences.Editor oEditor = oSettings.edit();
        oEditor.putString("play_setting", sPlaySetting);
        oEditor.apply();
    }

    public void startCounterValue()
    {
        if(isPlayPeriodFromCur()) {
            secondCounter = (playSetting.getMinutes() * 60) + (playSetting.getHours() * 60 * 60);
        }else{
            secondCounter = 0;
        }
    }

    public boolean countDownAndCheckEnd()
    {
        boolean bReturn;

        if(secondCounter > 0)
        {
            secondCounter--;
        }
        if(secondCounter <= 0)
        {
            bReturn = true;
        }else{
            bReturn = false;
        }

        return bReturn;
    }

    public boolean isAudioPreparing() {
        return audioPreparing;
    }

    public void setAudioPreparing(boolean audioPreparing) {
        this.audioPreparing = audioPreparing;
    }

    public boolean isAudioSeeking() {
        return audioSeeking;
    }

    public void setAudioSeeking(boolean audioSeeking) {
        this.audioSeeking = audioSeeking;
    }

    public void setIPlayingMode (boolean iBPlayUntilEndPart, boolean iBPlayPeriodFromCur, boolean iBPlayNPartFromCur)
    {
        int iPlayingMode = DefSetting.PLAY_WITHOUT_PLAN;

        if(iBPlayUntilEndPart)
        {
            iPlayingMode += DefSetting.PLAY_CHOOSING_TO_END;
        }
        if(iBPlayPeriodFromCur)
        {
            iPlayingMode += DefSetting.PLAY_PERIOD_FROM_CHOOSING;
        }
        if(iBPlayNPartFromCur)
        {
            iPlayingMode += DefSetting.PLAY_N_PART_FROM_CHOOSING;
        }

        playSetting.setPlayingMode(iPlayingMode);
    }

    public boolean isValidModeForRepeat()
    {
        boolean bResult;
        if( isPlayWithoutPlan() || isPlayUntilEndPart() )
        {
            bResult = true;
        }else
        {
            bResult = false;
        }
        return  bResult;
    }

    public boolean isPlayWithoutPlan()
    {
        boolean bResult;
        if( playSetting.getPlayingMode()  == 0)
        {
            bResult = true;
        }else
        {
            bResult = false;
        }
        return  bResult;
    }

    public boolean isPlayUntilEndPart()
    {
        boolean bResult;
        if( (playSetting.getPlayingMode() & DefSetting.PLAY_CHOOSING_TO_END) == 0)
        {
            bResult = false;
        }else
        {
            bResult = true;
        }
        return  bResult;
    }

    public boolean isPlayPeriodFromCur()
    {
        boolean bResult;
        if( (playSetting.getPlayingMode() & DefSetting.PLAY_PERIOD_FROM_CHOOSING) == 0)
        {
            bResult = false;
        }else
        {
            bResult = true;
        }
        return  bResult;
    }

    public boolean isPlayNPartFromCur()
    {
        boolean bResult;
        if( (playSetting.getPlayingMode() & DefSetting.PLAY_N_PART_FROM_CHOOSING) == 0)
        {
            bResult = false;
        }else
        {
            bResult = true;
        }
        return  bResult;
    }


    public int getIStartPartIndex() {
        return startPartIndex;
    }

    public void setIStartPartIndex(int iStartPartIndex) {
        this.startPartIndex = iStartPartIndex;
        this.endPartIndex = this.startPartIndex + playSetting.getParts();
    }

    public int getIEndPartIndex() {
        return endPartIndex;
    }

    public void setIEndPartIndex(int iEndPartIndex) {
        this.endPartIndex = iEndPartIndex;
    }
}
