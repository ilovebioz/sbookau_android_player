package com.sectic.sbookau.service;

import android.os.AsyncTask;

import com.sectic.sbookau.model.Book;
import com.sectic.sbookau.model.BookList;
import com.sectic.sbookau.ultils.AppProvider;

import java.util.List;

/**
 * Created by bioz on 9/18/2014.
 */
public class GetBookHistoryFromFileService extends AsyncTask<Void, Integer, Boolean > {

    public BookList oBookList;

    public OnTaskCompleted oITaskCompleted;

    public interface OnTaskCompleted {
        void onTaskCompleted(boolean bResult, BookList oBookList);
    }

    public GetBookHistoryFromFileService(){}

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
        try{
            if(oBookList == null){
                oBookList = new BookList();
            }
            oBookList.loadBookHistoryFromInternalStorage(AppProvider.getContext());
            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void parseBookDataFromFileResult(List<Book> aLstBook)
    {
        try {
            aLstBook.clear();
            for (int i = 0; i < oBookList.lOBook.size(); i++) {
                Book oTmp = oBookList.lOBook.get(i);
                Book oBook = new Book(oTmp.sId, oTmp.sName, oTmp.iPartCount, oTmp.sAuthor, oTmp.sTranslator, oTmp.bIsRecommend, oTmp.sStatus, oTmp.iViewCount, oTmp.iLikeCount);
                aLstBook.add(oBook);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

