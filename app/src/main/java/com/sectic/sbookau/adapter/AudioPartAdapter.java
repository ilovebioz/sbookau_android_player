package com.sectic.sbookau.adapter;

/**
 * Created by bioz on 9/20/2014.
 */

import android.app.Activity;
import android.os.Handler;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import android.text.SpannableString;
//import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.sectic.sbookau.R;
import com.sectic.sbookau.service.PostAudioPartToCloudService;
import com.sectic.sbookau.ultils.AuthSetting;
import com.sectic.sbookau.icallback.OnItemClickListener;
import com.sectic.sbookau.icallback.OnLoadMoreListener;
import com.sectic.sbookau.model.AudioPart;
import com.sectic.sbookau.service.DownloadFileFromCloudService;
import com.sectic.sbookau.service.PostMessageToCloudService;
import com.sectic.sbookau.ultils.DefSetting;
import com.sectic.sbookau.ultils.FileUtils;
import com.sectic.sbookau.ultils.RestAPIService;
import com.sectic.sbookau.ultils.RestAPIServiceBuilder;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AudioPartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = AudioPartAdapter.class.getSimpleName();

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private final List<AudioPart> lData;

    private OnItemClickListener mItemClickListener;
    private OnLoadMoreListener onLoadMoreListener;
    private LinearLayoutManager mLinearLayoutManager;

    private boolean bIsMoreLoading = false;
    private int iSelectedIdx = -1;

    private Activity mContext;
    private DownloadFileFromCloudService oLoadEngine;

    public AudioPartAdapter(Activity mContext)
    {
        this.mContext = mContext;
        lData = new ArrayList<>();
        this.setHasStableIds(true);
    }

    //// override functions
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_audio_part_list_item, viewGroup, false);
            return new BookAudioPartViewHolder(v);
        } else {
            View loadMoreView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progress_item, viewGroup, false);
            return new ProgressViewHolder(loadMoreView);
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BookAudioPartViewHolder) {
            holder.itemView.setSelected(iSelectedIdx == position);
            ((BookAudioPartViewHolder) holder).bindData(lData.get(position), position);
        }
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    @Override
    public int getItemCount() {
        return this.lData.size();
    }
    @Override
    public long getItemId(int position) {
        if(lData.size() <= position || lData.get(position) == null){
            return position;
        }
        return lData.get(position).sId.hashCode();
    }
    @Override
    public int getItemViewType(int position) {
        return (lData.get(position) != null) ? VIEW_ITEM: VIEW_PROG;
    }

    //// helps
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.onLoadMoreListener = loadMoreListener;
    }

    public void setLinearLayoutManager(LinearLayoutManager linearLayoutManager){
        this.mLinearLayoutManager=linearLayoutManager;
    }

    public boolean isNeedToLoadMore( int iISelectedIdx , int iTotalData){
        boolean bResult = false;
        if( (iISelectedIdx >= 0) && (lData.size() > 0) && (iISelectedIdx < (iTotalData - 1)) && (iISelectedIdx >= lData.size()) ){
            bResult = true;
        }
        return bResult;
    }

    public void setNewLength( int iINewLength ){
        if( iSelectedIdx >= 0 ){
            lData.get(iSelectedIdx).iLength = iINewLength;
            notifyItemChanged(iSelectedIdx);
        }
    }

    public void setSelectedItem( int iISelectedIdx ){
        if( (iISelectedIdx >= 0) && (lData.size() > 0) && (iISelectedIdx < lData.size()) ){
            int iPreSelectedIdx = iSelectedIdx;
            iSelectedIdx = iISelectedIdx;
            notifyItemChanged(iSelectedIdx);
            notifyItemChanged(iPreSelectedIdx);
        }
    }

    public AudioPart getSelectedItem(){
        if( (iSelectedIdx >= 0) && (lData.size() > 0) && (iSelectedIdx < lData.size()) ){
            return lData.get(iSelectedIdx);
        }else{
            return null;
        }
    }

    public List<AudioPart> getDataList(){
        return lData;
    }

    public void addAll(List<AudioPart> list) {
        lData.addAll(list);
        notifyItemRangeChanged(0, lData.size());
    }

    public void resetAll(List<AudioPart> list) {
        lData.clear();
        lData.addAll(list);
        notifyDataSetChanged();
    }

    public void add(int location, AudioPart iOItem){
        lData.add(location, iOItem);
        notifyItemInserted(location);
    }

    public void set(int location, AudioPart iOItem){
        lData.set(location, iOItem);
        notifyItemChanged(location);
    }

    public void clear(){
        lData.clear();
        notifyDataSetChanged();
    }

    public void setMoreLoading(boolean isMoreLoading) {
        this.bIsMoreLoading=isMoreLoading;
    }

    public void setProgressMore(final boolean isProgress) {
        if (isProgress) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    lData.add(null);
                    notifyItemInserted(lData.size() - 1);
                }
            });
        } else {
            lData.remove(lData.size() - 1);
            notifyItemRemoved(lData.size());
        }
    }

    public void setRecyclerView(RecyclerView mView){
        mView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int iVisibleItemCount = recyclerView.getChildCount();
                int iTotalItemCount = mLinearLayoutManager.getItemCount();
                int iFirstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

                if (!bIsMoreLoading && (iFirstVisibleItem + iVisibleItemCount) >= iTotalItemCount && (iFirstVisibleItem >= 0)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    bIsMoreLoading = true;
                }
            }
        });
    }

    //// sub Class
    public class BookAudioPartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_audio_part_id;
        TextView txt_audio_part_name;
        TextView txt_audio_part_format;
        TextView txt_audio_part_length;

        ImageButton btnDownload;
        ImageButton btnReport;
        ImageButton btnDelete;
        ImageButton btnUpdate;

        NumberProgressBar pBProgress;

        BookAudioPartViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            txt_audio_part_id = itemView.findViewById(R.id.txt_audio_part_list_item_id);
            txt_audio_part_name = itemView.findViewById(R.id.txt_audio_part_list_item_name);
            txt_audio_part_format = itemView.findViewById(R.id.txt_audio_part_list_item_format);
            txt_audio_part_length = itemView.findViewById(R.id.txt_audio_part_list_item_length);

            btnReport = itemView.findViewById(R.id.btnReport);
            btnDownload = itemView.findViewById(R.id.btnDownload);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);

            btnDownload.setFocusable(false);
            btnDownload.setFocusableInTouchMode(false);
            btnDelete.setFocusable(false);
            btnDelete.setFocusableInTouchMode(false);
            btnUpdate.setFocusable(false);
            btnUpdate.setFocusableInTouchMode(false);

            pBProgress = itemView.findViewById(R.id.pBProgress);

            btnDownload.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getLayoutPosition();
                    if(oLoadEngine!= null && oLoadEngine.bIsBusy){
                        Toasty.info(mContext, mContext.getString(R.string.s_res_mess_download_file_in_progress), Toast.LENGTH_SHORT, true).show();
                        return;
                    }

                    Toasty.info(mContext, mContext.getString(R.string.s_res_mess_download_file_start), Toast.LENGTH_SHORT, true).show();
                    try {
                        //RestAPIService gitHubService = RestAPIServiceBuilder.retrofitForDownload.create(RestAPIService.class);
                        RestAPIService gitHubService = RestAPIServiceBuilder.CreatRetrofitForDownload().create(RestAPIService.class);
                        Call<ResponseBody> call = gitHubService.downloadFileWithDynamicUrlAsync(lData.get(position).sUrl);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    oLoadEngine = new DownloadFileFromCloudService(String.format(DefSetting.gSMediaFilePattern,
                                                                                        lData.get(position).sId, lData.get(position).sFormat),
                                                                        AudioPartAdapter.this, position);
                                    oLoadEngine.execute(response.body());
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.d(TAG, t.getMessage());
                                Toasty.error(mContext, mContext.getString(R.string.s_res_mess_download_file_fail), Toast.LENGTH_SHORT, true).show();
                            }
                        });
                    } catch (Exception e) {
                        Toasty.error(mContext, mContext.getString(R.string.s_res_mess_download_file_fail), Toast.LENGTH_SHORT, true).show();
                        e.printStackTrace();
                    }
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getLayoutPosition();
                    if(oLoadEngine!= null && oLoadEngine.bIsBusy){
                        Toasty.info(mContext, mContext.getString(R.string.s_res_mess_system_busy), Toast.LENGTH_SHORT, true).show();
                        return;
                    }
                    FileUtils.DeleteLocalFile( mContext, lData.get(position).GetMediaFileName() );
                    Toasty.success(mContext, mContext.getString(R.string.s_res_mess_delete_file_success), Toast.LENGTH_SHORT, true).show();
                    AudioPartAdapter.this.notifyDataSetChanged();
                }
            });

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getLayoutPosition();
                    if(oLoadEngine!= null && oLoadEngine.bIsBusy){
                        Toasty.info(mContext, mContext.getString(R.string.s_res_mess_system_busy), Toast.LENGTH_SHORT, true).show();
                        return;
                    }

                    final PostAudioPartToCloudService oPostAudioPartToCloudService = new PostAudioPartToCloudService(AuthSetting.gToken, lData.get(position));
                    oPostAudioPartToCloudService.oITaskCompleted = new PostAudioPartToCloudService.OnTaskCompleted() {
                        @Override
                        public void onTaskCompleted(boolean bResult) {
                            if(!bResult){
                                Toasty.error(mContext, mContext.getString(R.string.s_res_mess_update_fail), Toast.LENGTH_SHORT, true).show();
                            }else{
                                Toasty.success(mContext, mContext.getString(R.string.s_res_mess_update_success), Toast.LENGTH_SHORT,true).show();
                            }
                        }
                    };
                    oPostAudioPartToCloudService.execute();
                }
            });

            btnReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getLayoutPosition();
                    if(oLoadEngine!= null && oLoadEngine.bIsBusy){
                        Toasty.info(mContext, mContext.getString(R.string.s_res_mess_system_busy), Toast.LENGTH_SHORT, true).show();
                        return;
                    }
                    final PostMessageToCloudService oPostMessageToCloudService = new PostMessageToCloudService(AuthSetting.gToken, lData.get(position).sId, lData.get(position).sName, "REPORT");
                    oPostMessageToCloudService.oITaskCompleted = new PostMessageToCloudService.OnTaskCompleted() {
                        @Override
                        public void onTaskCompleted(boolean bResult) {
                            if(!bResult){
                                Toasty.error(mContext, mContext.getString(R.string.s_res_mess_report_fail), Toast.LENGTH_SHORT, true).show();
                            }else{
                                Toasty.success(mContext, mContext.getString(R.string.s_res_mess_report_success), Toast.LENGTH_SHORT, true).show();
                            }
                        }
                    };
                    oPostMessageToCloudService.execute();
                }
            });
        }
        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getLayoutPosition());
                notifyItemChanged(iSelectedIdx);
                iSelectedIdx = getLayoutPosition();
                notifyItemChanged(iSelectedIdx);
            }
        }

        void bindData(AudioPart bookPart, int iOrder) {
            txt_audio_part_id.setText(String.valueOf(iOrder + 1) + ".");
            txt_audio_part_name.setText(bookPart.sName);
            txt_audio_part_format.setText(bookPart.sFormat);

            int iHour = 0;
            int iMinute = 0;
            int iSecond = 0;
            String sTime="";
            iHour = bookPart.iLength / (60 * 60);
            iMinute = ( bookPart.iLength - iHour * 60 * 60 ) / 60;
            iSecond =  bookPart.iLength - iHour * 60 * 60 - iMinute * 60;

            if (iHour != 0){
                sTime = String.format("%d ", iHour);
            }

            if (iMinute != 0){
                sTime += String.format("%d' ", iMinute);
            }

            if (iSecond != 0){
                sTime += String.format("%d\"", iSecond);
            }

            txt_audio_part_length.setText( sTime );

            String sFilePathInInternal = FileUtils.generateInternalFilePath( mContext, bookPart.GetMediaFileName());
            if( FileUtils.checkAFileAvailable(sFilePathInInternal) && bookPart.iDownloadProgress <= 0){
                btnDelete.setVisibility(View.VISIBLE);
            }else{
                btnDelete.setVisibility(View.GONE);
            }

            if( iOrder == iSelectedIdx && AuthSetting.gUserRight != null && AuthSetting.gUserRight.equals(DefSetting.USER_RIGHT_ADMIN)){
                btnUpdate.setVisibility(View.VISIBLE);
            }else{
                btnUpdate.setVisibility(View.GONE);
            }

            if(bookPart.iDownloadProgress <= 0){
                pBProgress.setVisibility(View.GONE);
            }else{
                pBProgress.setProgress(bookPart.iDownloadProgress);
                pBProgress.setVisibility(View.VISIBLE);
            }
        }
    }
}