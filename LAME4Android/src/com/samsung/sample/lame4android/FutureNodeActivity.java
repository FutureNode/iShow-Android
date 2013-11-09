
package com.samsung.sample.lame4android;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.samsung.sample.lame4android.R;

import java.io.IOException;

public class FutureNodeActivity extends Activity {

    public static final String TAG = "FutureNode_FutureNodeActivity";

    private Button mCameraButton;
    private ImageView mPhotoImageView;
    private Button mSelectBGMusicButton;
    private Button mRecordAudioButton;
    private Button mRecordVideoButton;

    private Uri mPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mCameraButton = (Button)findViewById(R.id.camera_button);
        mCameraButton.setOnClickListener(mCameraOnClickListener);
        mPhotoImageView = (ImageView)findViewById(R.id.photo);
        mSelectBGMusicButton = (Button)findViewById(R.id.select_bgmusic);
        mSelectBGMusicButton.setOnClickListener(mSelectBGMusicOnClickListener);
        mRecordAudioButton = (Button)findViewById(R.id.recorder_audio);
        mRecordAudioButton.setOnClickListener(mRecordAudioOnClickListener);
        mRecordVideoButton = (Button)findViewById(R.id.recorder_audio);
        mRecordVideoButton.setOnClickListener(mRecordAudioOnClickListener);
    }

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
                mCameraButton.setVisibility(View.GONE);
                mRecordAudioButton.setVisibility(View.VISIBLE);
                mRecordVideoButton.setVisibility(View.VISIBLE);
                mPhotoImageView.setVisibility(View.VISIBLE);
                mPhotoImageView.setImageURI(mPhotoUri);
            } else if (requestCode == Utils.RECORD_AUDIO_ACTIVITY_REQUEST_CODE) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getApplicationContext(), data.getData());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
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
    }
}
