package com.scripto.mantuyadavjmm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Player extends AppCompatActivity {

    WebView videoView;
    String videoID, fbProfile;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_player);

        mContext = this;
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (!Methods.isConnected(mContext)) {
            Methods.errorConnection(mContext);
        } else {


            Intent intent = getIntent();
            fbProfile = intent.getStringExtra("fbProfile");
            videoID = intent.getStringExtra("videoID");

            String data = "<iframe src=\"https://www.facebook.com/plugins/video.php?href=https%3A%2F%2Fwww.facebook.com%2F" + fbProfile + "%2Fvideos%2F" +
                    videoID + "\" width=\"100%\" height=\"98%\" style=\"border:none;overflow:hidden\" scrolling=\"no\" frameborder=\"0\" " +
                    "allowTransparency=\"true\" allowFullScreen=\"true\"></iframe>";

            videoView = (WebView) findViewById(R.id.videoView);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                videoView.getSettings().setMediaPlaybackRequiresUserGesture(false);
            }
            videoView.getSettings().setLoadWithOverviewMode(true);
            videoView.getSettings().setUseWideViewPort(true);
            videoView.getSettings().setBuiltInZoomControls(false);
            videoView.getSettings().setPluginState(WebSettings.PluginState.ON);
            videoView.setBackgroundColor(Color.BLACK);
            videoView.setVerticalScrollBarEnabled(false);
            videoView.setHorizontalScrollBarEnabled(false);
            videoView.getSettings().setJavaScriptEnabled(true);

            videoView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return (event.getAction() == MotionEvent.ACTION_MOVE);
                }
            });

            videoView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }
            });

            videoView.loadData(data, "text/html", "UTF-8");
        }
    }

}
