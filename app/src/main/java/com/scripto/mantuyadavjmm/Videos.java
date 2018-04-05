package com.scripto.mantuyadavjmm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import java.util.List;

public class Videos extends AppCompatActivity {

    String title, msg, load, fbProfile, fbToken, ytChannel, ytToken, ytDevKey, error, send;
    LayoutInflater inflater;
    DBManager db;
    Cursor profile, videosFB, videosYT;
    ScrollView scrollVid;
    LinearLayout linVid;
    TextView text;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_videos);
        getSupportActionBar().setTitle("YouTube Videos");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = this;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        db = new DBManager(mContext);

        if (Dashboard.stat == 1) {
            title = getString(R.string.hindi_videos);
            msg = getString(R.string.hindi_wait);
            load = getString(R.string.hindi_load);
            error = getString(R.string.hindi_empty);
            send = getString(R.string.hindi_send);

        } else {
            title = getString(R.string.videos);
            msg = getString(R.string.wait);
            load = getString(R.string.load);
            error = getString(R.string.empty);
            send = getString(R.string.send);
        }

        profile = db.getProfile();
        fbProfile = profile.getString(profile.getColumnIndex("FB_Profile_ID"));
        fbToken = profile.getString(profile.getColumnIndex("FB_Token"));
        ytChannel = profile.getString(profile.getColumnIndex("YT_channel"));
        ytToken = profile.getString(profile.getColumnIndex("YT_token"));
        ytDevKey = profile.getString(profile.getColumnIndex("YT_dev_key"));

        videosFB = db.getFBVideo();
        videosYT = db.getYTVideo();

        text = findViewById(R.id.errorVideos);
        text.setText(error);

        //Log.e("Log "+ytChannel, ytToken);

        /*Client			326426228144-9tkefo2dc0qjt9pemhuq72g6h2nfil1u.apps.googleusercontent.com
        API Key			AIzaSyAhcEgKbXcji6C_iQ9ypoUo6lMi8sHUURc
        https://www.youtube.com/channel/UC_x5XG1OV2P6uZZ5FSM9Ttw*/

        final RelativeLayout layYT = (RelativeLayout) findViewById(R.id.layYT);
        final RelativeLayout layFB = (RelativeLayout) findViewById(R.id.layFB);

        final TextView butYT = (TextView) findViewById(R.id.butYT);
        final TextView butFB = (TextView) findViewById(R.id.butFB);

        layYT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                butYT.setTextColor(getResources().getColor(R.color.colorPrimary));
                butFB.setTextColor(Color.parseColor("#FFFFFF"));
                layYT.setBackgroundResource(R.drawable.select_dialog);
                layFB.setBackgroundResource(R.drawable.tab_dialog);
                linVid.removeAllViews();
                videosYT.moveToFirst();
                getYT();

                if (Dashboard.stat == 1) {
                    butFB.setText(getString(R.string.hindi_facebook));
                    butYT.setText(getString(R.string.hindi_youtube));
                    getSupportActionBar().setTitle(getString(R.string.hindi_youtube) + " " + title);
                } else {
                    butFB.setText(getString(R.string.facebook));
                    butYT.setText(getString(R.string.youtube));
                    getSupportActionBar().setTitle(getString(R.string.youtube) + " " + title);
                }
            }
        });

        layFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Dashboard.stat == 1) {
                    butFB.setText(getString(R.string.hindi_facebook));
                    butYT.setText(getString(R.string.hindi_youtube));
                    getSupportActionBar().setTitle(getString(R.string.hindi_facebook) + " " + title);
                } else {
                    butFB.setText(getString(R.string.facebook));
                    butYT.setText(getString(R.string.youtube));
                    getSupportActionBar().setTitle(getString(R.string.facebook) + " " + title);
                }
                butFB.setTextColor(getResources().getColor(R.color.colorPrimary));
                butYT.setTextColor(Color.parseColor("#FFFFFF"));
                layFB.setBackgroundResource(R.drawable.select_dialog);
                layYT.setBackgroundResource(R.drawable.tab_dialog);
                linVid.removeAllViews();
                videosFB.moveToFirst();
                getFB();

            }
        });


        linVid = (LinearLayout) findViewById(R.id.linVid);

        scrollVid = (ScrollView) findViewById(R.id.scrollVid);
        final Button butTop = (Button) findViewById(R.id.butTop);
        butTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollVid.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        scrollVid.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (scrollVid != null) {
                    if (scrollVid.getScrollY() == 0) {
                        butTop.setVisibility(View.GONE);
                    } else {
                        butTop.setVisibility(View.VISIBLE);
                    }
                    if (scrollVid.getChildAt(0).getBottom() <= (scrollVid.getHeight() + scrollVid.getScrollY())) {
                        //getMore(nextLink);
                        //Log.e("Scroll", "End");
                    }
                }
            }
        });

        linVid.removeAllViews();
        getYT();

        if (Dashboard.stat == 1) {
            butFB.setText(getString(R.string.hindi_facebook));
            butYT.setText(getString(R.string.hindi_youtube));
            getSupportActionBar().setTitle(getString(R.string.hindi_youtube) + " " + title);
        } else {
            butFB.setText(getString(R.string.facebook));
            butYT.setText(getString(R.string.youtube));
            getSupportActionBar().setTitle(getString(R.string.youtube) + " " + title);
        }
    }

    private void getFB() {

        Methods.showDialog(mContext, msg);
        if (linVid.getChildCount() == 0) {
            linVid.setVisibility(View.GONE);
            scrollVid.fullScroll(ScrollView.FOCUS_UP);
            videosFB.moveToFirst();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                text.setVisibility(View.VISIBLE);

                if (videosFB.getCount() > 0) {

                    text.setVisibility(View.GONE);
                    int i = videosFB.getPosition();
                    int c = 0;
                    while (i < videosFB.getCount() && c < 50) {

                        String id = videosFB.getString(videosFB.getColumnIndex("fb_video_id"));
                        String created_time = videosFB.getString(videosFB.getColumnIndex("fb_video_time"));
                        String videoThumbUri = videosFB.getString(videosFB.getColumnIndex("fb_video_thumb_uri"));
                        String message = videosFB.getString(videosFB.getColumnIndex("fb_video_desc"));

                        createChild(id, message, created_time, videoThumbUri, 1);

                        videosFB.moveToNext();
                        i++;
                        c++;
                    }

                    if (!videosFB.isLast() && c == 50) {
                        final Button loadMore = new Button(mContext);
                        loadMore.setText(load);
                        loadMore.setTextColor(getResources().getColor(R.color.colorPrimary));
                        loadMore.setBackgroundResource(R.drawable.child_dialog);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 0, 0, 10);
                        loadMore.setLayoutParams(params);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            loadMore.setElevation(10f);
                        }
                        linVid.addView(loadMore);

                        loadMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                linVid.removeView(loadMore);
                                getFB();
                            }
                        });
                    }
                    linVid.setVisibility(View.VISIBLE);
                }
            }
        }, 1000);

        Methods.hideDialog(2000);
    }

    private void getYT() {

        Methods.showDialog(mContext, msg);
        if (linVid.getChildCount() == 0) {
            linVid.setVisibility(View.GONE);
            scrollVid.fullScroll(ScrollView.FOCUS_UP);
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                text.setVisibility(View.VISIBLE);

                if (videosYT.getCount() > 0) {

                    text.setVisibility(View.GONE);
                    int i = videosYT.getPosition();
                    int c = 0;
                    while (i < videosYT.getCount() && c < 50) {

                        String id = videosYT.getString(videosYT.getColumnIndex("yt_video_id"));
                        String created_time = videosYT.getString(videosYT.getColumnIndex("yt_video_time"));
                        String videoThumbUri = videosYT.getString(videosYT.getColumnIndex("yt_video_thumb"));
                        String message = videosYT.getString(videosYT.getColumnIndex("yt_video_title"));

                        createChild(id, message, created_time, videoThumbUri, 2);

                        videosYT.moveToNext();
                        i++;
                        c++;
                    }

                    if (!videosYT.isLast() && c == 50) {
                        final Button loadMore = new Button(mContext);
                        loadMore.setText(load);
                        loadMore.setTextColor(getResources().getColor(R.color.colorPrimary));
                        loadMore.setBackgroundResource(R.drawable.child_dialog);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 0, 0, 10);
                        loadMore.setLayoutParams(params);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            loadMore.setElevation(10f);
                        }
                        linVid.addView(loadMore);

                        loadMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                linVid.removeView(loadMore);
                                getYT();
                            }
                        });
                    }
                    linVid.setVisibility(View.VISIBLE);
                }
            }
        }, 1000);

        Methods.hideDialog(2000);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void createChild(final String id, String msg, final String time, final String uri, final int tag) {

        RelativeLayout child = (RelativeLayout) inflater.inflate(R.layout.child_news, linVid, false);
        child.setBackgroundResource(R.drawable.child_dialog);

        ImageView logo = (ImageView) child.findViewById(R.id.imageLogo);
        Glide.with(mContext).load(uri).thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(logo);

        TextView textData = (TextView) child.findViewById(R.id.textData);
        Methods.setTags(textData, msg);

        TextView textDate = (TextView) child.findViewById(R.id.textDate);
        textDate.setText(Methods.getVideoDate(time, tag));

        ImageView imageShare = child.findViewById(R.id.imageShare);
        imageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareSubject = "";
                String shareData = "";

                if(tag == 1){
                    shareSubject = "Facebook Video (Uploaded: " + Methods.getVideoDate(time, tag) + ")";
                    shareData = "https://www.facebook.com/" + id;

                } else if(tag == 2){
                    shareSubject = "Youtube Video (Uploaded: " + Methods.getVideoDate(time, tag) + ")";
                    shareData = "https://www.youtube.com/watch?v=" + id;
                }
                //Log.e("Data", shareData);

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareData);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, send));
            }
        });

        child.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (tag == 1) {
                    startActivity(new Intent(mContext, Player.class).putExtra("videoID", id).putExtra("fbProfile", fbProfile));
                } else {
                    Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) mContext, ytDevKey, id, 0, true, false);

                    if (intent != null) {
                        if (canResolveIntent(intent)) {
                            startActivityForResult(intent, 1);
                        } else {
                            // Could not resolve the intent - must need to install or update the YouTube API service.
                            YouTubeInitializationResult.SERVICE_MISSING.getErrorDialog((Activity) mContext, 2).show();
                        }
                    }
                }
            }
        });

        linVid.addView(child);
    }

    private boolean canResolveIntent(Intent intent) {
        List<ResolveInfo> resolveInfo = getPackageManager().queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }
}
