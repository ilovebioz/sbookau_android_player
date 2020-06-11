package com.sectic.sbookau.ultils;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorRes;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by bioz on 6/22/2017.
 */

public class AdvUtils {
    public static void lockCurrentScreenRotation(Activity iOGui)
    {
        switch (iOGui.getResources().getConfiguration().orientation)
        {
            case Configuration.ORIENTATION_PORTRAIT:
                iOGui.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                iOGui.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }
    }

    public static void hideSystemUI(Activity iOGui, boolean bHideActionBar) {
        iOGui.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final View decorView = iOGui.getWindow().getDecorView();
        // Hide the status bar.
        final int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        if(bHideActionBar){
            ActionBar actionBar = iOGui.getActionBar();
            if(actionBar != null){
                actionBar.hide();
            }
        }

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
        {
            @Override
            public void onSystemUiVisibilityChange(int visibility)
            {
                if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                {
                    decorView.setSystemUiVisibility(uiOptions);
                }
            }
        });
    }

    public static void tintMenuIcon(Context context, MenuItem item, @ColorRes int color) {
        Drawable normalDrawable = item.getIcon();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, context.getResources().getColor(color));
        item.setIcon(wrapDrawable);
    }
}
