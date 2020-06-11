package com.sectic.sbookau;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import es.dmoral.toasty.Toasty;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.ybq.android.spinkit.SpinKitView;
import com.sectic.sbookau.adapter.AudioPartAdapter;
import com.sectic.sbookau.model.UserBook;
import com.sectic.sbookau.service.GetUserBookFromCloudService;
import com.sectic.sbookau.service.PostUserBookToCloudService;
import com.sectic.sbookau.ultils.AuthSetting;
import com.sectic.sbookau.icallback.OnItemClickListener;
import com.sectic.sbookau.icallback.OnLoadMoreListener;
import com.sectic.sbookau.handler.PlayingInfoHandler;
import com.sectic.sbookau.handler.PlaySettingHandler;
import com.sectic.sbookau.model.AudioPartList;
import com.sectic.sbookau.service.CleanUpFileStorageService;
import com.sectic.sbookau.service.GetAudioPartFromCloudService;
import com.sectic.sbookau.ultils.BaseUtils;
import com.sectic.sbookau.ultils.DefSetting;
import com.sectic.sbookau.ultils.FileUtils;


public class PlayingListActivity extends Activity implements OnClickListener, AudioManager.OnAudioFocusChangeListener {
    public static final String TAG = PlayingListActivity.class.getSimpleName();
    private boolean mAudioFocusChange = false;


    private AudioManager mAudioManager;

    // play controller
    private SeekBar skb_seek;
    private ImageButton btn_play;
    private ImageButton tbnPlayPlan;
    private ImageButton btn_prev;
    private ImageButton btn_next;
    private ImageButton tbnRepeat;

    private TextView txt_cur_pos;
    private TextView txt_total_length;
    SpinKitView pbr_loading_status;
    private MediaPlayer oMediaPlayer;
    private boolean bIsAutoStartPlay = false;
    private int iAudioPartLength;

    // data list
    private AudioPartList oAudioPartList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AudioPartAdapter audioPartAdapter;
    private UserBook oUserBook;

    // play setting and info
    private PlayingInfoHandler oPlayingInfoHandler;
    private PlaySettingHandler oPlaySettingHandler;

    private Handler hdl_seek = new Handler();

    Runnable run = new Runnable() {
        @Override
        public void run() {
            seekUpdThr();
        }
    };

    public void seekUpdThr() {
        int iValue = 0;
        if(oMediaPlayer != null )
        {
            if(oMediaPlayer.isPlaying())
            {
                iValue = oMediaPlayer.getCurrentPosition();
                skb_seek.setProgress(iValue);
                oPlayingInfoHandler.playingInfo.setCurPlayingPos(iValue);
                txt_cur_pos.setText(BaseUtils.convertInt2Time(iValue / 1000));

                if(oPlaySettingHandler.isPlayPeriodFromCur())
                {
                    if(oPlaySettingHandler.countDownAndCheckEnd())
                    {
                        actPlayOrPauseMedia(false, getString(R.string.s_res_pause));
                        oPlaySettingHandler.startCounterValue();
                    }
                }
                btn_play.setImageResource(R.drawable.btn_player_pause_selector);
            }else{
                btn_play.setImageResource(R.drawable.btn_player_play_selector);
            }

            if(oPlayingInfoHandler.getPrePart() < 0) {
                btn_prev.setEnabled(false);
            }else{
                btn_prev.setEnabled(true);
            }

            if( oPlayingInfoHandler.getNextPart() < 0 )
            {
                btn_next.setEnabled(false);
            }else{
                btn_next.setEnabled(true);
            }
        }else{
            btn_play.setImageResource(R.drawable.btn_player_play_selector);
        }
        hdl_seek.postDelayed(run, 1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_audio_part_list);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        int iLoadPref = 0;
        Intent oIntent = getIntent();
        iLoadPref = oIntent.getIntExtra("bLoadPref", 0);

        oPlaySettingHandler = new PlaySettingHandler(this, 0, 0, false, false);
        // replay the previous listening position
        if(iLoadPref != 0) {
            oPlayingInfoHandler =  new PlayingInfoHandler(this);
            // play as current selection
        }else{
            oPlayingInfoHandler =  new PlayingInfoHandler();
            oPlayingInfoHandler.playingInfo.setBookName(oIntent.getStringExtra("name"));
            oPlayingInfoHandler.playingInfo.setBookId(oIntent.getStringExtra("id"));
        }

        if(oUserBook == null){
            oUserBook = new UserBook();
        }

        initViews();

        // start the checking thread which run in silent
        seekUpdThr();
        fetchDataAsync(1, true);
        loadUserBook();
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onCreate");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onResume");
        }
    }

    private void loadUserBook(){
        final GetUserBookFromCloudService oGetUserBookFromCloudService = new GetUserBookFromCloudService(AuthSetting.gToken, AuthSetting.gUserId, oPlayingInfoHandler.playingInfo.getBookId());
        oGetUserBookFromCloudService.oITaskCompleted = new GetUserBookFromCloudService.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean bResult, UserBook oResult) {
                if(bResult){
                    oUserBook.Clone(oResult);
                    if(oUserBook.bIsBookmarkAudio){
                        FloatingActionButton fabBookmark = findViewById(R.id.fabBookmark);
                        fabBookmark.setColorNormal(getResources().getColor(R.color.pink));
                    }
                    if(oUserBook.bIsLikeAudio){
                        FloatingActionButton fabLike = findViewById(R.id.fabLike);
                        fabLike.setColorNormal(getResources().getColor(R.color.pink));
                    }
                    if(oUserBook.bIsReportAudio){
                        FloatingActionButton fabReport = findViewById(R.id.fabReport);
                        fabReport.setColorNormal(getResources().getColor(R.color.pink));
                    }
                }else{
                    Toasty.error(getBaseContext(),getString(R.string.s_res_mess_can_not_get_book_from_server),Toast.LENGTH_SHORT, true).show();
                }
            }
        };
        oGetUserBookFromCloudService.execute();

        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "loadUserBook");
        }
    }

    private void updateUserBook(final UserBook iONewUserBook){
        final PostUserBookToCloudService oPostUserBookFromCloudService = new PostUserBookToCloudService(AuthSetting.gToken, iONewUserBook);
        oPostUserBookFromCloudService.oITaskCompleted = new PostUserBookToCloudService.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean bResult, UserBook oResult) {
                if(bResult){
                    oUserBook.Clone(iONewUserBook);
                    FloatingActionButton fabBookmark = findViewById(R.id.fabBookmark);
                    if(oUserBook.bIsBookmarkAudio){
                        fabBookmark.setColorNormal(getResources().getColor(R.color.pink));
                    }else{
                        fabBookmark.setColorNormal(getResources().getColor(R.color.half_black));
                    }

                    FloatingActionButton fabLike = findViewById(R.id.fabLike);
                    if(oUserBook.bIsLikeAudio){
                        fabLike.setColorNormal(getResources().getColor(R.color.pink));
                    }else{
                        fabLike.setColorNormal(getResources().getColor(R.color.half_black));
                    }

                    FloatingActionButton fabReport = findViewById(R.id.fabReport);
                    if(oUserBook.bIsReportAudio){
                        fabReport.setColorNormal(getResources().getColor(R.color.pink));
                    }else{
                        fabReport.setColorNormal(getResources().getColor(R.color.half_black));
                    }
                }else{
                    Toasty.error(getBaseContext(),getString(R.string.s_res_mess_update_fail),Toast.LENGTH_SHORT, true).show();
                }
            }
        };
        oPostUserBookFromCloudService.execute();

        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "updateUserBook");
        }
    }

    private void fetchDataAsync(int iIPage, final boolean needResetPlay){
        if(iIPage == 0) {
            if(oAudioPartList != null && oAudioPartList.isHasNext()){
                iIPage = oAudioPartList.oPages.iNext;
            }else {
                return;
            }
        }
        audioPartAdapter.setProgressMore(true);
        audioPartAdapter.setMoreLoading(true);

        final GetAudioPartFromCloudService oGetBookPartFromCloudService = new GetAudioPartFromCloudService(AuthSetting.gToken, oPlayingInfoHandler.playingInfo.getBookId(), iIPage);
        oGetBookPartFromCloudService.oITaskCompleted = new GetAudioPartFromCloudService.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean bResult, AudioPartList oResults) {
                audioPartAdapter.setProgressMore(false);
                if(bResult){
                    oGetBookPartFromCloudService.parseBookPartDataFromRestResult(oAudioPartList);
                    oPlayingInfoHandler.playingInfo.setBookTotalPart(oAudioPartList.oItems.iTotal);
                    if (oAudioPartList.isHasPre()) {
                        audioPartAdapter.addAll(oAudioPartList.lOBookPart);
                    } else {
                        audioPartAdapter.resetAll(oAudioPartList.lOBookPart);
                    }
                }else{
                    Toasty.error(getBaseContext(),getString(R.string.s_res_mess_can_not_get_book_from_server),Toast.LENGTH_SHORT, true).show();
                }
                audioPartAdapter.setMoreLoading(false);
                mSwipeRefreshLayout.setRefreshing(false);

                updateDataToGUI(needResetPlay);
                oPlaySettingHandler.setLoadingBookPart(false);
            }
        };
        oGetBookPartFromCloudService.execute();
        oPlaySettingHandler.setLoadingBookPart(true);

        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "fetchDataAsync");
        }
    }

    private void initViews() {

        RelativeLayout rltActionMenu = findViewById(R.id.rltActionMenu);

        FloatingActionButton fabBookmark = findViewById(R.id.fabBookmark);
        fabBookmark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UserBook oTmp = new UserBook();
                oTmp.Clone(oUserBook);
                oTmp.bIsBookmarkAudio = !oTmp.bIsBookmarkAudio;
                updateUserBook(oTmp);
            }
        });
        FloatingActionButton fabLike = findViewById(R.id.fabLike);
        fabLike.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UserBook oTmp = new UserBook();
                oTmp.Clone(oUserBook);
                oTmp.bIsLikeAudio = !oTmp.bIsLikeAudio;
                updateUserBook(oTmp);
            }
        });
        FloatingActionButton fabReport = findViewById(R.id.fabReport);
        fabReport.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UserBook oTmp = new UserBook();
                oTmp.Clone(oUserBook);
                oTmp.bIsReportAudio = !oTmp.bIsReportAudio;
                updateUserBook(oTmp);
            }
        });

        if( AuthSetting.IsNotAnonymous() ){
            rltActionMenu.setVisibility(View.VISIBLE);
        }else{
            rltActionMenu.setVisibility(View.INVISIBLE);
        }

        // list book audio part data
        oAudioPartList = new AudioPartList();
        audioPartAdapter = new AudioPartAdapter(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvAudioPartList);

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        audioPartAdapter.setLinearLayoutManager(mLayoutManager);
        audioPartAdapter.setRecyclerView(recyclerView);
        recyclerView.setAdapter(audioPartAdapter);
        audioPartAdapter.setLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                fetchDataAsync(0, false);
            }
        });
        audioPartAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(oPlaySettingHandler.iSSystemBusy())
                {
                    Toasty.warning(getApplicationContext(), getString(R.string.s_res_mess_system_busy), Toast.LENGTH_SHORT, true).show();
                    return;
                }

                oPlaySettingHandler.setIStartPartIndex(position);
                oPlaySettingHandler.startCounterValue();
                bIsAutoStartPlay = true;
                oPlayingInfoHandler.playingInfo.setCurPlayingPos(0);
                selBookPartAndPrep2Play(position);
            }
        });
        mSwipeRefreshLayout = findViewById(R.id.srlAudioPartList);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                fetchDataAsync(1, true);
            }
        });

        // play controller
        skb_seek = findViewById(R.id.skb_player_seek);
        btn_play = findViewById(R.id.btn_player_play);
        tbnPlayPlan = findViewById(R.id.tbn_player_is_plan_setting);
        btn_prev = findViewById(R.id.btn_player_pre_part);
        btn_next = findViewById(R.id.btn_player_next_part);
        pbr_loading_status = findViewById(R.id.pbr_audio_part_loading_status);
        tbnRepeat = findViewById(R.id.tbn_player_is_repeat);

        txt_cur_pos = findViewById(R.id.txt_player_cur_pos);
        txt_total_length = findViewById(R.id.txt_player_total_length);

        btn_play.setOnClickListener(this);
        btn_prev.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        tbnPlayPlan.setOnClickListener(this);
        tbnRepeat.setOnClickListener(this);

        if(!oPlaySettingHandler.isPlayWithoutPlan())
        {
            tbnPlayPlan.setImageResource(R.drawable.ic_btn_plan_on);
        }

        if(!oPlaySettingHandler.isValidModeForRepeat()) {
            tbnRepeat.setVisibility(View.GONE);
        }else{
            tbnRepeat.setVisibility(View.VISIBLE);
            if(oPlaySettingHandler.playSetting.getRepeat()) {
                tbnRepeat.setImageResource(R.drawable.baseline_repeat_white_24);
            } else{
                tbnRepeat.setImageResource(R.drawable.baseline_repeat_black_24);
            }
        }

        oMediaPlayer = new MediaPlayer();
        oMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        skb_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar sbrSeekBar, int iIProgresValue, boolean iBFromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar sbrSeekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar sbrSeekBar) {
                if(oPlaySettingHandler.iSSystemBusy())
                {
                    Toasty.warning(getApplicationContext(), getString(R.string.s_res_mess_system_busy), Toast.LENGTH_SHORT, true).show();
                    return;
                }

                if(oMediaPlayer != null && oMediaPlayer.isPlaying()) {
                    oMediaPlayer.pause();
                    bIsAutoStartPlay = true;
                    oMediaPlayer.seekTo(sbrSeekBar.getProgress());
                    pbr_loading_status.setVisibility(View.VISIBLE);
                    oPlaySettingHandler.setAudioSeeking(true);
                }
            }
        });

        oMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                oPlaySettingHandler.setAudioSeeking(false);
                oPlaySettingHandler.setAudioPreparing(false);
                pbr_loading_status.setVisibility(View.INVISIBLE);
                Toasty.warning(getApplicationContext(), getString(R.string.s_res_mess_system_busy), Toast.LENGTH_SHORT, true).show();
                return true;
            }
        });

        oMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mediaPlayer) {
                pbr_loading_status.setVisibility(View.INVISIBLE);
                oPlaySettingHandler.setAudioSeeking(false);
                if(bIsAutoStartPlay) {
                    bIsAutoStartPlay = false;
                    actPlayOrPauseMedia(true, getString(R.string.s_res_ready_play));
                }
            }
        });

        oMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer iOMediaPlayer) {
                iAudioPartLength = oMediaPlayer.getDuration();
                audioPartAdapter.setNewLength(iAudioPartLength / 1000);

                txt_total_length.setText(BaseUtils.convertInt2Time(audioPartAdapter.getSelectedItem().iLength));
                skb_seek.setMax(iAudioPartLength);
                oPlaySettingHandler.setAudioPreparing(false);
                oMediaPlayer.seekTo(oPlayingInfoHandler.playingInfo.getCurPlayingPos());
                oPlaySettingHandler.setAudioSeeking(true);
            }
        });
        oMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            public void onCompletion(MediaPlayer oMP){
                // the unit is millisecond
                if(oPlaySettingHandler.isPlayWithoutPlan() && oPlaySettingHandler.playSetting.getRepeat())
                {
                    oPlayingInfoHandler.playingInfo.setCurPlayingPos(0);
                    bIsAutoStartPlay = true;
                    selBookPartAndPrep2Play(oPlayingInfoHandler.playingInfo.getCurPartIndex());
                    return;
                }
                int iNextPartIndex = oPlayingInfoHandler.getNextPart();
                if(iNextPartIndex <= 0)
                {
                    if(oPlaySettingHandler.isPlayUntilEndPart() && oPlaySettingHandler.playSetting.getRepeat())
                    {
                        oPlayingInfoHandler.playingInfo.setCurPlayingPos(0);
                        bIsAutoStartPlay = true;
                        selBookPartAndPrep2Play(0);
                    }
                    return;
                }
                oPlayingInfoHandler.playingInfo.setCurPlayingPos(0);

                if(oPlaySettingHandler.isPlayUntilEndPart())
                {
                    bIsAutoStartPlay = true;
                    selBookPartAndPrep2Play(iNextPartIndex);
                }else if(oPlaySettingHandler.isPlayNPartFromCur())
                {
                    if(iNextPartIndex <= oPlaySettingHandler.getIEndPartIndex())
                    {
                        bIsAutoStartPlay = true;
                        selBookPartAndPrep2Play(iNextPartIndex);
                    }
                }else if(oPlaySettingHandler.isPlayPeriodFromCur()){
                    if(!oPlaySettingHandler.countDownAndCheckEnd())
                    {
                        bIsAutoStartPlay = true;
                        selBookPartAndPrep2Play(iNextPartIndex);
                    }
                }
            }
        });

        try {
            String sBookName = oPlayingInfoHandler.playingInfo.getBookName();
            if( (sBookName == null) || (sBookName.trim().length() <= 0) ){
                sBookName = getString(R.string.s_res_unavailable_book);
            }
            getActionBar().setTitle(sBookName);
            // just only support from API 14
            getActionBar().setHomeButtonEnabled(true);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "initViews");
        }
    }

    private void savePrefSetting(boolean iIsSavePlaySettingOnly)
    {
        if(!iIsSavePlaySettingOnly) {
            oPlayingInfoHandler.SavePlayingInfo(this);
        }
        oPlaySettingHandler.SavePlayingSetting(this);
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "savePrefSetting");
        }
    }

    private void updateDataToGUI(final boolean needResetPlay)
    {
        try {
            if (audioPartAdapter.getDataList().size() > 0) {

                this.findViewById(R.id.img_audio_part_empty).setVisibility(View.INVISIBLE);
                this.findViewById(R.id.famUserBook).setVisibility(View.VISIBLE);
                this.findViewById(R.id.incController).setVisibility(View.VISIBLE);

                // play the from last time position - step to listening page
                if (oPlayingInfoHandler.playingInfo.getCurPartIndex() >= 0 && needResetPlay) {
                    oPlaySettingHandler.setIStartPartIndex(oPlayingInfoHandler.playingInfo.getCurPartIndex());
                    oPlaySettingHandler.startCounterValue();
                    bIsAutoStartPlay = true;
                    selBookPartAndPrep2Play(oPlayingInfoHandler.playingInfo.getCurPartIndex());
                }
            } else {
                this.findViewById(R.id.img_audio_part_empty).setVisibility(View.VISIBLE);
                this.findViewById(R.id.famUserBook).setVisibility(View.INVISIBLE);
                this.findViewById(R.id.incController).setVisibility(View.INVISIBLE);
            }

            if (!oPlaySettingHandler.isValidModeForRepeat()) {
                tbnRepeat.setVisibility(View.GONE);
            } else {
                tbnRepeat.setVisibility(View.VISIBLE);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "updateDataToGUI");
        }
    }

    private void selBookPartAndPrep2Play(int iIBookPartIndex)
    {
        try {
            if (audioPartAdapter.isNeedToLoadMore(iIBookPartIndex, oPlayingInfoHandler.playingInfo.getBookTotalPart())) {
                fetchDataAsync(0, true);
                return;
            }

            audioPartAdapter.setSelectedItem(iIBookPartIndex);

            oPlayingInfoHandler.playingInfo.setCurPartIndex(iIBookPartIndex);
            oPlayingInfoHandler.playingInfo.setBookPartUrl(audioPartAdapter.getDataList().get(oPlayingInfoHandler.playingInfo.getCurPartIndex()).sUrl);

            String sFileLocalPath = FileUtils.generateInternalFilePath(this, audioPartAdapter.getDataList().get(oPlayingInfoHandler.playingInfo.getCurPartIndex()).GetMediaFileName());
            oPlayingInfoHandler.playingInfo.setBookPartFileInInternal(sFileLocalPath);

            sFileLocalPath = FileUtils.generateExternalFilePath(audioPartAdapter.getDataList().get(oPlayingInfoHandler.playingInfo.getCurPartIndex()).GetMediaFileName(), false);
            oPlayingInfoHandler.playingInfo.setBookPartFileInExternal(sFileLocalPath);

            if (FileUtils.checkAFileAvailable(oPlayingInfoHandler.playingInfo.getBookPartFileInInternal())) {
                createMediaPlayer(oPlayingInfoHandler.playingInfo.getBookPartFileInInternal());
            } else if (FileUtils.checkAFileAvailable(oPlayingInfoHandler.playingInfo.getBookPartFileInExternal())) {
                createMediaPlayer(oPlayingInfoHandler.playingInfo.getBookPartFileInExternal());
            } else {
                createMediaPlayer(oPlayingInfoHandler.playingInfo.getBookPartUrl());
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "selBookPartAndPrep2Play");
        }
    }

    private void actPlayOrPauseMedia(boolean iBIsPlay, String iSStatus)
    {
        if(oMediaPlayer != null && oPlayingInfoHandler.playingInfo.getCurPartIndex() >= 0) {
            if(iBIsPlay) {
                oMediaPlayer.start();
            }else{
                oMediaPlayer.pause();
            }

            oPlayingInfoHandler.playingInfo.setCurPlayingPos( oMediaPlayer.getCurrentPosition() );
            txt_cur_pos.setText(BaseUtils.convertInt2Time(oPlayingInfoHandler.playingInfo.getCurPlayingPos() / 1000));
            savePrefSetting(false);
            Toasty.info(getApplicationContext(), iSStatus, Toast.LENGTH_SHORT, true).show();
        }

        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "actPlayOrPauseMedia");
        }
    }

    private void createMediaPlayer(String iSPartFileName) {
        try {
            if(oMediaPlayer != null)
            {
                oMediaPlayer.pause();
                oMediaPlayer.reset();
            }
            oMediaPlayer.setDataSource( this, Uri.parse(iSPartFileName) );
            oMediaPlayer.prepareAsync();
            pbr_loading_status.setVisibility(View.VISIBLE);
            oPlaySettingHandler.setAudioPreparing(true);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "createMediaPlayer");
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if(focusChange <= 0 && oMediaPlayer != null && oMediaPlayer.isPlaying()) {
            //LOSS -> PAUSE
            mAudioFocusChange = true;
            actPlayOrPauseMedia(false, getString(R.string.s_res_pause));
        } else {
            //GAIN -> PLAY
            if(mAudioFocusChange){
                mAudioFocusChange = false;
                actPlayOrPauseMedia(true, getString(R.string.s_res_play));
            }
        }

        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onAudioFocusChange");
        }
    }

    @Override
    public void onClick(View view) {
        if(oPlaySettingHandler.iSSystemBusy())
        {
            Toasty.warning(getApplicationContext(), getString(R.string.s_res_mess_system_busy), Toast.LENGTH_SHORT, true).show();
            if(!oPlaySettingHandler.isPlayWithoutPlan())
            {
                tbnPlayPlan.setImageResource(R.drawable.ic_btn_plan_on);
            }
            return;
        }
        switch (view.getId()) {
            case R.id.tbn_player_is_repeat:
                oPlaySettingHandler.playSetting.setRepeat(!oPlaySettingHandler.playSetting.getRepeat());
                savePrefSetting(true);
                if(oPlaySettingHandler.playSetting.getRepeat()) {
                    tbnRepeat.setImageResource(R.drawable.baseline_repeat_white_24);
                }else{
                    tbnRepeat.setImageResource(R.drawable.baseline_repeat_black_24);
                }
                break;
            case R.id.btn_player_next_part:
                int iNextPartIndex = oPlayingInfoHandler.getNextPart();
                if(iNextPartIndex < 0)
                {
                    break;
                }
                bIsAutoStartPlay = true;
                oPlayingInfoHandler.playingInfo.setCurPlayingPos(0);
                selBookPartAndPrep2Play(iNextPartIndex);
                break;
            case R.id.btn_player_pre_part:
                int iPrePartIndex = oPlayingInfoHandler.getPrePart();
                if(iPrePartIndex < 0)
                {
                    break;
                }
                bIsAutoStartPlay = true;
                oPlayingInfoHandler.playingInfo.setCurPlayingPos(0);
                selBookPartAndPrep2Play(iPrePartIndex);
                break;
            case R.id.btn_player_play:
                if(oMediaPlayer != null)
                {
                    if(oMediaPlayer.isPlaying())
                    {
                        actPlayOrPauseMedia(false, getString(R.string.s_res_pause));
                    }else{
                        actPlayOrPauseMedia(true, getString(R.string.s_res_play));
                    }
                }
                break;

            case R.id.tbn_player_is_plan_setting:
                AlertDialog.Builder oDialogBuild = new AlertDialog.Builder(this);
                oDialogBuild.setTitle(getString(R.string.s_res_play_setting_set_schedule));
                oDialogBuild.setIcon(R.drawable.ic_btn_plan_off);

                final LayoutInflater factory = getLayoutInflater();
                final View oPlaySettingView = factory.inflate(R.layout.alertdialog_play_setting, null);

                final RadioGroup chg_play_setting = oPlaySettingView.findViewById(R.id.grp_play_setting_mode);
                final RadioButton chk_play_setting_only_selected = oPlaySettingView.findViewById(R.id.chk_play_setting_only_selected);
                final RadioButton chk_play_setting_until_end_part = oPlaySettingView.findViewById(R.id.chk_play_setting_to_end_list);
                final RadioButton chk_play_setting_n_part_from_cur = oPlaySettingView.findViewById(R.id.chk_play_setting_num_of_part);
                final RadioButton chk_play_setting_period_from_cur = oPlaySettingView.findViewById(R.id.chk_play_setting_period);

                final LinearLayout lltPart = oPlaySettingView.findViewById(R.id.lltParts);
                final LinearLayout lltTime = oPlaySettingView.findViewById(R.id.lltTime);
                final NumberPicker pkr_play_setting_n_part = oPlaySettingView.findViewById(R.id.pkr_play_setting_n_part);
                final NumberPicker tpk_play_setting_stop_time = oPlaySettingView.findViewById(R.id.pkr_play_setting_end_time);

                final TextView txt_play_setting_n_part_from_cur = oPlaySettingView.findViewById(R.id.txt_play_setting_n_part);
                final TextView txt_play_setting_period_from_cur = oPlaySettingView.findViewById(R.id.txt_play_setting_end_time);

                chg_play_setting.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener(){
                    @Override
                    public void onCheckedChanged (RadioGroup group, int checkedId){
                        if (checkedId == R.id.chk_play_setting_num_of_part) {
                            pkr_play_setting_n_part.setEnabled(true);
                            txt_play_setting_n_part_from_cur.setEnabled(true);
                            lltPart.setVisibility(View.VISIBLE);

                            tpk_play_setting_stop_time.setEnabled(false);
                            txt_play_setting_period_from_cur.setEnabled(false);
                            lltTime.setVisibility(View.GONE);
                        } else if (checkedId == R.id.chk_play_setting_period) {
                            pkr_play_setting_n_part.setEnabled(false);
                            txt_play_setting_n_part_from_cur.setEnabled(false);
                            lltPart.setVisibility(View.GONE);

                            tpk_play_setting_stop_time.setEnabled(true);
                            txt_play_setting_period_from_cur.setEnabled(true);
                            lltTime.setVisibility(View.VISIBLE);
                        }else{
                            pkr_play_setting_n_part.setEnabled(false);
                            txt_play_setting_n_part_from_cur.setEnabled(false);
                            lltPart.setVisibility(View.GONE);

                            tpk_play_setting_stop_time.setEnabled(false);
                            txt_play_setting_period_from_cur.setEnabled(false);
                            lltTime.setVisibility(View.GONE);
                        }
                    }
                });

                chk_play_setting_only_selected.setChecked(oPlaySettingHandler.isPlayWithoutPlan());
                chk_play_setting_n_part_from_cur.setChecked(oPlaySettingHandler.isPlayNPartFromCur());
                chk_play_setting_n_part_from_cur.setText(String.format(getString(R.string.s_res_play_setting_num_of_part), oPlaySettingHandler.playSetting.getParts()));
                chk_play_setting_until_end_part.setChecked(oPlaySettingHandler.isPlayUntilEndPart());
                chk_play_setting_period_from_cur.setChecked(oPlaySettingHandler.isPlayPeriodFromCur());
                chk_play_setting_period_from_cur.setText(String.format(getString(R.string.s_res_play_setting_period), oPlaySettingHandler.playSetting.getHours(), oPlaySettingHandler.playSetting.getMinutes()));

                pkr_play_setting_n_part.setMaxValue(oPlayingInfoHandler.getMaxNPart());
                pkr_play_setting_n_part.setMinValue(1);
                pkr_play_setting_n_part.setValue(oPlaySettingHandler.playSetting.getParts());
                pkr_play_setting_n_part.setWrapSelectorWheel(true);

                pkr_play_setting_n_part.setOnValueChangedListener( new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                        chk_play_setting_n_part_from_cur.setText(String.format(getString(R.string.s_res_play_setting_num_of_part), newVal));
                    }
                });

                tpk_play_setting_stop_time.setMaxValue(1440);
                tpk_play_setting_stop_time.setMinValue(1);
                tpk_play_setting_stop_time.setValue(oPlaySettingHandler.playSetting.getHours() * 60 + oPlaySettingHandler.playSetting.getMinutes());
                tpk_play_setting_stop_time.setWrapSelectorWheel(true);

                tpk_play_setting_stop_time.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                        chk_play_setting_period_from_cur.setText(String.format(getString(R.string.s_res_play_setting_period), (newVal / 60), newVal % 60));
                    }
                });

                oDialogBuild.setView(oPlaySettingView);

                oDialogBuild.setPositiveButton(getString(R.string.s_res_button_save_schedule), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // here you can add functions
                        oPlaySettingHandler.setIPlayingMode(chk_play_setting_until_end_part.isChecked(), chk_play_setting_period_from_cur.isChecked(), chk_play_setting_n_part_from_cur.isChecked());
                        oPlaySettingHandler.playSetting.setParts(pkr_play_setting_n_part.getValue());
                        oPlaySettingHandler.playSetting.setHours(tpk_play_setting_stop_time.getValue() / 60);
                        oPlaySettingHandler.playSetting.setMinutes(tpk_play_setting_stop_time.getValue() % 60);
                        oPlaySettingHandler.startCounterValue();
                        oPlaySettingHandler.setIStartPartIndex(oPlayingInfoHandler.playingInfo.getCurPartIndex());
                        savePrefSetting(true);
                        if(!oPlaySettingHandler.isPlayWithoutPlan()) {
                            tbnPlayPlan.setImageResource(R.drawable.ic_btn_plan_on);
                        }else{
                            tbnPlayPlan.setImageResource(R.drawable.ic_btn_plan_off);
                        }

                        if(!oPlaySettingHandler.isValidModeForRepeat()) {
                            tbnRepeat.setVisibility(View.GONE);
                        }else{
                            tbnRepeat.setVisibility(View.VISIBLE);
                            if(oPlaySettingHandler.playSetting.getRepeat()) {
                                tbnRepeat.setImageResource(R.drawable.baseline_repeat_white_24);
                            }else{
                                tbnRepeat.setImageResource(R.drawable.baseline_repeat_black_24);
                            }
                        }
                    }
                });
                oDialogBuild.setNeutralButton(getString(R.string.s_res_button_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // here you can add functions
                    }
                });

                AlertDialog adlgPlaySetting = oDialogBuild.create();
                adlgPlaySetting.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                adlgPlaySetting.show();
                adlgPlaySetting.getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility());
                adlgPlaySetting.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        try {
            hdl_seek.removeCallbacks(run);

            if (oMediaPlayer != null) {
                oPlayingInfoHandler.playingInfo.setCurPlayingPos( oMediaPlayer.getCurrentPosition() );
                savePrefSetting(false);
                oMediaPlayer.release();
                oMediaPlayer = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
        mAudioManager.abandonAudioFocus(this);
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onDestroy");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch ( item.getItemId() )
        {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.itm_menu_clean:
                final CleanUpFileStorageService oCleanUpFileStorageService = new CleanUpFileStorageService();
                oCleanUpFileStorageService.oITaskCompleted = new CleanUpFileStorageService.OnTaskCompleted() {
                    @Override
                    public void onTaskCompleted(boolean bResult) {
                        if(bResult){
                            if(audioPartAdapter != null){
                                audioPartAdapter.notifyDataSetChanged();
                            }
                            Toasty.success(PlayingListActivity.this,getString(R.string.s_res_mess_clean_ok),Toast.LENGTH_SHORT, true).show();
                        }else{
                            Toasty.error(PlayingListActivity.this,getString(R.string.s_res_mess_clean_fail),Toast.LENGTH_SHORT, true).show();
                        }
                    }
                };
                oCleanUpFileStorageService.execute();
                break;
            case R.id.itm_menu_rate:
                Uri uri = Uri.parse(String.format(DefSetting.gGoogleStoreUrl, this.getPackageName() ));
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onOptionsItemSelected");
        }
        return super.onOptionsItemSelected( item );
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.activity_audio_part, menu );

        MenuItem item = menu.findItem(R.id.itm_menu_share);
        ShareActionProvider myShareActionProvider = (ShareActionProvider) item.getActionProvider();
        Intent myIntent = new Intent();
        myIntent.setAction( Intent.ACTION_SEND );
        myIntent.putExtra( Intent.EXTRA_TEXT, String.format(DefSetting.gGoogleStoreUrl, this.getPackageName() ) );
        myIntent.setType( "text/plain" );
        myShareActionProvider.setShareIntent( myIntent );
        return true;
    }

    /* *
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        return super.onPrepareOptionsMenu( menu );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }
}
