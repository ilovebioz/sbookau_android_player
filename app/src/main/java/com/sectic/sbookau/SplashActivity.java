package com.sectic.sbookau;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.sectic.sbookau.model.CatalogList;
import com.sectic.sbookau.service.AuthService;
import com.sectic.sbookau.service.GetCatalogService;
import com.sectic.sbookau.ultils.AppProvider;
import com.sectic.sbookau.ultils.AuthSetting;
import com.sectic.sbookau.ultils.DefSetting;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class SplashActivity extends Activity implements EasyPermissions.PermissionCallbacks {
    public static final String TAG = SplashActivity.class.getSimpleName();
    private GoogleSignInClient mGoogleSignInClient;

    NumberProgressBar pBProgress;
    SpinKitView pbr_main_loading_status;

    private static final int RC_FULL_PERM = 124;
    private static final String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.READ_PHONE_STATE
    };

    @AfterPermissionGranted(RC_FULL_PERM)
    public void requestFullPermissionTask() {
        if(!EasyPermissions.hasPermissions(this, PERMISSIONS)){
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.s_res_mess_accept_full_permission),
                    RC_FULL_PERM,
                    PERMISSIONS);
        }else{
            pBProgress.setProgress(25);
            // [START configure_signin]
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(DefSetting.SERVER_CLIENT_ID_FOR_GOOGLE_SERVICE)
                    .requestEmail()
                    .build();
            // [END configure_signin]

            // [START build_client]
            // Build a GoogleSignInClient with the options specified by gso.
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            // [END build_client]


            // [START on_start_sign_in]
            // Check for existing Google Sign In account, if the user is already signed in
            // the GoogleSignInAccount will be non-null.
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

            if(account != null){
                refreshIdToken();
            }else{
                AuthSetting.gLoginType = DefSetting.ENUM_LOGIN_BASE;
                LoginAndGetCat();
            }
            // [END on_start_sign_in]
        }

        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "requestFullPermissionTask");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onRequestPermissionsResult");
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onPermissionsGranted: " + requestCode + ":" + perms.size());
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onPermissionsDenied: " + requestCode + ":" + perms.size());
        }

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }


    private void initViews()
    {
        try {
            AppProvider.mLstCatItem = new ArrayList<>();
            pBProgress = this.findViewById(R.id.pBProgress);
            pBProgress.setProgress(0);

            TextView txt_version = this.findViewById(R.id.txt_version);
            String sVersion = getString(R.string.s_res_verion) + " " + BuildConfig.VERSION_NAME;
            txt_version.setText(sVersion);

            pbr_main_loading_status = findViewById(R.id.pbr_main_loading_status);
            pbr_main_loading_status.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "initViews");
        }
    }

    public void GetCatThread()
    {
        final GetCatalogService oGetCatalogueService = new GetCatalogService();
        oGetCatalogueService.oITaskCompleted = new GetCatalogService.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean bResult, CatalogList oCatalogList) {
                pBProgress.setProgress(100);
                AppProvider.mLstCatItem.clear();
                if(bResult){
                    oGetCatalogueService.parseCatDataFromRestResult(AppProvider.mLstCatItem);
                }

                pbr_main_loading_status.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent myIntent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(myIntent);
                        finish();
                    }
                }, 1000);
            }
        };
        oGetCatalogueService.execute();

        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "GetCatThread");
        }
    }

    public void LoginAndGetCat(){
        final AuthService oJsonService = new AuthService(true);
        oJsonService.mITaskCompleted = new AuthService.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(boolean bResult) {
                pBProgress.setProgress(75);
                if(bResult){
                    GetCatThread();
                    AppProvider.mIsFirstAuth = true;
                }else{
                    if( (DefSetting.gSystemMessage == null) ||
                            (DefSetting.gSystemMessage.length() <= 0) ){
                        Toasty.error(SplashActivity.this, getString(R.string.s_res_mess_connect_fail), Toast.LENGTH_SHORT, true).show();
                    }
                }
                if( DefSetting.gSystemMessage != null && DefSetting.gSystemMessage.length() > 0 ) {
                    Toasty.info(SplashActivity.this, DefSetting.gSystemMessage, Toast.LENGTH_SHORT, true).show();
                }
            }
        };
        oJsonService.execute();
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "LoginAndGetCat");
        }
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initViews();
        requestFullPermissionTask();
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onCreate");
        }
	}

    @Override
    protected void onStart() {
        super.onStart();
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onStart");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onResume");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onDestroy");
        }
    }

    private void refreshIdToken() {
        // Attempt to silently refresh the GoogleSignInAccount. If the GoogleSignInAccount
        // already has a valid token this method may complete immediately.
        //
        // If the user has not previously signed in on this device or the sign-in has expired,
        // this asynchronous branch will attempt to sign in the user silently and get a valid
        // ID token. Cross-device single sign on will occur in this branch.
        mGoogleSignInClient.silentSignIn()
                .addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        handleSignInResult(task);
                    }
                });

        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "refreshIdToken");
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String sIdToken = account.getIdToken();
            AuthSetting.gLoginType = DefSetting.ENUM_LOGIN_GOOGLE;
            AuthSetting.gGIdToken = sIdToken;
            AuthSetting.gGPhotoUrl = account.getPhotoUrl().toString();
            AuthSetting.gGEmail = account.getEmail();
            AuthSetting.gGDisplayName = account.getDisplayName();

        } catch (ApiException e) {
            AuthSetting.gLoginType = DefSetting.ENUM_LOGIN_BASE;
            e.printStackTrace();
        }finally{
            pBProgress.setProgress(50);
            LoginAndGetCat();
        }

        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "handleSignInResult");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            if(!EasyPermissions.hasPermissions(this, PERMISSIONS)){
                Toasty.warning(this,R.string.s_res_mess_must_grant_permission, Toast.LENGTH_SHORT, true).show();
                final Handler handlerTransferData = new Handler();
                handlerTransferData.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        System.exit(0);
                    }
                }, 3000);
            }else{
                pBProgress.setProgress(25);
                // [START configure_signin]
                // Configure sign-in to request the user's ID, email address, and basic
                // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(DefSetting.SERVER_CLIENT_ID_FOR_GOOGLE_SERVICE)
                        .requestEmail()
                        .build();
                // [END configure_signin]

                // [START build_client]
                // Build a GoogleSignInClient with the options specified by gso.
                mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                // [END build_client]

                // [START on_start_sign_in]
                // Check for existing Google Sign In account, if the user is already signed in
                // the GoogleSignInAccount will be non-null.
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

                if(AppProvider.mIsFirstAuth){
                    return;
                }

                if(account != null){
                    refreshIdToken();
                }else{
                    AuthSetting.gLoginType = DefSetting.ENUM_LOGIN_BASE;
                    LoginAndGetCat();
                }
                // [END on_start_sign_in]
            }
        }

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == DefSetting.RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onActivityResult");
        }
    }
}
