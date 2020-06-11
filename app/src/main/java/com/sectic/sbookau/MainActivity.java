package com.sectic.sbookau;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.sectic.sbookau.adapter.CatAdapter;
import com.sectic.sbookau.model.CatalogList;
import com.sectic.sbookau.service.AuthService;
import com.sectic.sbookau.service.GetCatalogService;
import com.sectic.sbookau.service.CleanUpFileStorageService;
import com.sectic.sbookau.ultils.AppProvider;
import com.sectic.sbookau.ultils.AuthSetting;
import com.sectic.sbookau.ultils.DefSetting;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import es.dmoral.toasty.Toasty;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout drlDrawCatList;
    private RelativeLayout relLeftDrawer;
	private ListView lstCat;
	private ActionBarDrawerToggle oDrawerToggle;
    SpinKitView pbr_main_loading_status;

	// nav drawer title
	private CharSequence mDrawerTitle;
	// used to store app title
	private CharSequence mTitle;
    CatAdapter oCatAdapter;

    public String sTextQuery = null;
    private long iBackPressPeriod = 0;
    private boolean needOpenBackMainMenu = false;

    private GoogleSignInClient mGoogleSignInClient;


    private void initViews()
    {
        try {
            //AppProvider.mLstCatItem = new ArrayList<>();
            oCatAdapter = new CatAdapter(getApplicationContext(), AppProvider.mLstCatItem);

            drlDrawCatList = findViewById(R.id.drawer_layout);
            relLeftDrawer = findViewById(R.id.relLeftDrawer);
            lstCat = findViewById(R.id.lst_main_drawer);
            lstCat.setAdapter(oCatAdapter);

            LinearLayout lltHistory = findViewById(R.id.lltHistory);
            lltHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayHistory();
                }
            });

            LinearLayout lltListening = findViewById(R.id.lltListening);
            lltListening.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayListening();
                }
            });

            LinearLayout lltHome = findViewById(R.id.lltHome);
            lltHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayHome();
                }
            });

            ImageView imvRefresh = findViewById(R.id.imvRefresh);
            imvRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayRefresh();
                }
            });

            pbr_main_loading_status = findViewById(R.id.pbr_main_loading_status);

            mTitle = mDrawerTitle = getTitle();
            lstCat.setOnItemClickListener(new SlideMenuClickListener());
            // enabling action bar app icon and behaving it as toggle button
            getActionBar().setDisplayHomeAsUpEnabled(true);

            oDrawerToggle = new ActionBarDrawerToggle(this, drlDrawCatList,
                    R.string.app_name, // nav drawer open - description for accessibility
                    R.string.app_name // nav drawer close - description for accessibility
            ) {
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    getActionBar().setTitle(mTitle);
                    invalidateOptionsMenu();
                }

                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    getActionBar().setTitle(mDrawerTitle);
                    invalidateOptionsMenu();
                }
            };

            ImageButton imbAccount = findViewById(R.id.imbAccount);
            if(DefSetting.ENUM_LOGIN_GOOGLE.equals(AuthSetting.gLoginType)){
                TextView txtName = findViewById(R.id.txtName);
                txtName.setText(AuthSetting.gGDisplayName);
                TextView txtEmail = findViewById(R.id.txtEmail);
                txtEmail.setText(AuthSetting.gGEmail);
                Glide.with(MainActivity.this)
                        .load(AuthSetting.gGPhotoUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imbAccount);
            }else{
                Glide.with(MainActivity.this)
                        .load(R.drawable.user_login)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imbAccount);
            }

            imbAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(DefSetting.ENUM_LOGIN_GOOGLE.equals(AuthSetting.gLoginType)){
                        signOut();
                    }else{
                        signIn();
                    }
                }
            });
            //AppProvider.mLstCatItem.clear();
            oCatAdapter.updateParams(getApplicationContext(), AppProvider.mLstCatItem);

            displayHome();
            needOpenBackMainMenu = true;

            drlDrawCatList.addDrawerListener(oDrawerToggle);
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
                AppProvider.mLstCatItem.clear();
                if(bResult){
                    oGetCatalogueService.parseCatDataFromRestResult(AppProvider.mLstCatItem);
                }
                if(AppProvider.mLstCatItem.size() > 0) {
                    oCatAdapter.updateParams(getApplicationContext(), AppProvider.mLstCatItem);
                    lstCat.setAdapter(oCatAdapter);
                }
                displayHome();
                pbr_main_loading_status.setVisibility(View.GONE);
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
                if(bResult){
                    GetCatThread();
                    AppProvider.mIsFirstAuth = true;
                }else{
                    if( DefSetting.gSystemMessage == null || DefSetting.gSystemMessage.length() <= 0 ){
                        Toasty.error(MainActivity.this, getString(R.string.s_res_mess_connect_fail), Toast.LENGTH_SHORT, true).show();
                    }
                }
                if( DefSetting.gSystemMessage != null && DefSetting.gSystemMessage.length() > 0 ) {
                    Toasty.info(MainActivity.this, DefSetting.gSystemMessage, Toast.LENGTH_SHORT, true).show();
                }
            }
        };
        pbr_main_loading_status.setVisibility(View.VISIBLE);
        oJsonService.execute();

        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "LoginAndGetCat");
        }
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        initViews();

        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onCreate");
        }
	}

    @Override
    protected void onStart() {
        super.onStart();
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

    /**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sbookau, menu);

        MenuItem itemShare = menu.findItem(R.id.itm_menu_share);
        ShareActionProvider myShareActionProvider = (ShareActionProvider) itemShare.getActionProvider();
        Intent myIntent = new Intent();
        myIntent.setAction(Intent.ACTION_SEND);
        myIntent.putExtra( Intent.EXTRA_TEXT, String.format(DefSetting.gGoogleStoreUrl, this.getPackageName() ) );
        myIntent.setType("text/plain");
        myShareActionProvider.setShareIntent(myIntent);

        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onCreateOptionsMenu");
        }
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (oDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.itm_menu_exit:
            this.finish();
			break;
        case R.id.itm_menu_clean:
            final CleanUpFileStorageService oCleanUpFileStorageService = new CleanUpFileStorageService();
            oCleanUpFileStorageService.oITaskCompleted = new CleanUpFileStorageService.OnTaskCompleted() {
                @Override
                public void onTaskCompleted(boolean bResult) {
                    if(bResult){
                        Toasty.success(MainActivity.this,getString(R.string.s_res_mess_clean_ok),Toast.LENGTH_SHORT, true).show();
                    }else{
                        Toasty.error(MainActivity.this,getString(R.string.s_res_mess_clean_fail),Toast.LENGTH_SHORT, true).show();
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
        return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
        drlDrawCatList.isDrawerOpen(relLeftDrawer);
		return super.onPrepareOptionsMenu(menu);
	}

	public void displayHome(){
        sTextQuery = null;
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frm_container, fragment).commit();
        setTitle(R.string.s_res_home_page);

        lstCat.setItemChecked(-1, true);

        if(!needOpenBackMainMenu){
            drlDrawCatList.closeDrawer(relLeftDrawer);
        }else{
            drlDrawCatList.openDrawer(relLeftDrawer);
        }
        needOpenBackMainMenu = false;

        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "displayHome");
        }
    }

    public void displayListening(){
        lstCat.setItemChecked(-1, true);
        Intent intent = new Intent("com.sectic.sbookau.PLAYINGLIST");
        intent.putExtra("bLoadPref", 1);
        startActivity(intent);
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "displayListening");
        }
    }

    public void displayHistory(){
        lstCat.setItemChecked(-1, true);
        Fragment fragment = BookListFragment.newInstance("","", "FILE");
        getFragmentManager().beginTransaction().replace(R.id.frm_container, fragment).commit();
        setTitle(R.string.s_res_history);
        drlDrawCatList.closeDrawer(relLeftDrawer);
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "displayHistory");
        }
    }

    public void displayRefresh(){
        GetCatThread();
        needOpenBackMainMenu = true;
        drlDrawCatList.closeDrawer(relLeftDrawer);
        pbr_main_loading_status.setVisibility(View.VISIBLE);
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "displayRefresh");
        }
    }

	public void displayView(int position) {
        Fragment fragment;
        if(position >= 0){
            fragment = BookListFragment.newInstance(AppProvider.mLstCatItem.get(position).id, null, "CLOUD");
            getFragmentManager().beginTransaction().replace(R.id.frm_container, fragment).commit();
            lstCat.setItemChecked(position, true);
            lstCat.setSelection(position);
            setTitle(AppProvider.mLstCatItem.get(position).value);
        }else{
            fragment = BookListFragment.newInstance(null, sTextQuery, "CLOUD");
            getFragmentManager().beginTransaction().replace(R.id.frm_container, fragment).commit();
            if(sTextQuery == null || sTextQuery.length() == 0){
                setTitle(getString(R.string.s_res_mess_empty_keyword));
            }else{
                setTitle(sTextQuery);
            }
        }
        drlDrawCatList.closeDrawer(relLeftDrawer);

        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "displayView");
        }
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
        try{
            getActionBar().setTitle(mTitle);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
        oDrawerToggle.syncState();
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onPostCreate");
        }
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
        oDrawerToggle.onConfigurationChanged(newConfig);
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onConfigurationChanged");
        }
	}

    @Override
    public void onBackPressed()
    {
        if (iBackPressPeriod + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toasty.info(getBaseContext(), getString(R.string.s_res_mess_back_again_to_exit), Toast.LENGTH_SHORT, true).show();
        }
        iBackPressPeriod = System.currentTimeMillis();
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onBackPressed");
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onDestroy");
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onWindowFocusChanged");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, DefSetting.RC_SIGN_IN);
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "signIn");
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        Toasty.info(MainActivity.this,getString(R.string.s_res_mess_logout_google),Toast.LENGTH_SHORT, true).show();
                        AuthSetting.gLoginType = DefSetting.ENUM_LOGIN_BASE;

                        TextView txtName = findViewById(R.id.txtName);
                        txtName.setText("");
                        TextView txtEmail = findViewById(R.id.txtEmail);
                        txtEmail.setText("");
                        ImageButton imbAccount = findViewById(R.id.imbAccount);
                        Glide.with(MainActivity.this)
                                .load(R.drawable.user_login)
                                .apply(RequestOptions.circleCropTransform())
                                .into(imbAccount);
                        //imbAccount.setImageResource(R.drawable.user_login);
                        LoginAndGetCat();
                        // [END_EXCLUDE]
                    }
                });
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "signOut");
        }
    }
    private void revokeAccess() {
	    // disconnect google account
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        //updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "revokeAccess");
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


            TextView txtName = findViewById(R.id.txtName);
            txtName.setText(AuthSetting.gGDisplayName);
            TextView txtEmail = findViewById(R.id.txtEmail);
            txtEmail.setText(AuthSetting.gGEmail);
            ImageButton imbAccount = findViewById(R.id.imbAccount);
            Glide.with(MainActivity.this)
                    .load(AuthSetting.gGPhotoUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imbAccount);

            Toasty.info(MainActivity.this,getString(R.string.s_res_mess_login_google),Toast.LENGTH_SHORT, true).show();
            LoginAndGetCat();
        } catch (ApiException e) {
            AuthSetting.gLoginType = DefSetting.ENUM_LOGIN_BASE;
            LoginAndGetCat();
            e.printStackTrace();
        }
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "handleSignInResult");
        }
    }
}
