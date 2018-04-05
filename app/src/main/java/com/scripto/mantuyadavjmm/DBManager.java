package com.scripto.mantuyadavjmm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mantu_yadav.db";
    private static final String TABLE_PROFILE = "profile";
    private static final String TABLE_NEWSFEED = "news_feed";
    private static final String TABLE_PHOTO = "photo";
    private static final String TABLE_FB_VIDEO = "fb_video";
    private static final String TABLE_YT_VIDEO = "yt_video";
    private static final String TABLE_MEMBER = "member";
    private static final String FB_Profile_ID = "FB_Profile_ID";
    private static final String FB_Profile_Name = "FB_Profile_Name";
    private static final String FB_Profile_pic = "FB_Profile_pic";
    private static final String FB_Token = "FB_Token";
    private static final String YT_channel = "YT_channel";
    private static final String YT_token = "YT_token";
    private static final String YT_dev_key = "YT_dev_key";
    private static final String introLink = "introLink";
    private static final String manifestoLink = "manifestoLink";
    private static final String achievementsLink = "achievementsLink";
    private static final String upcomingLink = "upcomingLink";
    private static final String resultLink = "resultLink";
    private static final String contactLink = "contactLink";
    private static final String introLinkHindi = "introLinkHindi";
    private static final String manifestoLinkHindi = "manifestoLinkHindi";
    private static final String achievementsLinkHindi = "achievementsLinkHindi";
    private static final String upcomingLinkHindi = "upcomingLinkHindi";
    private static final String resultLinkHindi = "resultLinkHindi";
    private static final String contactLinkHindi = "contactLinkHindi";
    private static final String marqueeBG = "marqueeBG";
    private static final String marqueeTextData = "marqueeTextData";
    private static final String marqueeTextHindi = "marqueeTextHindi";
    private static final String Background = "Background";
    private static final String NEWSID = "news_id";
    private static final String NEWSTEXT = "news_text";
    private static final String NEWSTIME = "news_time";
    private static final String PHOTOID = "photo_id";
    private static final String PHOTOSOURCE = "photo_source";
    private static final String PHOTONAME = "photo_name";
    private static final String PHOTOTIME = "photo_time";
    private static final String FBVIDEOID = "fb_video_id";
    private static final String FBVIDEODESC = "fb_video_desc";
    private static final String FBVIDEOTHUMBURI = "fb_video_thumb_uri";
    private static final String FBVIDEOTIME = "fb_video_time";
    private static final String YTVIDEOID = "yt_video_id";
    private static final String YTVIDEOTHUMB = "yt_video_thumb";
    private static final String YTVIDEOTITLE = "yt_video_title";
    private static final String YTVIDEOTIME = "yt_video_time";
    private static final String VOTERID = "voterid";
    private static final String NAME = "name";
    private static final String AGE = "age";
    private static final String PHONE = "phone";
    private static final String WARD = "ward";
    private static final String STATUS = "status";
    Context context;
    String CREATE_TABLE_PROFILE = "CREATE TABLE " + TABLE_PROFILE + " ( " +
            FB_Profile_ID + " TEXT, " +
            FB_Profile_Name + " TEXT, " +
            FB_Profile_pic + " TEXT, " +
            FB_Token + " TEXT, " +
            YT_channel + " TEXT, " +
            YT_token + " TEXT, " +
            YT_dev_key + " TEXT, " +
            introLink + " TEXT, " +
            manifestoLink + " TEXT, " +
            achievementsLink + " TEXT, " +
            upcomingLink + " TEXT, " +
            resultLink + " TEXT, " +
            contactLink + " TEXT, " +
            introLinkHindi + " TEXT, " +
            manifestoLinkHindi + " TEXT, " +
            achievementsLinkHindi + " TEXT, " +
            upcomingLinkHindi + " TEXT, " +
            resultLinkHindi + " TEXT, " +
            contactLinkHindi + " TEXT, " +
            marqueeBG + " TEXT, " +
            marqueeTextData + " TEXT, " +
            marqueeTextHindi + " TEXT, " +
            Background + " TEXT )";

    String CREATE_TABLE_NEWSFEED = "CREATE TABLE " + TABLE_NEWSFEED + " ( " +
            NEWSID + " TEXT, " +
            NEWSTEXT + " TEXT, " +
            NEWSTIME + " TEXT )";

    String CREATE_TABLE_PHOTO = "CREATE TABLE " + TABLE_PHOTO + " ( " +
            PHOTOID + " TEXT, " +
            PHOTOSOURCE + " TEXT, " +
            PHOTONAME + " TEXT, " +
            PHOTOTIME + " TEXT )";

    String CREATE_TABLE_FB_VIDEO = "CREATE TABLE " + TABLE_FB_VIDEO + " ( " +
            FBVIDEOID + " TEXT, " +
            FBVIDEODESC + " TEXT, " +
            FBVIDEOTHUMBURI + " TEXT, " +
            FBVIDEOTIME + " TEXT )";

    String CREATE_TABLE_YT_VIDEO = "CREATE TABLE " + TABLE_YT_VIDEO + " ( " +
            YTVIDEOID + " TEXT, " +
            YTVIDEOTHUMB + " TEXT, " +
            YTVIDEOTITLE + " TEXT, " +
            YTVIDEOTIME + " TEXT )";

    String CREATE_TABLE_MEMBER = "CREATE TABLE " + TABLE_MEMBER + " ( " +
            VOTERID + " TEXT, " +
            NAME + " TEXT, " +
            AGE + " TEXT, " +
            PHONE + " TEXT, " +
            WARD + " TEXT, " +
            STATUS + " TEXT )";

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWSFEED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FB_VIDEO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_YT_VIDEO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBER);

        db.execSQL(CREATE_TABLE_PROFILE);
        db.execSQL(CREATE_TABLE_NEWSFEED);
        db.execSQL(CREATE_TABLE_PHOTO);
        db.execSQL(CREATE_TABLE_FB_VIDEO);
        db.execSQL(CREATE_TABLE_YT_VIDEO);
        db.execSQL(CREATE_TABLE_MEMBER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWSFEED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FB_VIDEO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_YT_VIDEO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBER);

        onCreate(db);
    }

    public void addProfile(String FB_ID, String FB_Name, String FB_pic, String FB_Tokn,
                           String YT_chan, String YT_tokn, String YT_key,
                           String intro, String manifesto, String achievements,
                           String upcoming, String result, String contact,
                           String introHindi, String manifestoHindi, String achievementsHindi,
                           String upcomingHindi, String resultHindi, String contactHindi,
                           String marqueebg, String marqueeText, String marqueeHindi, String bgpic) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FB_Profile_ID, FB_ID);
        values.put(FB_Profile_Name, FB_Name);
        values.put(FB_Profile_pic, FB_pic);
        values.put(FB_Token, FB_Tokn);
        values.put(YT_channel, YT_chan);
        values.put(YT_token, YT_tokn);
        values.put(YT_dev_key, YT_key);
        values.put(introLink, intro);
        values.put(manifestoLink, manifesto);
        values.put(achievementsLink, achievements);
        values.put(upcomingLink, upcoming);
        values.put(resultLink, result);
        values.put(contactLink, contact);
        values.put(introLinkHindi, introHindi);
        values.put(manifestoLinkHindi, manifestoHindi);
        values.put(achievementsLinkHindi, achievementsHindi);
        values.put(upcomingLinkHindi, upcomingHindi);
        values.put(resultLinkHindi, resultHindi);
        values.put(contactLinkHindi, contactHindi);
        values.put(marqueeTextData, marqueeText);
        values.put(marqueeTextHindi, marqueeHindi);
        values.put(marqueeBG, marqueebg);
        values.put(Background, bgpic);

        db.insert(TABLE_PROFILE, null, values);
        db.close();

    }

    public void addNewsFeed(String News_ID, String News_text, String News_time) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NEWSID, News_ID);
        values.put(NEWSTEXT, News_text);
        values.put(NEWSTIME, News_time);

        db.insert(TABLE_NEWSFEED, null, values);
        db.close();

    }

    public void addPhoto(String Photo_ID, String Photo_source, String Photo_name, String Photo_time) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PHOTOID, Photo_ID);
        values.put(PHOTOSOURCE, Photo_source);
        values.put(PHOTONAME, Photo_name);
        values.put(PHOTOTIME, Photo_time);

        db.insert(TABLE_PHOTO, null, values);
        db.close();

    }

    public void addFBVideo(String Video_ID, String Video_desc, String Video_thumb, String Video_time) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FBVIDEOID, Video_ID);
        values.put(FBVIDEODESC, Video_desc);
        values.put(FBVIDEOTHUMBURI, Video_thumb);
        values.put(FBVIDEOTIME, Video_time);

        db.insert(TABLE_FB_VIDEO, null, values);
        db.close();

    }

    public void addYTVideo(String Video_ID, String Video_title, String Video_thumb, String Video_time) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(YTVIDEOID, Video_ID);
        values.put(YTVIDEOTITLE, Video_title);
        values.put(YTVIDEOTHUMB, Video_thumb);
        values.put(YTVIDEOTIME, Video_time);

        db.insert(TABLE_YT_VIDEO, null, values);
        db.close();

    }

    public void addMember(String voterID, String name, String phone, String age, String status, String ward) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(VOTERID, voterID);
        values.put(NAME, name);
        values.put(PHONE, phone);
        values.put(AGE, age);
        values.put(STATUS, status);
        values.put(WARD, ward);

        db.insert(TABLE_MEMBER, null, values);
        db.close();

    }

    public Cursor getProfile() {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "Select * from " + TABLE_PROFILE;
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        db.close();

        return cursor;
    }

    public Cursor getNewsFeed() {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "Select * from " + TABLE_NEWSFEED + " order by news_time desc";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        db.close();

        return cursor;
    }

    public Cursor getPhoto() {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "Select * from " + TABLE_PHOTO + " order by photo_time desc";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        db.close();

        return cursor;
    }

    public Cursor getFBVideo() {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "Select * from " + TABLE_FB_VIDEO + " order by fb_video_time desc";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        db.close();

        return cursor;
    }

    public Cursor getYTVideo() {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "Select * from " + TABLE_YT_VIDEO + " order by yt_video_time desc";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        db.close();

        return cursor;
    }

    public Cursor getMember(String data) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "Select * from " + TABLE_MEMBER;
        if (!data.equals(""))
            sql += " where " + NAME + " like '%" + data + "%'";

        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        db.close();

        return cursor;
    }

    /*public void updateMessage(String id, String stat, String sign) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues updateDetails = new ContentValues();

        updateDetails.put(MSG_STAT, stat);
        updateDetails.put(MSG_SIGN, sign);

        String where = MSG_ID + "= ?";
        db.update(TABLE_RESP, updateDetails, where, new String[]{id});

        //Log.e("Update", id + "\t" + stat + "\t" + sign)

        db.close();
    }*/

    public void deleteMember() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_MEMBER, null, null);

        db.close();
    }

    public void delete() {
        context.deleteDatabase(DATABASE_NAME);
    }
}