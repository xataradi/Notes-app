package com.example.test;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    private Note_Detail context;
    private ArrayList<Bitmap> images;

    // Constructor
    public GridAdapter(Note_Detail c, ArrayList<Bitmap> images) {
        context = c;
        this.images = images;
    }
    public int getCount() {
        return images.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView=new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //kích thước hình trong gird view
        imageView.setLayoutParams(new GridView.LayoutParams(context.getScreenWidth()/2,context.getScreenWidth()/2));
        imageView.setImageBitmap(images.get(position));
        return imageView;
    }

}
