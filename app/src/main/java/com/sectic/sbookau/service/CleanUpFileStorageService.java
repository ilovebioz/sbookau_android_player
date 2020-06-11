package com.sectic.sbookau.service;

import android.os.AsyncTask;

import com.sectic.sbookau.ultils.AppProvider;
import com.sectic.sbookau.ultils.FileUtils;

import java.io.File;

/**
 * Created by bioz on 9/18/2014.
 */
public class CleanUpFileStorageService extends AsyncTask<Void, Integer, Boolean > {
    //private Activity oGui;

    public OnTaskCompleted oITaskCompleted;

    public interface OnTaskCompleted {
        void onTaskCompleted(boolean bResult);
    }

    public CleanUpFileStorageService(){}

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
            FileUtils.CleanAllFileInExternalStorage();
            String sInternal = AppProvider.getContext().getFilesDir().getAbsolutePath();
            File f = new File(sInternal);
            FileUtils.CleanAllMediaFileInInternalStorage(f);
            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
}

