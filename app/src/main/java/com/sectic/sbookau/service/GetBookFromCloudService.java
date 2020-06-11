package com.sectic.sbookau.service;


import android.os.AsyncTask;

import com.google.gson.Gson;
import com.sectic.sbookau.handler.DataHandler;
import com.sectic.sbookau.model.BookList;
import com.sectic.sbookau.model.Filter;
import com.sectic.sbookau.ultils.AppProvider;
import com.sectic.sbookau.ultils.DefSetting;
import com.sectic.sbookau.ultils.RestAPIService;
import com.sectic.sbookau.ultils.RestAPIServiceBuilder;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by bioz on 9/18/2014.
 */
public class GetBookFromCloudService extends AsyncTask<Void, Integer, Boolean > {

    private String sToken;
    private String sFilter;
    private String sQuery;
    private int iPage;
    public BookList oBookList;

    public OnTaskCompleted oITaskCompleted;

    public interface OnTaskCompleted {
        void onTaskCompleted(boolean bResult, BookList oBookList);
    }

    public GetBookFromCloudService(String sToken, String sTag, String sQuery, int iIPage)
    {
        this.sToken = sToken;

        List<Filter> aTag = new ArrayList<>();
        aTag.add( new Filter("tags", "=", sTag) );
        Gson oGson = new Gson();
        this.sFilter = oGson.toJson(aTag);
        this.sQuery = sQuery;
        this.iPage = iIPage;
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
        oITaskCompleted.onTaskCompleted(result, this.oBookList);
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        try {
            //RestAPIService gitHubService = RestAPIServiceBuilder.retrofit.create(RestAPIService.class);
            RestAPIService gitHubService = RestAPIServiceBuilder.CreatRetrofitForAPI().create(RestAPIService.class);
            Call<BookList> call = gitHubService.getBooks(this.sToken, this.sFilter, this.sQuery, this.iPage, DefSetting.gPageSize);
            oBookList = call.execute().body();
            DataHandler.saveBookList( AppProvider.getContext(), oBookList, DataHandler.genBookListKey(this.sFilter, this.sQuery, String.valueOf(this.iPage), String.valueOf(DefSetting.gPageSize)) );

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(oBookList == null){
                oBookList = DataHandler.loadBookList( AppProvider.getContext(), DataHandler.genBookListKey(this.sFilter, this.sQuery, String.valueOf(this.iPage), String.valueOf(DefSetting.gPageSize)) );
            }
            if(oBookList == null){
                return false;
            }else{
                return true;
            }
        }
    }
}

