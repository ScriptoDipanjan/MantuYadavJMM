package com.scripto.mantuyadavjmm;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertEquals;

public class Photos extends AppCompatActivity {

    String title, msg, load, error, send;
    LayoutInflater inflater;
    DBManager db;
    Cursor photo;
    GridView photos;
    private Context mContext;
    ArrayList photoIDs = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_photos);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (Dashboard.stat == 1) {
            title = getString(R.string.hindi_photos);
            msg = getString(R.string.hindi_wait);
            load = getString(R.string.hindi_load);
            error = getString(R.string.hindi_empty);
            send = getString(R.string.hindi_send);
        } else {
            title = getString(R.string.photos);
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

        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Photos.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        photo = db.getPhoto();

        if(photo.getCount() == 0){
            TextView text = findViewById(R.id.errorPhotos);
            text.setText(error);
        }

        photos = (GridView) findViewById(R.id.photos);
        photos.setVisibility(View.GONE);
        Methods.showDialog(mContext, msg);

        final Button butTop = (Button) findViewById(R.id.butTop);
        butTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photo.moveToFirst();
                photos.setAdapter(new PhotosGrid(mContext, photo));
                photos.smoothScrollToPosition(0);
            }
        });

        photos.setVerticalScrollBarEnabled(false);
        photos.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    butTop.setVisibility(View.GONE);
                } else
                    butTop.setVisibility(View.VISIBLE);
            }
        });

        hideDialog();
    }

    private void hideDialog() {
        photos.setAdapter(new PhotosGrid(mContext, photo));
        photos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long ids) {
                showImage(mContext, position);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                photos.setVisibility(View.VISIBLE);
            }
        }, 2400);

        Methods.hideDialog(2500);
    }

    private void showImage(final Context mContext, int position) {

        final Dialog showImage = new Dialog(mContext);
        showImage.requestWindowFeature(Window.FEATURE_NO_TITLE);
        showImage.setContentView(R.layout.dialog_showimage);

        Cursor tempPhoto = photo;
        tempPhoto.moveToPosition(position);

        final String photoID = tempPhoto.getString(tempPhoto.getColumnIndex("photo_id"));
        final String imageLink = tempPhoto.getString(tempPhoto.getColumnIndex("photo_source"));
        final String createdTime = tempPhoto.getString(tempPhoto.getColumnIndex("photo_time"));
        String imageName = tempPhoto.getString(tempPhoto.getColumnIndex("photo_name"));

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
        showImage.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);


        ImageView photoImage = (ImageView) showImage.findViewById(R.id.photoImage);

        Glide.with(mContext).load(imageLink)
                .crossFade()
                .thumbnail(0.5f)
                //.placeholder(R.mipmap.loader)
                //.bitmapTransform(new CircleTransform(mContext))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(photoImage);


        if (photoImage.getLayoutParams().height > 250) {
            photoImage.setLayoutParams(new LinearLayout.LayoutParams(250, 250));
        }

        TextView photoDate = (TextView) showImage.findViewById(R.id.photoDate);
        photoDate.setText(Methods.getPostDate(createdTime));

        final TextView nameImage = (TextView) showImage.findViewById(R.id.nameImage);
        nameImage.setHighlightColor(Color.TRANSPARENT);
        String inputText = imageName;

        if (inputText.length() == 0) {
            nameImage.setVisibility(View.GONE);

        } else if (inputText.length() > 150) {
            String text = inputText.substring(0, 150) + "... ";
            final String fulltext = inputText + "  ";

            final SpannableString ss = new SpannableString(text + "(More)");

            ClickableSpan span1 = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    // do some thing
                    SpannableString ss1 = new SpannableString(fulltext + "(Less)");
                    ClickableSpan span2 = new ClickableSpan() {
                        @Override
                        public void onClick(View textView) {
                            // do some thing
                            setTags(nameImage, ss);
                            nameImage.setMovementMethod(LinkMovementMethod.getInstance());

                        }
                    };
                    ss1.setSpan(span2, fulltext.length(), ss1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss1.setSpan(new ForegroundColorSpan(Color.BLUE), fulltext.length(), ss1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                    setTags(nameImage, ss1);
                    nameImage.setMovementMethod(LinkMovementMethod.getInstance());
                }
            };
            ss.setSpan(span1, 154, 160, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.BLUE), 154, 160, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            setTags(nameImage, ss);
            nameImage.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            setTags(nameImage, SpannableString.valueOf(inputText));
        }

        ImageView imageShare = showImage.findViewById(R.id.imageShare);
        imageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    new ShareTask(photoID, Methods.getPostDate(createdTime)).execute(imageLink);

            }
        });

        Button closeImage = (Button) showImage.findViewById(R.id.closeImage);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage.dismiss();
            }
        });

        showImage.getWindow().setBackgroundDrawable(null);
        showImage.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    private void setTags(TextView pTextView, SpannableString pTagString) {
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

                    //final String tag = pTagString.substring(start, i);
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

    class ShareTask extends AsyncTask<String, Void, File> {

        String photoID, postDate;

        private ShareTask(String photoID, String postDate) {
            this.photoID = photoID;
            this.postDate = postDate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Methods.showDialog(mContext, msg);
        }

        @Override
        protected File doInBackground(String... params) {
            String url = params[0]; // should be easy to extend to share multiple images at once
            try {
                // Glide v3

                return Glide
                        .with(mContext)
                        .load(url)
                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();



            } catch (Exception ex) {
                //Log.e("SHARE", "Sharing " + url + " failed");
                //Log.e("Exception", ex.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(File result) {
            if (result == null) {
                return;
            }


            File file = new File(result.getPath());

            byte[] b = new byte[(int) file.length()];
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                fileInputStream.read(b);

            } catch (FileNotFoundException e) {
                System.out.println("File Not Found.");
                e.printStackTrace();
            }
            catch (IOException e1) {
                System.out.println("Error Reading The File.");
                e1.printStackTrace();
            }

            File photo = new File(Environment.getExternalStorageDirectory(), "FB_" + photoID + ".jpg");

            if (photo.exists()) {
                photo.delete();
            }

            try {
                FileOutputStream fos = new FileOutputStream(photo.getPath());
                fos.write(b);
                fos.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
                //Log.e("PictureDemo", "Exception in photoCallback", e);
            }

            Uri uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", photo);
            //Log.e("URI", uri.getPath() + " "+uri.getAuthority()+ "file Name" + result.getName());

            share(uri, photoID); // startActivity probably needs UI thread
            Methods.hideDialog(500);


        }

        private void share(Uri result, String photoID) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/jpeg");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Facebook Photo (Uploaded: " + postDate + ")");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.facebook.com/" + photoID);
            shareIntent.putExtra(Intent.EXTRA_STREAM, result);
            mContext.startActivity(Intent.createChooser(shareIntent, send));
            photoIDs.add(photoID);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for(int i=0; i<photoIDs.size(); i++)
            new File(Environment.getExternalStorageDirectory(), "FB_" + photoIDs.get(i) + ".jpg").delete();
    }
}
