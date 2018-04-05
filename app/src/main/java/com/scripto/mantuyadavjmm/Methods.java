package com.scripto.mantuyadavjmm;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class Methods {

    static ProgressDialog pDialog;

    static String URL_MEMBER = "http://www.scriptoindia.com/jmm/jmm.php";
    static String URL_DATA = "http://www.scriptoindia.com/jmm/mantuyadav/data.php";

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void showDialog(Context context, String message) {

        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);
        pDialog.setMessage(message);

        if (!pDialog.isShowing())
            pDialog.show();
    }

    public static void hideDialog(int duration) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }
        }, duration);

    }

    public static String getPostDate(String time) {
        String dateString = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            sdf.setTimeZone(TimeZone.getTimeZone("IST"));
            SimpleDateFormat df = new SimpleDateFormat("MMMM d, yyyy h:mm a");
            Date dt = sdf.parse(time);
            long epoch = dt.getTime();
            dateString = df.format(new Date(epoch));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateString;
    }

    public static String getPhotoDate(String time) {
        String dateString = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            sdf.setTimeZone(TimeZone.getTimeZone("IST"));
            SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy h:mm a");
            Date dt = sdf.parse(time);
            long epoch = dt.getTime();
            dateString = df.format(new Date(epoch));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateString;
    }

    public static String getVideoDate(String time, int tag) {
        String dateString = null;

        SimpleDateFormat sdf = null;

        if (tag == 1) {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        } else if(tag == 2) {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        }
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));

        try {
            SimpleDateFormat df = new SimpleDateFormat("MMMM d, yyyy h:mm a");
            Date dt = sdf.parse(time);
            long epoch = dt.getTime();
            dateString = df.format(new Date(epoch));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateString;
    }

    public static void setTags(TextView pTextView, String pTagString) {
        SpannableString string = new SpannableString(pTagString);

        int start = -1;
        for (int i = 0; i < pTagString.length(); i++) {
            if (pTagString.charAt(i) == '#') {
                start = i;
            } else if (pTagString.charAt(i) == ' ' || (i == pTagString.length() - 1 && start != -1) || !Pattern.matches("[A-Za-z0-9-_]+\\b", String.valueOf(pTagString.charAt(i)))) {
                if (start != -1) {
                    if (i == pTagString.length() - 1) {
                        i++; // case for if hash is last word and there is no
                        // space after word
                    }

                    final String tag = pTagString.substring(start, i);
                    string.setSpan(new ClickableSpan() {

                        @Override
                        public void onClick(View widget) {
                            //Log.e("Clicked", tag);
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            // link color
                            ds.setColor(Color.parseColor("#00baff"));
                            ds.setUnderlineText(false);
                        }
                    }, start, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = -1;
                }
            }
        }

        pTextView.setMovementMethod(LinkMovementMethod.getInstance());
        pTextView.setText(string);
    }

    public static void errorConnection(final Context context) {
        new AlertDialog.Builder(context)
                .setIcon(R.mipmap.warning)
                .setTitle("\tAttention")
                .setMessage("\nNot Connected to Internet!!!")
                .setPositiveButton("CONNECT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setComponent(new ComponentName("com.android.settings",
                                "com.android.settings.Settings$DataUsageSummaryActivity"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        System.exit(0);
                    }
                })
                .show();
    }
}
