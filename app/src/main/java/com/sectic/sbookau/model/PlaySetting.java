package com.sectic.sbookau.model;
import com.google.gson.annotations.SerializedName;
import com.sectic.sbookau.ultils.DefSetting;

import java.io.Serializable;

/**
 * Created by bioz on 9/23/2014.
 */
public class PlaySetting implements Serializable {
    @SerializedName("playingMode")
    private int playingMode;
    @SerializedName("hours")
    private int hours;
    @SerializedName("minutes")
    private int minutes;
    @SerializedName("parts")
    private int parts;
    @SerializedName("repeat")
    private boolean repeat;

    public PlaySetting(){
        playingMode = DefSetting.PLAY_WITHOUT_PLAN;
        parts = 1;
        hours = 0;
        minutes = 1;
        repeat = false;
    }

    public int getPlayingMode() {
        return playingMode;
    }

    public void setPlayingMode(int playingMode) {
        this.playingMode = playingMode;
    }

    public int getParts() {
        return parts;
    }

    public void setParts(int parts) {
        this.parts = parts;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minute) {
        this.minutes = minute;
    }

    public boolean getRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }
}
