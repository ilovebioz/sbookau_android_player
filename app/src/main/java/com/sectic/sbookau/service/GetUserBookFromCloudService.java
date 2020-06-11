package com.sectic.sbookau.service;


import android.os.AsyncTask;

import com.sectic.sbookau.handler.DataHandler;
import com.sectic.sbookau.model.UserBook;
import com.sectic.sbookau.ultils.AppProvider;
import com.sectic.sbookau.ultils.RestAPIService;
import com.sectic.sbookau.ultils.RestAPIServiceBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import retrofit2.Call;

/**
 * Created by bioz on 9/18/2014.
 */
public class GetUserBookFromCloudService extends AsyncTask<Void, Integer, Boolean > {

    private String sToken;
    private String sCreator;
    private String sBook;

    private UserBook oUserbook;

    public OnTaskCompleted oITaskCompleted;

    public interface OnTaskCompleted {
        void onTaskCompleted(boolean bResult, UserBook oUserBook);
    }

    public GetUserBookFromCloudService(String sToken, String sCreator, String sBook)
    {
        this.sToken = sToken;
        this.sCreator = sCreator;
        this.sBook = sBook;
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
            Call<UserBook> call = gitHubService.getByCreatorAndBook(this.sToken, this.sCreator, this.sBook);
            oUserbook = call.execute().body();
            DataHandler.saveUserBook( AppProvider.getContext(), oUserbook, DataHandler.genUserBookKey(this.sCreator, this.sBook) );

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(oUserbook == null){
                oUserbook = DataHandler.loadUserBook( AppProvider.getContext(), DataHandler.genUserBookKey(this.sCreator, this.sBook) );
            }
            if(oUserbook == null){
                return false;
            }else{
                return true;
            }
        }
    }
}

