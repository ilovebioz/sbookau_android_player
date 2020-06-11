package com.sectic.sbookau.service;


import android.os.AsyncTask;

import com.sectic.sbookau.model.Book;
import com.sectic.sbookau.ultils.RestAPIService;
import com.sectic.sbookau.ultils.RestAPIServiceBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import retrofit2.Call;

/**
 * Created by bioz on 9/18/2014.
 */
public class PostBookToCloudService extends AsyncTask<Void, Integer, Boolean > {

    private String sToken;
    private Book obook;

    public OnTaskCompleted oITaskCompleted;

    public interface OnTaskCompleted {
        void onTaskCompleted(boolean bResult);
    }

    public PostBookToCloudService(String sToken, Book oBook)
    {
        this.sToken = sToken;
        this.obook = oBook;
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
        Book oResult= null;
        try {
            //RestAPIService gitHubService = RestAPIServiceBuilder.retrofit.create(RestAPIService.class);
            RestAPIService gitHubService = RestAPIServiceBuilder.CreatRetrofitForAPI().create(RestAPIService.class);
            Call<Book> call = gitHubService.updateBooks(this.sToken, this.obook.sId, this.obook);
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

