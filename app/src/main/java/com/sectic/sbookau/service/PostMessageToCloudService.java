package com.sectic.sbookau.service;

import android.os.AsyncTask;

import com.sectic.sbookau.model.BaseResponse;
import com.sectic.sbookau.model.Message;
import com.sectic.sbookau.ultils.RestAPIService;
import com.sectic.sbookau.ultils.RestAPIServiceBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import retrofit2.Call;

/**
 * Created by bioz on 9/18/2014.
 */
public class PostMessageToCloudService extends AsyncTask<Void, Integer, Boolean > {

    private String sToken;
    private String sContent;
    private String sExtraInfo;

    Message oMessage;

    public OnTaskCompleted oITaskCompleted;

    public interface OnTaskCompleted {
        void onTaskCompleted(boolean bResult);
    }

    public PostMessageToCloudService(String sToken, String sContent, String sExtraInfo, String sAction)
    {
        oMessage = new Message(sAction);
        this.sToken = sToken;
        this.sContent = sContent;
        this.sExtraInfo = sExtraInfo;
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
        try {
            //RestAPIService gitHubService = RestAPIServiceBuilder.retrofit.create(RestAPIService.class);
            RestAPIService gitHubService = RestAPIServiceBuilder.CreatRetrofitForAPI().create(RestAPIService.class);
            oMessage.content += sContent;
            oMessage.extraInfo += sExtraInfo;
            Call<BaseResponse> call = gitHubService.sendtosystem(sToken, oMessage);
            BaseResponse oBaseResponse = call.execute().body();

            if(oBaseResponse != null && oBaseResponse.id != null){
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

