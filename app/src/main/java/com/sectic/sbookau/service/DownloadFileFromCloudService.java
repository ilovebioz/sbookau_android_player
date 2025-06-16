package com.sectic.sbookau.service;

import android.os.AsyncTask;
import android.widget.Toast;

import com.sectic.sbookau.R;
import com.sectic.sbookau.adapter.AudioPartAdapter;
import com.sectic.sbookau.ultils.AppProvider;
import com.sectic.sbookau.ultils.FileUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by bioz on 9/18/2014.
 */
public class DownloadFileFromCloudService extends AsyncTask<ResponseBody, Integer, Boolean > {

    public AudioPartAdapter oBookPartAdapter;
    public String sFileName;
    public int iPos;

    public boolean bIsBusy;

    public DownloadFileFromCloudService(String sFileName, AudioPartAdapter oBookPartAdapter, int iIPos){
        this.sFileName = sFileName;
        this.oBookPartAdapter = oBookPartAdapter;
        this.bIsBusy = true;
        this.iPos = iIPos;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        oBookPartAdapter.getDataList().get(iPos).iDownloadProgress = values[0];
        oBookPartAdapter.notifyItemChanged(iPos);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        this.bIsBusy = false;
        if(!result){
            Toasty.error(AppProvider.getContext(), AppProvider.getContext().getString(R.string.s_res_mess_download_file_fail), Toast.LENGTH_SHORT, true).show();
        }else{
            Toasty.success(AppProvider.getContext(), AppProvider.getContext().getString(R.string.s_res_mess_download_file_success), Toast.LENGTH_SHORT, true).show();
        }
        oBookPartAdapter.getDataList().get(iPos).iDownloadProgress = -1;
        oBookPartAdapter.notifyDataSetChanged();
    }

    @Override
    protected Boolean doInBackground(ResponseBody ... params)
    {
        this.bIsBusy = true;
        Boolean bResult = false;
        InputStream inputStream;
        FileOutputStream oOutStream = null;
        try {
            inputStream = params[0].byteStream();
            byte[] contentInBytes = new byte[2048];

            long lFileSize = params[0].contentLength();
            float fPercentPart = lFileSize / 100;
            int iPrePercent = 0;
            int iCurPercent = 0;

            long lFileSizeDownloaded = 0;
            int iReadByte;

            if(FileUtils.isPossibleToUseInternalStorage()){
                oOutStream = AppProvider.getContext().openFileOutput(sFileName, MODE_PRIVATE);

                while (true) {
                    iReadByte = inputStream.read(contentInBytes);
                    if( iReadByte == -1 || lFileSizeDownloaded >= lFileSize){
                        if(lFileSizeDownloaded == lFileSize){
                            bResult = true;
                        }
                        break;
                    }
                    oOutStream.write(contentInBytes, 0, iReadByte);
                    lFileSizeDownloaded += iReadByte;

                    iCurPercent = (int)((float)lFileSizeDownloaded / fPercentPart);
                    if(iCurPercent > iPrePercent){
                        iPrePercent = iCurPercent;
                        publishProgress(iCurPercent);
                    }
                }
                oOutStream.flush();
            }
            return bResult;
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception ex) {
            ex.printStackTrace();
        }finally {
            try {
                if (oOutStream != null) {
                    oOutStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                return bResult;
            }
        }
    }
}

