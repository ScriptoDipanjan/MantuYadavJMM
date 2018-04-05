package com.scripto.mantuyadavjmm;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SplashActivity extends AppCompatActivity {

    private Context mContext;
    DBManager db;
    Cursor profile;
    static String fbProfile, fbName, fbPic, fbToken, ytChannel, ytToken, ytDevKey, backPic, marqueeText, marqueeTextHindi, marqueeBG,
            introLink, manifestoLink, achievementsLink, upcomingLink, resultLink, contactLink,
            introLinkHindi, manifestoLinkHindi, achievementsLinkHindi, upcomingLinkHindi, resultLinkHindi, contactLinkHindi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //Log.e("Activity", "Started");
        getSupportActionBar().hide();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mContext = this;
        db = new DBManager(mContext);

        if(Methods.isConnected(mContext)){
            db.delete();
            getToken();
        } else {
            profile = db.getProfile();
            if(profile.getCount() > 0){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(mContext, Dashboard.class));
                        finish();
                    }
                }, 30000);
            } else
                Methods.errorConnection(mContext);
        }
    }

    private void getToken() {
        //freeMemory();
        //Log.e("Entered", "getToken()");
        String tag_string_req = "req_token";

        StringRequest strReq = new StringRequest(Request.Method.POST, Methods.URL_DATA, new Response.Listener<String>() {

            @Override
            public void onResponse(String data) {
                //Log.e("Response", data.toString());

                try {
                    JSONObject dataResp = new JSONObject(data);

                    fbProfile = dataResp.getString("fbProfile");
                    fbName = dataResp.getString("fbName");
                    fbPic = dataResp.getString("fbPic");
                    fbToken = dataResp.getString("fbToken");
                    ytChannel = dataResp.getString("ytChannel");
                    ytToken = dataResp.getString("ytToken");
                    ytDevKey = dataResp.getString("ytDevKey");
                    introLink = dataResp.getString("introLink");
                    manifestoLink = dataResp.getString("manifestoLink");
                    achievementsLink = dataResp.getString("achievementsLink");
                    upcomingLink = dataResp.getString("upcomingLink");
                    resultLink = dataResp.getString("resultLink");
                    contactLink = dataResp.getString("contactLink");
                    introLinkHindi = dataResp.getString("introLinkHindi");
                    manifestoLinkHindi = dataResp.getString("manifestoLinkHindi");
                    achievementsLinkHindi = dataResp.getString("achievementsLinkHindi");
                    upcomingLinkHindi = dataResp.getString("upcomingLinkHindi");
                    resultLinkHindi = dataResp.getString("resultLinkHindi");
                    contactLinkHindi = dataResp.getString("contactLinkHindi");
                    marqueeBG = dataResp.getString("marqueeBG");
                    marqueeText = dataResp.getString("marqueeText");
                    marqueeTextHindi = dataResp.getString("marqueeTextHindi");
                    backPic = dataResp.getString("backPic");

                    db.addProfile(fbProfile, fbName, fbPic, fbToken, ytChannel, ytToken, ytDevKey,
                            introLink, manifestoLink, achievementsLink, upcomingLink, resultLink, contactLink,
                            introLinkHindi, manifestoLinkHindi, achievementsLinkHindi, upcomingLinkHindi, resultLinkHindi, contactLinkHindi,
                            marqueeBG, marqueeText, marqueeTextHindi, backPic);

                    String URL_FEED = "https://graph.facebook.com/v2.5/" + fbProfile + "/posts?fields=message,created_time&limit=100&access_token=" + fbToken;
                    getNewsFeed(URL_FEED);

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(" Token Error", error.getMessage());
                errorLogger(error);
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

    private void getNewsFeed(String URL) {
        //freeMemory();
        //Log.e("Entered", "getNewsFeed(String URL)");
        String tag_string_req = "req_token";

        StringRequest strReq = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String data) {
                //Log.e("News Response", data.toString());


                try {
                    final JSONObject postResp = new JSONObject(data);

                    try {
                        JSONArray postData = new JSONArray(postResp.getString("data"));

                        if(postData.length()>0){
                            for(int i=0; i<postData.length(); i++){
                                String message = null;
                                JSONObject postDataResponse = postData.getJSONObject(i);
                                String id = postDataResponse.getString("id");
                                String created_time = postDataResponse.getString("created_time");
                                if(postDataResponse.has("message")) {
                                    message = postDataResponse.getString("message");
                                    db.addNewsFeed(id, message, created_time);
                                }
                            }


                            JSONObject paging = new JSONObject(postResp.getString("paging"));
                            if(paging.has("next")) {
                                String nextLink = paging.getString("next");
                                getNewsFeed(nextLink);
                            }
                        }

                        String URL_PHOTO = "https://graph.facebook.com/v2.5/" + fbProfile + "/photos?type=uploaded&fields=id&limit=500&access_token=" + fbToken;
                        getPhoto(URL_PHOTO);

                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("News Error", error.getMessage());
                errorLogger(error);
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

    private void getPhoto(String URL) {
        //freeMemory();
        //Log.e("Entered", "getPhoto(String URL)");
        String tag_string_req = "req_photos";

        StringRequest strReq = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String data) {
                //Log.e("Photo Response", data.toString());


                try {
                    JSONObject photoResp = new JSONObject(data);

                    try {
                        JSONArray photoData = new JSONArray(photoResp.getString("data"));
                        //Log.e("Photos", photoData.toString());
                        if(photoData.length()>0){
                            for(int i=0; i<photoData.length(); i++){
                                JSONObject photoDataResponse = photoData.getJSONObject(i);
                                getData(photoDataResponse.getString("id"));
                            }

                            JSONObject paging = new JSONObject(photoResp.getString("paging"));
                            //Log.e("Prev", String.valueOf(photoData.length()));
                            if(paging.has("next")) {
                                //Log.e("Next", paging.getString("next"));
                                String nextLink = paging.getString("next");
                                getPhoto(nextLink);

                            }
                        }

                        String URL_FB = "https://graph.facebook.com/v2.5/" + fbProfile + "/videos?fields=created_time,description,thumbnails&limit=300&access_token=" + fbToken;
                        getFB(URL_FB);

                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                errorLogger(error);
                //Log.e("Photo Error", error.getMessage());
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

    private void getData(final String id) {
        //Log.e("Entered", "getData(final String id)");
        String URL = "https://graph.facebook.com/v2.5/" + id + "?fields=images,name,created_time&access_token=" + fbToken;
        String tag_string_req = "req_photo_data";

        StringRequest strReq = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String data) {
                //Log.e(" Data Response " + id, data.toString());


                try {
                    JSONObject photoResp = new JSONObject(data);

                    try {
                        JSONArray photoData = new JSONArray(photoResp.getString("images"));
                        JSONObject photoDataResponse = photoData.getJSONObject(0);

                        String uri = photoDataResponse.getString("source");
                        String time = photoResp.getString("created_time");
                        String name;

                        if(photoResp.has("name")) {
                            name = photoResp.getString("name");
                        } else {
                            name = "";
                        }
                        db.addPhoto(id, uri, name, time);

                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                errorLogger(error);
                //Log.e("Data Error of " + id, error.getMessage());
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

    private void getFB(String URL) {
        //freeMemory();
        //Log.e("Entered", "getFB(String URL)");
        String tag_string_req = "req_video";

        StringRequest strReq = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String data) {
                //Log.e("FB Response", data.toString());


                try {
                    final JSONObject videoResp = new JSONObject(data);

                    try {
                        JSONArray videoData = new JSONArray(videoResp.getString("data"));
                        if(videoData.length()>0){
                            for(int i=0; i<videoData.length(); i++){
                                String message = "";
                                JSONObject videoDataResponse = videoData.getJSONObject(i);
                                String id = videoDataResponse.getString("id");
                                String created_time = videoDataResponse.getString("created_time");

                                JSONObject videoThumb = new JSONObject(videoDataResponse.getString("thumbnails"));
                                JSONArray videoThumbData = new JSONArray(videoThumb.getString("data"));
                                JSONObject videoThumbResponse = videoThumbData.getJSONObject(0);
                                String videoThumbUri = videoThumbResponse.getString("uri");

                                if(videoDataResponse.has("description")) {
                                    message = videoDataResponse.getString("description");
                                }

                                db.addFBVideo(id, message, videoThumbUri, created_time);
                                //freeMemory();
                                //Log.e(i+1 +". ID " + id, videoThumbUri);
                            }

                            JSONObject paging = new JSONObject(videoResp.getString("paging"));
                            if(paging.has("next")) {
                                String nextLink = paging.getString("next");
                                getFB(nextLink);
                            }
                        }

                        String URL_YT = "https://www.googleapis.com/youtube/v3/playlistItems?playlistId=" + ytChannel+"&key=" + ytToken + "&part=snippet&maxResults=50&order=date";
                        getYT(URL_YT);


                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                errorLogger(error);
                //Log.e(" FB Error", error.getMessage());
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

    private void getYT(String URL) {
        //freeMemory();
        //Log.e("Entered", "getYT(String URL)");
        String tag_string_req = "req_video";

        StringRequest strReq = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String data) {
                //Log.e("YT Response", data.toString());


                try {
                    final JSONObject videoResp = new JSONObject(data);

                    try {
                        JSONArray videoData = new JSONArray(videoResp.getString("items"));

                        if(videoData.length()>0){
                            for(int i=0; i<videoData.length(); i++){
                                JSONObject videoDataResponse = videoData.getJSONObject(i);
                                JSONObject videoDataResp = new JSONObject(videoDataResponse.getString("snippet"));
                                //String message = null;
                                //Log.e(i+1+ ". Snippet", videoDataResp.toString());

                                String publishedAt = videoDataResp.getString("publishedAt");
                                String title = videoDataResp.getString("title");
                                JSONObject thumbnails = new JSONObject(videoDataResp.getString("thumbnails"));
                                String url = new JSONObject(thumbnails.getString("default")).getString("url");
                                String videoId = new JSONObject(videoDataResp.getString("resourceId")).getString("videoId");

                                db.addYTVideo(videoId, title, url, publishedAt);
                                //freeMemory();
                            }

                            if(videoResp.has("nextPageToken")) {
                                String nextLink = videoResp.getString("nextPageToken");
                                String URL_YT = "https://www.googleapis.com/youtube/v3/playlistItems?playlistId=" + ytChannel + "&key=" + ytToken + "&part=snippet&maxResults=50&order=date&pageToken=" + nextLink;
                                getYT(URL_YT);
                            } else
                                changeActivity();
                        } else
                            changeActivity();



                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                errorLogger(error);
                //Log.e("YT Error", error.getMessage());
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

    void errorLogger(VolleyError error){
        error.printStackTrace();
        Toast.makeText(mContext, "Please check your network connection \n& try again later...", Toast.LENGTH_SHORT).show();
        db.delete();
        finish();
    }

    void changeActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(mContext, Dashboard.class));
                finish();
            }
        }, 500);
    }

}
