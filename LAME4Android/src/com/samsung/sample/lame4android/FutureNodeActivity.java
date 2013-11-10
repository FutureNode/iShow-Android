
package com.samsung.sample.lame4android;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;
import android.widget.AdapterView.OnItemClickListener;

import java.io.IOException;
import java.util.List;

public class FutureNodeActivity extends Activity {

    public static final String TAG = "FutureNode_FutureNodeActivity";

    private ImageButton mCameraButton;
    private ImageView mPhotoImageView;
    private Button mSelectBGMusicButton;
    private GridView mMoodGridView;
    private ImageButton mRecordAudioButton;
    private ImageButton mRecordVideoButton;
    private VideoView mVideoView;

    private Uri mPhotoUri;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mCameraButton = (ImageButton)findViewById(R.id.camera_button);
        mCameraButton.setOnClickListener(mCameraOnClickListener);
        mPhotoImageView = (ImageView)findViewById(R.id.photo);
        //        mSelectBGMusicButton = (Button)findViewById(R.id.select_bgmusic);
        //        mSelectBGMusicButton.setOnClickListener(mSelectBGMusicOnClickListener);
        mRecordAudioButton = (ImageButton)findViewById(R.id.recorder_audio);
        mRecordAudioButton.setOnClickListener(mRecordAudioOnClickListener);
        mRecordVideoButton = (ImageButton)findViewById(R.id.recorder_video);
        mRecordVideoButton.setOnClickListener(mRecordVideoOnClickListener);
        mVideoView = (VideoView)findViewById(R.id.videoview);
        initMoodGridView();
    }

    private void initMoodGridView() {
        mMoodGridView = (GridView)findViewById(R.id.mood_gridview);
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
            mMoodGridView.setAdapter(adapter);
            mMoodGridView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int selectionPosition = mMoodGridView.getSelectedItemPosition();
                    Log.d(TAG, "selectionPosition = " + selectionPosition);
                    if (selectionPosition == -1 || position != selectionPosition) {
                        playAudio(Utils.getMusicUri(((Data)parent.getItemAtPosition(position)).musicId));
                        mMoodGridView.setSelection(position);
                    } else {
                        mMediaPlayer.stop();
                        mMoodGridView.setSelection(-1);
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

    private OnClickListener mRecordVideoOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), RecordVideoActivity.class);
            startActivityForResult(intent, Utils.RECORD_VIDEO_ACTIVITY_REQUEST_CODE);
        }
    };
    private OnClickListener mRecordAudioOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), LameActivity.class);
            startActivityForResult(intent, Utils.RECORD_AUDIO_ACTIVITY_REQUEST_CODE);
        }
    };
    private OnClickListener mSelectBGMusicOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), SelectBGMusicActivity.class);
            startActivityForResult(intent, Utils.SELECT_ACTIVITY_REQUEST_CODE);
        }
    };

    private OnClickListener mCameraOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            mPhotoUri = Utils.getOutputMediaFileUri(Utils.MEDIA_TYPE_IMAGE);
            if (mPhotoUri == null) {
                setResult(RESULT_CANCELED);
                finish();
            }
            Log.d(TAG, "mPhotoUri = " + mPhotoUri.toString());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri); // set the image file name

            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high

            // start the Video Capture Intent
            startActivityForResult(intent, Utils.CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Utils.CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
                Uri thumbanilUri = Utils.getThumbnailUri(mPhotoUri);
                Log.d(TAG, "thumbanilUri = " + thumbanilUri.toString());
                Utils.uploadFile(thumbanilUri, Utils.WEBSITE_IMAGE);
                mCameraButton.setVisibility(View.GONE);
                mRecordAudioButton.setVisibility(View.VISIBLE);
                mRecordVideoButton.setVisibility(View.VISIBLE);
                mPhotoImageView.setVisibility(View.VISIBLE);
                mPhotoImageView.setImageURI(thumbanilUri);
            } else if (requestCode == Utils.RECORD_AUDIO_ACTIVITY_REQUEST_CODE) {
                playVideo(data.getData());
                //                playAudio(data.getData());
            } else if (requestCode == Utils.RECORD_VIDEO_ACTIVITY_REQUEST_CODE) {
                playVideo(data.getData());
            }
        }
    }

    private void playVideo(Uri uri) {
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.setVideoURI(uri);
        mVideoView.start();
        mVideoView.requestFocus();
    }
}
