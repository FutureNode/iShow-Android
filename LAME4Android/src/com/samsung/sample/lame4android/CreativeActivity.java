
package com.samsung.sample.lame4android;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;

import java.io.IOException;
import java.util.List;

public class CreativeActivity extends Activity {

    public static final String TAG = "FutureNode_FutureNodeActivity";

    private MediaPlayer mMediaPlayer;

    private ListView mListView;
    private Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creative);

        mMediaPlayer = Utils.sMediaPlayer;
        mNextButton = (Button)findViewById(R.id.next);
        mNextButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick Utils.sData = " + Utils.sData);
                Log.d(TAG, "onClick Utils.sData.lifeId = " + Utils.sData.musicId);
                Utils.uploadData(Utils.KEY_MUSIC_ID, Utils.sData.musicId, Utils.WEBSITE_MUSIC);
                Intent intent = new Intent(getApplicationContext(), LameActivity.class);
                startActivity(intent);

            }
        });
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
                    R.layout.unit, dataList);
            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (!view.isSelected()) {
                        Log.d(TAG, "lifeId = " + ((Data)parent.getItemAtPosition(position)).musicId);
                        playAudio(Utils.getMusicUri(((Data)parent.getItemAtPosition(position)).musicId));
                        mListView.setSelection(position);
                        Utils.sData = (Data)parent.getItemAtPosition(position);
                    } else {
                        mMediaPlayer.stop();
                    }
                    mNextButton.setEnabled(true);
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

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        mMediaPlayer.pause();
        mListView.setSelection(-1);
    }
}
