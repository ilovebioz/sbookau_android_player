package com.sectic.sbookau.ultils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by bioz on 8/15/2017.
 */

public class FileUtils {
    public static String readJsonFromInternalFile (Context iOContext, String iSFileName){
        String sJson = null;
        FileInputStream oInStream = null;
        try {
            File file = iOContext.getFileStreamPath(iSFileName);
            if(file != null && file.exists()) {
                oInStream = iOContext.openFileInput(iSFileName);
                FileChannel oFileChannel = oInStream.getChannel();
                MappedByteBuffer oByteBuff = oFileChannel.map(FileChannel.MapMode.READ_ONLY, 0, oFileChannel.size());
                sJson = Charset.defaultCharset().decode(oByteBuff).toString();
                oInStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (oInStream != null) {
                    oInStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                return sJson;
            }
        }
    }

    public static boolean writeJsonToInternalFile(Context iOContext, String iSFileName, String iSData) {
        FileOutputStream oOutStream = null;
        boolean bResult = false;
        try {
            oOutStream=iOContext.openFileOutput(iSFileName, MODE_PRIVATE);
            byte[] contentInBytes = iSData.getBytes();
            oOutStream.write(contentInBytes);
            oOutStream.flush();
            oOutStream.close();
            bResult = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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

    public static boolean writeInputStreamToInternalFile(Context iOContext, String iSFileName, InputStream... iInStream) {
        InputStream inputStream = iInStream[0];
        FileOutputStream oOutStream = null;

        boolean bResult = false;
        try {
            oOutStream=iOContext.openFileOutput(iSFileName, MODE_PRIVATE);
            byte[] contentInBytes = new byte[1024];
            int iReadByte;
            while ((iReadByte = inputStream.read(contentInBytes)) != -1) {
                oOutStream.write(contentInBytes, 0, iReadByte);
            }
            oOutStream.flush();
            bResult = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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

    public static String generateInternalFilePath(Activity iOActivity, String iSFileName){
        return iOActivity.getFilesDir().getAbsolutePath() + "/" + iSFileName;
    }

    public static boolean checkAFileAvailable(String iSFilePath) {
        boolean bResult = false;
        try {
            File file = new File(iSFilePath);
            if(file.exists()){
                bResult = true;
            }else{
                bResult = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return bResult;
        }
    }

    public static boolean isPossibleToUseInternalStorage()
    {
        boolean bResult = true;
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        stat.restat(Environment.getDataDirectory().getPath());
        float mBAvailable = (long)stat.getFreeBytes() / (1024.f * 1024.f);
        if(mBAvailable < 1024){
            bResult = false;
        }
        return bResult;
    }

    public static void DeleteLocalFile(Activity iOActivity, String iSFileName){
        try {
            String sInternalPath = generateInternalFilePath(iOActivity, iSFileName);
            File oInternalFile = new File(sInternalPath);
            if (oInternalFile.exists()) {
                oInternalFile.delete();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void CleanAllMediaFileInInternalStorage(File fileOrDirectory){
        try {
            if (fileOrDirectory.isDirectory()) {
                for (File child : fileOrDirectory.listFiles()) {
                    if (child.getName().toLowerCase().contains(".mp3")
                        || child.getName().toLowerCase().contains(".wma")
                        || child.getName().toLowerCase().contains(".json")) {
                        CleanAllMediaFileInInternalStorage(child);
                    }
                }
            }
            fileOrDirectory.delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static String getJSON(String url) {
        HttpsURLConnection oHttpUrlConnection = null;
        try {
            URL oUrl = new URL(url);
            oHttpUrlConnection = (HttpsURLConnection) oUrl.openConnection();
            oHttpUrlConnection.setReadTimeout(5000);
            oHttpUrlConnection.setConnectTimeout(5000);
            oHttpUrlConnection.connect();
            
            BufferedReader oBufferReader = new BufferedReader(new InputStreamReader(oHttpUrlConnection.getInputStream()));
            StringBuilder sStringBuilder = new StringBuilder();
            String sLine;
            while ((sLine = oBufferReader.readLine()) != null) {
                sStringBuilder.append(sLine + "\n");
            }
            oBufferReader.close();
            return sStringBuilder.toString();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (oHttpUrlConnection != null) {
                try {
                    oHttpUrlConnection.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }
}
