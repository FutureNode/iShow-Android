
package com.samsung.sample.lame4android;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectBGMusicActivity extends Activity {

    public static final String TAG = "FutureNode_SelectBGMusicActivity";

    private MediaPlayer mMediaPlayer;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creative);
        mMediaPlayer = new MediaPlayer();
        mListView = (ListView)findViewById(R.id.listview);
        mGetMusicListTask.execute();
    }

    private AsyncTask<Void, Void, List<Data>> mGetMusicListTask = new AsyncTask<Void, Void, List<Data>>() {

        @Override
        protected List<Data> doInBackground(Void... params) {
            return Utils.getMusicList();
        }

        @Override
        protected void onPostExecute(List<Data> dataList) {
            super.onPostExecute(dataList);

            MyAdapter adapter = new MyAdapter(getApplicationContext(), R.id.listview,
                    R.layout.activity_lame, dataList);
            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int selectionPosition = mListView.getSelectedItemPosition();
                    Log.d(TAG, "selectionPosition = " + selectionPosition);
                    if (selectionPosition == -1 || position != selectionPosition) {
                        playAudio(Utils.getMusicUri(((Data)parent.getItemAtPosition(position)).musicId));
                        mListView.setSelection(position);
                    } else {
                        mMediaPlayer.stop();
                        mListView.setSelection(-1);
                    }
                }
            });
        }
    };

    private void playAudio(Uri uri) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(getApplicationContext(), uri);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
