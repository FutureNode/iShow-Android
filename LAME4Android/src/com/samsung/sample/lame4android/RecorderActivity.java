
package com.samsung.sample.lame4android;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RecorderActivity extends Activity {

    public static final String TAG = "FutureNode_FutureNodeActivity";

    static {
        System.loadLibrary("mp3lame");
    }

    private native void initEncoder(int numChannels, int sampleRate, int bitRate, int mode,
            int quality);

    private native void destroyEncoder();

    private native int encodeFile(String sourcePath, String targetPath);

    public static final int NUM_CHANNELS = 1;
    public static final int SAMPLE_RATE = 16000;
    public static final int BITRATE = 128;
    public static final int MODE = 1;
    public static final int QUALITY = 2;
    private AudioRecord mRecorder;
    private short[] mBuffer;
    private final String startRecordingLabel = "Start recording";
    private final String stopRecordingLabel = "Stop recording";
    private boolean mIsRecording = false;
    private File mRawFile;
    private File mEncodedFile;

    private MediaPlayer mMediaPlayer;

    private ImageView mPhotoImageView;
    private Button mUploadButton;
    private ImageButton mRecorderImageView;
    private ImageView mRedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);

        initRecorder();
        initEncoder(NUM_CHANNELS, SAMPLE_RATE, BITRATE, MODE, QUALITY);

        mMediaPlayer = Utils.sMediaPlayer;
        mUploadButton = (Button)findViewById(R.id.next);
        mUploadButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //                Utils.uploadData(Utils.KEY_TEXT_ID, Utils.sText, Utils.WEBSITE_TEXT);
                //                Intent intent = new Intent(getApplicationContext(), CreativeActivity.class);
                //                startActivity(intent);
            }
        });

        mRedImageView = (ImageView)findViewById(R.id.red);

        mRecorderImageView = (ImageButton)findViewById(R.id.recorder);
        mRecorderImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!mIsRecording) {
                    mRedImageView.setVisibility(View.VISIBLE);
                    mIsRecording = true;
                    mRecorder.startRecording();
                    mRawFile = getFile("raw");
                    startBufferedWrite(mRawFile);
                } else {
                    mRedImageView.setVisibility(View.GONE);
                    mIsRecording = false;
                    mRecorder.stop();
                    mEncodedFile = getFile("mp3");
                    int result = encodeFile(mRawFile.getAbsolutePath(),
                            mEncodedFile.getAbsolutePath());
                    if (result == 0) {
                        String imagePath = mEncodedFile.getAbsolutePath();
                        Log.d(TAG, "file path = " + imagePath);
                        Toast.makeText(RecorderActivity.this,
                                "Encoded to " + mEncodedFile.getName(), Toast.LENGTH_SHORT).show();
                        Utils.uploadFile(Uri.fromFile(mEncodedFile), Utils.WEBSITE_RECORD);
                        Intent data = new Intent();
                        data.setData(Uri.fromFile(mEncodedFile));
                        setResult(RESULT_OK, data);
                        finish();
                    }
                }
            }
        });

        mPhotoImageView = (ImageView)findViewById(R.id.photo);
        if (Utils.sPhotoUri != null) {
            mPhotoImageView.setImageURI(Utils.sPhotoUri);
        }
    }

    @Override
    public void onDestroy() {
        mRecorder.release();
        destroyEncoder();
        super.onDestroy();
    }

    private void initRecorder() {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        mBuffer = new short[bufferSize];
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
    }

    private void startBufferedWrite(final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataOutputStream output = null;
                try {
                    output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(
                            file)));
                    while (mIsRecording) {
                        int readSize = mRecorder.read(mBuffer, 0, mBuffer.length);
                        for (int i = 0; i < readSize; i++) {
                            output.writeShort(mBuffer[i]);
                        }
                    }
                } catch (IOException e) {
                    Toast.makeText(RecorderActivity.this, e.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                } finally {
                    if (output != null) {
                        try {
                            output.flush();
                        } catch (IOException e) {
                            Toast.makeText(RecorderActivity.this, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } finally {
                            try {
                                output.close();
                            } catch (IOException e) {
                                Toast.makeText(RecorderActivity.this, e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        }).start();
    }

    private File getFile(final String suffix) {
        Time time = new Time();
        time.setToNow();
        return new File(Environment.getExternalStorageDirectory() + "/FutureNode",
                time.format("%Y%m%d%H%M%S") + "." + suffix);
    }

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
