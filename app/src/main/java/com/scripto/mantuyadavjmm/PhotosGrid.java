package com.scripto.mantuyadavjmm;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

class PhotosGrid extends BaseAdapter {
    public ArrayList mThumbIds = new ArrayList<String>(), mImageLink = new ArrayList<String>(), mCreatedTime = new ArrayList<String>();
    Cursor photos;
    private Context mContext;

    public PhotosGrid(Context c, Cursor photo) {
        mContext = c;
        photos = photo;

        if (photos.getCount() > 0) {
            int i = 0;
            while (i < photos.getCount()) {

                String id = photos.getString(photos.getColumnIndex("photo_id"));
                String imageLink = photos.getString(photos.getColumnIndex("photo_source"));
                String createdTime = photos.getString(photos.getColumnIndex("photo_time"));

                mThumbIds.add(id);
                mImageLink.add(imageLink);
                mCreatedTime.add(createdTime);

                photos.moveToNext();
                i++;
            }
        }
    }

    @Override
    public int getCount() {
        return mThumbIds.size();
    }

    @Override
    public Object getItem(int position) {
        return mThumbIds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater li = ((Activity) mContext).getLayoutInflater();
        View child = li.inflate(R.layout.grid_image, null);

        TextView imageText = (TextView) child.findViewById(R.id.gridname);
        imageText.setText(Methods.getPhotoDate(String.valueOf(mCreatedTime.get(position))));

        ImageView image = (ImageView) child.findViewById(R.id.gridpicture);
        image.setImageBitmap(null);
        image.destroyDrawingCache();
        image.setMaxHeight(100);
        image.setMaxWidth(100);
        image.setImageResource(R.mipmap.loader);
        Glide.with(mContext).load(mImageLink.get(position)).crossFade().thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.ALL).into(image);

        return child;
    }


}
