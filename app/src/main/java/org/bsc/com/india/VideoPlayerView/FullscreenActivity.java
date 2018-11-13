package org.bsc.com.india.VideoPlayerView;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.os.Environment;
import android.widget.ProgressBar;

import java.io.File;

import com.halilibo.bettervideoplayer.BetterVideoPlayer;

import org.bsc.com.india.FileDownloader;
import org.bsc.com.india.PdfView.DownloadListener;
import org.bsc.com.india.PdfView.SampleActivity;
import org.bsc.com.india.R;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity implements DownloadListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private String fileName="";
    private String url = "";
    FileDownloader fileDownloader = null;
    private String filePath="";

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private BetterVideoPlayer mBetterVideoPlayer;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mBetterVideoPlayer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        fileName = getIntent().getStringExtra("fileName");
        url = getIntent().getStringExtra("url");
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Download"+File.separator +"BSCVisit"+ File.separator+fileName;

        mVisible = true;
        mBetterVideoPlayer = (BetterVideoPlayer) findViewById(R.id.bvp);

        if(fileExist(filePath)){
            openVideo(filePath);
        }else{
            downloadFile(url,fileName);

        }


        // Set up the user interaction to manually show or hide the system UI.
        /*mBetterVideoPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });*/

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }
    private boolean fileExist(String filePath){
        File file = new File(filePath);
        if(file.exists()){
            return true;
        }
        else{
            return false;
        }

    }

    private void openVideo(String filePath){
        File f = new File(filePath);
        Uri videoUri = Uri.fromFile(f);
        mBetterVideoPlayer.setSource(videoUri);
        //mBetterVideoPlayer.setSource(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video));
        //mBetterVideoPlayer.setSource(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Download"+File.separator+fileName));
        mBetterVideoPlayer.getToolbar().setTitle(fileName);
        mBetterVideoPlayer.getToolbar()
                .setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        mBetterVideoPlayer.getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mBetterVideoPlayer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
    private void downloadFile(String url, String fileName) {
        try {
            fileDownloader = new FileDownloader(FullscreenActivity.this, url,fileName,this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                fileDownloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                fileDownloader.execute();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onSuccess() {
        openVideo(filePath);
    }

    @Override
    public void onFailure() {

    }
}
