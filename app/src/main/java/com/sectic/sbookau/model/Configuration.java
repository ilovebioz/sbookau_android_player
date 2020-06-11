package com.sectic.sbookau.model;
import com.sectic.sbookau.ultils.DefSetting;

/**
 * Created by bioz on 6/22/2017.
 */

public class Configuration {

    public String serverUrl;
    public String status;
    public String message;
    public int latestCodeStep;
    public String codeStepMessage;

    public Configuration(){
        this.serverUrl = DefSetting.sRestServerUrl;
        latestCodeStep = 0;
        message = "";
        codeStepMessage="";
        status="OK";
    }

    public Configuration(String serverUrl){
        this.serverUrl = serverUrl;
    }
}
