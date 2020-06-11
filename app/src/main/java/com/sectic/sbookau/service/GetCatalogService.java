package com.sectic.sbookau.service;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.sectic.sbookau.handler.DataHandler;
import com.sectic.sbookau.ultils.AuthSetting;
import com.sectic.sbookau.model.Catalog;
import com.sectic.sbookau.model.CatalogList;
import com.sectic.sbookau.model.Filter;
import com.sectic.sbookau.ultils.AppProvider;
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
public class GetCatalogService extends AsyncTask<Void, Integer, Boolean > {

    private CatalogList oCatalogList;
    public OnTaskCompleted oITaskCompleted;

    public interface OnTaskCompleted {
        void onTaskCompleted(boolean bResult, CatalogList oCatalogList);
    }

    public GetCatalogService(){}

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
        oITaskCompleted.onTaskCompleted(result, this.oCatalogList);
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        String sFilter = "";
        try {
            List<Filter> aTag = new ArrayList<>();
            aTag.add( new Filter("tagType", "=", "BOOK_CATALOGUE") );
            Gson oGson = new Gson();
            sFilter = oGson.toJson(aTag);

            //RestAPIService gitHubService = RestAPIServiceBuilder.retrofit.create(RestAPIService.class);
            RestAPIService gitHubService = RestAPIServiceBuilder.CreatRetrofitForAPI().create(RestAPIService.class);

            Call<CatalogList> call1 = gitHubService.getTags(AuthSetting.gToken, sFilter);
            oCatalogList = call1.execute().body();
            DataHandler.saveCatalogList( AppProvider.getContext(), oCatalogList, DataHandler.genCatalogListKey(sFilter) );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(oCatalogList == null){
                oCatalogList = DataHandler.loadCatalogList( AppProvider.getContext(), DataHandler.genCatalogListKey(sFilter) );
            }

            if(oCatalogList == null){
                return false;
            }else{
                return true;
            }
        }
    }

    public void parseCatDataFromRestResult(List<Catalog> aLstCat)
    {
        try {
            for (int i = 0; i < oCatalogList.lOCatalog.size(); i++) {
                int iTotalBooks = oCatalogList.lOCatalog.get(i).refCount;
                if (iTotalBooks > 0) {
                    Catalog oCat = new Catalog((i + 1), oCatalogList.lOCatalog.get(i).id,
                            oCatalogList.lOCatalog.get(i).value,
                            oCatalogList.lOCatalog.get(i).tagType,
                            iTotalBooks,
                            oCatalogList.lOCatalog.get(i).status);
                    aLstCat.add(oCat);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

