package com.scripto.mantuyadavjmm;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import static android.view.View.GONE;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static int stat = 0;
    NavigationView navigationView;
    ActionBar actionBar;
    WebView textMarquee, textMarqueeHindi;
    Context mContext;
    DBManager db;
    Cursor profile;
    Handler mHandler = new Handler();
    String data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;
        db = new DBManager(mContext);
        profile = db.getProfile();

        actionBar = getSupportActionBar();
        //Log.e("Activity", "Entered");

        //final RelativeLayout splash = (RelativeLayout) findViewById(R.id.splash);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ImageView background = (ImageView) findViewById(R.id.background);
        Glide.with(mContext)
                .load(profile.getString(profile.getColumnIndex("Background")))
                .placeholder(R.drawable.progress_animation)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(background);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        textMarquee = (WebView) findViewById(R.id.textMarquee);
        textMarqueeHindi = (WebView) findViewById(R.id.textMarqueeHindi);

        textMarquee.setBackgroundColor(Color.parseColor(profile.getString(profile.getColumnIndex("marqueeBG"))));
        textMarqueeHindi.setBackgroundColor(Color.parseColor(profile.getString(profile.getColumnIndex("marqueeBG"))));

        setup(2);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);

        ToggleButton toggleLanguage;
        toggleLanguage = (ToggleButton) menu.findItem(R.id.language_toggle).getActionView().findViewById(R.id.toggle_language);
        toggleLanguage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //Your code when checked
                    //Toast.makeText(Dashboard.this, R.string.hindi_wait, Toast.LENGTH_LONG).show();
                    changeLanguage(1);


                } else {
                    //Your code when unchecked
                    //Toast.makeText(Dashboard.this, R.string.wait, Toast.LENGTH_LONG).show();
                    changeLanguage(2);
                }
            }
        });
        return true;
    }

    private void changeLanguage(int i) {
        stat = i;

        if (i == 1) {
            Methods.showDialog(mContext, getResources().getString(R.string.hindi_wait));

        } else {
            Methods.showDialog(mContext, getResources().getString(R.string.wait));
        }

        Methods.hideDialog(2000);

        setup(i);
    }

    private void setup(int i) {

        textMarquee.setVisibility(GONE);
        textMarqueeHindi.setVisibility(GONE);

        Menu menu = navigationView.getMenu();

        MenuItem introduction = menu.findItem(R.id.introduction);
        MenuItem manifesto = menu.findItem(R.id.manifesto);
        MenuItem achievements = menu.findItem(R.id.achievements);
        MenuItem news = menu.findItem(R.id.news);
        MenuItem upcoming = menu.findItem(R.id.upcoming);
        MenuItem members = menu.findItem(R.id.members);
        MenuItem results = menu.findItem(R.id.results);
        MenuItem videos = menu.findItem(R.id.videos);
        MenuItem photos = menu.findItem(R.id.photos);
        MenuItem contact = menu.findItem(R.id.contact);

        if (i == 1) {
            actionBar.setTitle(R.string.hindi_app_name);
            introduction.setTitle(R.string.hindi_intro);
            manifesto.setTitle(R.string.hindi_manifesto);
            achievements.setTitle(R.string.hindi_achievements);
            news.setTitle(R.string.hindi_news);
            upcoming.setTitle(R.string.hindi_upcoming);
            members.setTitle(R.string.hindi_members);
            results.setTitle(R.string.hindi_results);
            videos.setTitle(R.string.hindi_videos);
            photos.setTitle(R.string.hindi_photos);
            contact.setTitle(R.string.hindi_contact);
            textMarqueeHindi.loadData(profile.getString(profile.getColumnIndex("marqueeTextHindi")), "text/html; charset=UTF-8", null);
            setVisibilty(textMarqueeHindi);

        } else if (i == 2) {
            actionBar.setTitle(R.string.app_name);
            introduction.setTitle(R.string.intro);
            manifesto.setTitle(R.string.manifesto);
            achievements.setTitle(R.string.achievements);
            news.setTitle(R.string.news);
            upcoming.setTitle(R.string.upcoming);
            members.setTitle(R.string.members);
            results.setTitle(R.string.results);
            videos.setTitle(R.string.videos);
            photos.setTitle(R.string.photos);
            contact.setTitle(R.string.contact);
            textMarquee.loadData(profile.getString(profile.getColumnIndex("marqueeTextData")), "text/html; charset=UTF-8", null);
            setVisibilty(textMarquee);
        }


    }

    private void setVisibilty(final WebView webView) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.setVisibility(View.VISIBLE);
            }
        }, 2000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.language_toggle) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.introduction) {
            startActivity(new Intent(mContext, WebviewLoader.class).putExtra("tag", "1"));

        } else if (id == R.id.manifesto) {
            startActivity(new Intent(mContext, WebviewLoader.class).putExtra("tag", "2"));

        } else if (id == R.id.achievements) {
            startActivity(new Intent(mContext, WebviewLoader.class).putExtra("tag", "3"));

        } else if (id == R.id.news) {
            startActivity(new Intent(mContext, NewsFeed.class));

        } else if (id == R.id.upcoming) {
            startActivity(new Intent(mContext, WebviewLoader.class).putExtra("tag", "4"));

        } else if (id == R.id.members) {
            startActivity(new Intent(mContext, Members.class));

        } else if (id == R.id.results) {
            startActivity(new Intent(mContext, WebviewLoader.class).putExtra("tag", "5"));

        } else if (id == R.id.videos) {
            startActivity(new Intent(mContext, Videos.class));

        } else if (id == R.id.photos) {
            startActivity(new Intent(mContext, Photos.class));

        } else if (id == R.id.contact) {
            startActivity(new Intent(mContext, WebviewLoader.class).putExtra("tag", "6"));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
