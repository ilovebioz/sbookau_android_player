package com.sectic.sbookau.service;


import android.os.AsyncTask;

import com.sectic.sbookau.model.AudioPart;
import com.sectic.sbookau.ultils.RestAPIService;
import com.sectic.sbookau.ultils.RestAPIServiceBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import retrofit2.Call;

/**
 * Created by bioz on 9/18/2014.
 */
public class PostAudioPartToCloudService extends AsyncTask<Void, Integer, Boolean > {

    private String sToken;
    private AudioPart oAudioPart;

    public OnTaskCompleted oITaskCompleted;

    public interface OnTaskCompleted {
        void onTaskCompleted(boolean bResult);
    }

    public PostAudioPartToCloudService(String sToken, AudioPart oAudioPart)
    {
        this.sToken = sToken;
        this.oAudioPart = oAudioPart;
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
        oITaskCompleted.onTaskCompleted(result);
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        AudioPart oResult= null;
        try {
            //RestAPIService gitHubService = RestAPIServiceBuilder.retrofit.create(RestAPIService.class);
            RestAPIService gitHubService = RestAPIServiceBuilder.CreatRetrofitForAPI().create(RestAPIService.class);
            Call<AudioPart> call = gitHubService.updateAudioParts(this.sToken, this.oAudioPart.sId, this.oAudioPart);
            oResult = call.execute().body();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(oResult == null){
                return false;
            }else{
                return true;
            }
        }
    }
}

