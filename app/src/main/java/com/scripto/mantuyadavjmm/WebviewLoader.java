package com.scripto.mantuyadavjmm;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WebviewLoader extends AppCompatActivity {

    SwipeRefreshLayout swipeLayout;
    String title, link, msg, send;
    DBManager db;
    Cursor profile;
    WebView webviewLoader;
    private Context mContext;
    Button butShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_webview_loader);

        mContext = this;
        db = new DBManager(mContext);
        profile = db.getProfile();

        Intent intent = getIntent();
        String tag = intent.getStringExtra("tag");

        if (Dashboard.stat == 1) {
            msg = getString(R.string.hindi_wait);
            send = getString(R.string.hindi_send);

            if (tag.equalsIgnoreCase("1")) {
                title = getString(R.string.hindi_intro);
                link = profile.getString(profile.getColumnIndex("introLinkHindi"));

            } else if (tag.equalsIgnoreCase("2")) {
                title = getString(R.string.hindi_manifesto);
                link = profile.getString(profile.getColumnIndex("manifestoLinkHindi"));

            } else if (tag.equalsIgnoreCase("3")) {
                title = getString(R.string.hindi_achievements);
                link = profile.getString(profile.getColumnIndex("achievementsLinkHindi"));

            } else if (tag.equalsIgnoreCase("4")) {
                title = getString(R.string.hindi_upcoming);
                link = profile.getString(profile.getColumnIndex("upcomingLinkHindi"));

            } else if (tag.equalsIgnoreCase("5")) {
                title = getString(R.string.hindi_results);
                link = profile.getString(profile.getColumnIndex("resultLinkHindi"));

            } else if (tag.equalsIgnoreCase("6")) {
                title = getString(R.string.hindi_contact);
                link = profile.getString(profile.getColumnIndex("contactLinkHindi"));

            }
        } else {
            msg = getString(R.string.wait);
            send = getString(R.string.send);

            if (tag.equalsIgnoreCase("1")) {
                title = getString(R.string.intro);
                link = profile.getString(profile.getColumnIndex("introLink"));

            } else if (tag.equalsIgnoreCase("2")) {
                title = getString(R.string.manifesto);
                link = profile.getString(profile.getColumnIndex("manifestoLink"));

            } else if (tag.equalsIgnoreCase("3")) {
                title = getString(R.string.achievements);
                link = profile.getString(profile.getColumnIndex("achievementsLink"));

            } else if (tag.equalsIgnoreCase("4")) {
                title = getString(R.string.upcoming);
                link = profile.getString(profile.getColumnIndex("upcomingLink"));

            } else if (tag.equalsIgnoreCase("5")) {
                title = getString(R.string.results);
                link = profile.getString(profile.getColumnIndex("resultLink"));

            } else if (tag.equalsIgnoreCase("6")) {
                title = getString(R.string.contact);
                link = profile.getString(profile.getColumnIndex("contactLink"));

            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);


        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        webviewLoader = findViewById(R.id.webviewLoader);
        butShare = findViewById(R.id.butShare);

        butShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag_string_req = "req_token";
                StringRequest strReq = new StringRequest(com.android.volley.Request.Method.GET, link, new com.android.volley.Response.Listener<String>() {

                    @Override
                    public void onResponse(String data) {

                        try {
                            data = new String(data.getBytes("ISO-8859-1"), "UTF-8");
                            Document doc = Jsoup.parse(data);
                            String title = doc.title();
                            Element body = doc.body();

                            //Log.e(title, Html.fromHtml(String.valueOf(body)).toString());

                            String shareData = Html.fromHtml(String.valueOf(body)).toString();

                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareData);
                            shareIntent.setType("text/plain");
                            startActivity(Intent.createChooser(shareIntent, send));

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }


                    }
                }, new com.android.volley.Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.e(" Token Error", error.getMessage());
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<String, String>();
                        //params.put("AccessID", accessid);

                        //Log.e("Params", String.valueOf(params));
                        return params;
                    }

                };

                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Methods.isConnected(mContext)) {

            swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeLayout.setRefreshing(true);
                    webviewLoader.clearCache(true);
                    webviewLoader.loadUrl(link);
                    webviewLoader.setVisibility(View.INVISIBLE);
                }
            });

            webviewLoader.setVerticalScrollBarEnabled(false);
            webviewLoader.setHorizontalScrollBarEnabled(false);
            webviewLoader.getSettings().setJavaScriptEnabled(true);
            webviewLoader.clearHistory();
            webviewLoader.clearFormData();
            webviewLoader.clearCache(true);

            webviewLoader.setWebViewClient(new WebViewClient() {

                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    swipeLayout.setRefreshing(true);
                    webviewLoader.setVisibility(View.INVISIBLE);
                    butShare.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    swipeLayout.setRefreshing(false);
                    webviewLoader.setVisibility(View.VISIBLE);
                    butShare.setVisibility(View.VISIBLE);
                }
            });

            webviewLoader.loadUrl(link);
        } else {
            Methods.errorConnection(mContext);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }
}
