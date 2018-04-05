package com.scripto.mantuyadavjmm;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.regex.Pattern;

public class NewsFeed extends AppCompatActivity {

    Context mContext;
    LayoutInflater inflater;
    DBManager db;
    Cursor profile, newsfeed;
    ScrollView scrollFeed;
    LinearLayout linFeed;
    TextView text;
    String title, msg, load, fbProfile, fbToken, name, image, error, send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_news_feed);

        if(Dashboard.stat == 1){
            title = getString(R.string.hindi_news);
            msg = getString(R.string.hindi_wait);
            load = getString(R.string.hindi_load);
            error = getString(R.string.hindi_empty);
            send = getString(R.string.hindi_send);
        } else {
            title = getString(R.string.news);
            msg = getString(R.string.wait);
            load = getString(R.string.load);
            error = getString(R.string.empty);
            send = getString(R.string.send);
        }

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = this;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        db = new DBManager(mContext);

        Intent intent = getIntent();
        fbProfile = intent.getStringExtra("fbProfile");
        fbToken = intent.getStringExtra("fbToken");

        profile = db.getProfile();
        name = profile.getString(profile.getColumnIndex("FB_Profile_Name"));
        image = profile.getString(profile.getColumnIndex("FB_Profile_pic"));

        newsfeed = db.getNewsFeed();

        //Log.e("News Cursor " + newsfeed.getCount(), DatabaseUtils.dumpCursorToString(newsfeed));


        text = findViewById(R.id.errorNews);
        text.setText(error);

        linFeed = (LinearLayout) findViewById(R.id.linFeed);

        scrollFeed = (ScrollView) findViewById(R.id.scrollFeed);
        final Button butTop = (Button) findViewById(R.id.butTop);
        butTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollFeed.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        scrollFeed.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (scrollFeed != null) {
                    if (scrollFeed.getScrollY()==0) {
                        butTop.setVisibility(View.GONE);
                    } else {
                        butTop.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        getFeed();
    }

    @Override
    public boolean onSupportNavigateUp(){
        super.onBackPressed();
        return true;
    }

    private void getFeed() {

        Methods.showDialog(mContext, msg);
        if(linFeed.getChildCount() == 0){
            linFeed.setVisibility(View.GONE);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(newsfeed.getCount()>0) {

                    text.setVisibility(View.GONE);
                    int i = newsfeed.getPosition();
                    int c = 0;
                    while (i<newsfeed.getCount() && c < 50) {

                        String message = newsfeed.getString(newsfeed.getColumnIndex("news_text"));
                        String created_time = newsfeed.getString(newsfeed.getColumnIndex("news_time"));

                        createChild(message, created_time);
                        newsfeed.moveToNext();
                        i++;
                        c++;
                    }

                    if(!newsfeed.isLast() && c == 50){
                        final Button loadMore = new Button(mContext);
                        loadMore.setText(load);
                        loadMore.setTextColor(getResources().getColor(R.color.colorPrimary));
                        loadMore.setBackgroundResource(R.drawable.child_dialog);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0,0,0,10);
                        loadMore.setLayoutParams(params);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            loadMore.setElevation(10f);
                        }
                        linFeed.addView(loadMore);

                        loadMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                linFeed.removeView(loadMore);
                                getFeed();
                            }
                        });
                    }
                }

                linFeed.setVisibility(View.VISIBLE);
            }
        }, 500);

        Methods.hideDialog(2000);
    }

    private void createChild(final String msg, final String time) {

        RelativeLayout child = (RelativeLayout) inflater.inflate(R.layout.child_news, linFeed, false);
        child.setBackgroundResource(R.drawable.child_dialog);

        ImageView logo = (ImageView) child.findViewById(R.id.imageLogo);
        Glide.with(mContext).load(image).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(logo);

        TextView textData = (TextView) child.findViewById(R.id.textData);
        textData.setHighlightColor(Color.TRANSPARENT);
        textData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(msg, time);
            }
        });
        Methods.setTags(textData, msg);

        TextView textDate = (TextView) child.findViewById(R.id.textDate);
        textDate.setText(Methods.getPostDate(time));

        ImageView imageShare = child.findViewById(R.id.imageShare);
        imageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareData = msg;

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Facebook Post (Posted: " + Methods.getPostDate(time) + ")");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareData);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, send));
            }
        });

        child.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(msg, time);
            }
        });

        linFeed.addView(child);
    }

    private void showDialog(String msg, String time) {
        final Dialog showPost = new Dialog(mContext);
        showPost.requestWindowFeature(Window.FEATURE_NO_TITLE);
        showPost.setContentView(R.layout.dialog_showpost);

        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.95);
        showPost.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

        Rect displayRectangle = new Rect();
        Window window = showPost.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        ImageView photoPost = (ImageView) showPost.findViewById(R.id.photoPost);
        Glide.with(mContext).load(image).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(photoPost);

        TextView namePost = (TextView) showPost.findViewById(R.id.namePost);
        namePost.setText(name);

        TextView datePost = (TextView) showPost.findViewById(R.id.datePost);
        datePost.setText(Methods.getPostDate(time));

        final TextView dataPost = (TextView) showPost.findViewById(R.id.dataPost);
        dataPost.setHighlightColor(Color.TRANSPARENT);
        Methods.setTags(dataPost, msg);

        Button closeImage = (Button) showPost.findViewById(R.id.closeImage);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPost.dismiss();
            }
        });

        showPost.getWindow().setBackgroundDrawable(null);
        showPost.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


}
