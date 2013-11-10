
package com.samsung.sample.lame4android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.samsung.sample.lame4android.SelectBGMusicActivity.Data;

import java.util.List;

public class MyAdapter extends ArrayAdapter<Data> {

    public static final String TAG = "FutureNode_MyAdapter";
    private Bitmap mMoodOriginBitmap;
    private int mMoodCount = 7;
    private int mBitmapWidth;
    private int mBitmapHeight;

    public MyAdapter(Context context, int resource, int textViewResourceId, List<Data> objects) {
        super(context, resource, textViewResourceId, objects);
        mMoodOriginBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.mood);
        mBitmapWidth = mMoodOriginBitmap.getWidth() / mMoodCount;
        mBitmapHeight = mMoodOriginBitmap.getHeight();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = newView(parent);
        }
        bindView(position, convertView, parent);
        return convertView;
    }

    private View newView(ViewGroup parent) {
        return LayoutInflater.from(getContext()).inflate(R.layout.unit, parent, false);
    }

    private void bindView(int position, View view, ViewGroup parent) {
        Holder holder = (Holder)view.getTag();
        if (holder == null) {
            holder = new Holder();
            holder.imageView = (ImageView)view.findViewById(R.id.mood);
            holder.textView = (TextView)view.findViewById(R.id.type);
        }
        holder.imageView.setImageBitmap(getBitmap(position));
        holder.textView.setText(getItem(position).type);
        view.setTag(holder);
    }

    private Bitmap getBitmap(int position) {
        return Bitmap.createBitmap(mMoodOriginBitmap, mBitmapWidth * position, 0, mBitmapWidth,
                mBitmapHeight);
    }

    class Holder {
        ImageView imageView;
        TextView textView;
    }
}
