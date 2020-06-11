package com.sectic.sbookau.service;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.sectic.sbookau.handler.DataHandler;
import com.sectic.sbookau.model.AudioPart;
import com.sectic.sbookau.model.AudioPartList;
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
public class GetAudioPartFromCloudService extends AsyncTask<Void, Integer, Boolean > {

    private String sToken;
    private String sFilter;
    private String sBookId;
    public AudioPartList oAudioPartList;
    private int iPage;


    public OnTaskCompleted oITaskCompleted;

    public interface OnTaskCompleted {
        void onTaskCompleted(boolean bResult, AudioPartList oBookPartList);
    }

    public GetAudioPartFromCloudService(String sToken, String sBookId, int iIPage)
    {
        try {
            this.sToken = sToken;
            this.sBookId = sBookId;
            List<Filter> aFilter = new ArrayList<>();
            aFilter.add(new Filter("book", "=", sBookId));
            Gson oGson = new Gson();
            this.sFilter = oGson.toJson(aFilter);
            this.iPage = iIPage;
        }catch (Exception e){
            e.printStackTrace();
        }
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
        oITaskCompleted.onTaskCompleted(result, this.oAudioPartList);
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        try {
            //RestAPIService gitHubService = RestAPIServiceBuilder.retrofit.create(RestAPIService.class);
            RestAPIService gitHubService = RestAPIServiceBuilder.CreatRetrofitForAPI().create(RestAPIService.class);
            Call<AudioPartList> call = gitHubService.getBookParts(sToken, sFilter, "partNum", iPage, DefSetting.gPageSize);
            oAudioPartList = call.execute().body();
            DataHandler.saveAudioPartList( AppProvider.getContext(), oAudioPartList, DataHandler.genAudioPartListKey(this.sFilter, String.valueOf(this.iPage), String.valueOf(DefSetting.gPageSize)) );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(oAudioPartList == null){
                oAudioPartList = DataHandler.loadAudioPartList( AppProvider.getContext(), DataHandler.genAudioPartListKey(this.sFilter, String.valueOf(this.iPage), String.valueOf(DefSetting.gPageSize)) );
            }
            if(oAudioPartList == null){
                return false;
            }else{
                return true;
            }
        }
    }

    public void parseBookPartDataFromRestResult(AudioPartList aLstBook)
    {
        try {
            aLstBook.lOBookPart.clear();
            for (int i = 0; i < oAudioPartList.lOBookPart.size(); i++) {
                AudioPart oTmp = oAudioPartList.lOBookPart.get(i);
                AudioPart oBookPart = new AudioPart(oTmp.sId, oTmp.sName, oTmp.sBookId, oTmp.sReader, oTmp.sUrl, oTmp.sStatus, oTmp.iLength, oTmp.sFormat, oTmp.iPartNum,oTmp.iDownloadCount, oTmp.iViewCount);
                aLstBook.lOBookPart.add(oBookPart);
            }
            aLstBook.oPages.bHasNext = oAudioPartList.oPages.bHasNext;
            aLstBook.oPages.bHasPrev = oAudioPartList.oPages.bHasPrev;
            aLstBook.oPages.iCurrent = oAudioPartList.oPages.iCurrent;
            aLstBook.oPages.iNext = oAudioPartList.oPages.iNext;
            aLstBook.oPages.iPrev = oAudioPartList.oPages.iPrev;
            aLstBook.oPages.iTotal = oAudioPartList.oPages.iTotal;

            aLstBook.oItems.iBegin = oAudioPartList.oItems.iBegin;
            aLstBook.oItems.iEnd = oAudioPartList.oItems.iEnd;
            aLstBook.oItems.iTotal = oAudioPartList.oItems.iTotal;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

