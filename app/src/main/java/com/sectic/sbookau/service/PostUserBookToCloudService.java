package com.sectic.sbookau.service;


import android.os.AsyncTask;

import com.sectic.sbookau.model.UserBook;
import com.sectic.sbookau.ultils.RestAPIService;
import com.sectic.sbookau.ultils.RestAPIServiceBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import retrofit2.Call;

/**
 * Created by bioz on 9/18/2014.
 */
public class PostUserBookToCloudService extends AsyncTask<Void, Integer, Boolean > {

    private String sToken;
    private UserBook oUserbook;
    private UserBook oResult;

    public OnTaskCompleted oITaskCompleted;

    public interface OnTaskCompleted {
        void onTaskCompleted(boolean bResult, UserBook oUserBook);
    }

    public PostUserBookToCloudService(String sToken, UserBook oUserBook)
    {
        this.sToken = sToken;
        this.oUserbook = oUserBook;
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
        oITaskCompleted.onTaskCompleted(result, this.oUserbook);
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        try {
            //RestAPIService gitHubService = RestAPIServiceBuilder.retrofit.create(RestAPIService.class);
            RestAPIService gitHubService = RestAPIServiceBuilder.CreatRetrofitForAPI().create(RestAPIService.class);
            Call<UserBook> call = gitHubService.modifyUserBook(this.sToken, this.oUserbook);
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

