package com.sectic.sbookau.service;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.sectic.sbookau.handler.DataHandler;
import com.sectic.sbookau.model.Configuration;
import com.sectic.sbookau.model.User;
import com.sectic.sbookau.ultils.AppProvider;
import com.sectic.sbookau.ultils.AuthSetting;
import com.sectic.sbookau.ultils.DefSetting;
import com.sectic.sbookau.ultils.FileUtils;
import com.sectic.sbookau.ultils.RestAPIService;
import com.sectic.sbookau.ultils.RestAPIServiceBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import retrofit2.Call;

/**
 * Created by bioz on 9/18/2014.
 */
public class AuthService extends AsyncTask<Void, Integer, Boolean > {
    public OnTaskCompleted mITaskCompleted;
    private boolean mIsLoadJsonConfig;

    public interface OnTaskCompleted {
        void onTaskCompleted(boolean bResult);
    }

    public AuthService(boolean isLoadJsonConfig){
        mIsLoadJsonConfig = isLoadJsonConfig;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        mITaskCompleted.onTaskCompleted(result);
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        User oUser = null;
        try {
            // root json config
            if(mIsLoadJsonConfig) {
                String sJsonConfig = FileUtils.getJSON(DefSetting.gSConfigJsonUrl);
                if (sJsonConfig != null) {
                    Gson gson = new Gson();
                    Configuration oConfig = gson.fromJson(sJsonConfig, Configuration.class);
                    if (oConfig == null) {
                        oConfig = new Configuration();
                    }

                    DefSetting.sRestServerUrl = oConfig.serverUrl;
                    DefSetting.gSystemMessage = oConfig.message;
                    DefSetting.gLatestCodeStep = oConfig.latestCodeStep;
                    DefSetting.gCodeStepMessage = oConfig.codeStepMessage;
                }
            }
            // login
            RestAPIService gitHubService = RestAPIServiceBuilder.CreatRetrofitForAPI().create(RestAPIService.class);
            Call<User> call;
            if(AuthSetting.gLoginType.equals(DefSetting.ENUM_LOGIN_BASE)) {
                call = gitHubService.login(new User(AuthSetting.gSUsername, AuthSetting.gSPassword));
                oUser = call.execute().body();
            }else{
                call = gitHubService.loginGoogle(new User(AuthSetting.gGIdToken));
                oUser = call.execute().body();
            }
            if(oUser != null) {
                DataHandler.saveUser(AppProvider.getContext(), oUser, DataHandler.genUserKey(AuthSetting.gSUsername, AuthSetting.gSPassword));
                AuthSetting.SetContent(oUser.token, oUser.username, oUser.displayName, oUser.userRight, oUser.id);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(oUser == null){
                oUser = DataHandler.loadUser( AppProvider.getContext(), DataHandler.genUserKey(AuthSetting.gSUsername, AuthSetting.gSPassword) );
            }

            if(oUser == null){
                return false;
            }else{
                return true;
            }
        }
    }
}

