
package com.samsung.sample.lame4android;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.samsung.sample.lame4android.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LameActivity extends Activity {

    public static final String TAG = "FutureNode_LameActivity";

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lame);

        initRecorder();
        initEncoder(NUM_CHANNELS, SAMPLE_RATE, BITRATE, MODE, QUALITY);

        final Button button = (Button)findViewById(R.id.button);
        button.setText(startRecordingLabel);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!mIsRecording) {
                    button.setText(stopRecordingLabel);
                    mIsRecording = true;
                    mRecorder.startRecording();
                    mRawFile = getFile("raw");
                    startBufferedWrite(mRawFile);
                } else {
                    button.setText(startRecordingLabel);
                    mIsRecording = false;
                    mRecorder.stop();
                    mEncodedFile = getFile("mp3");
                    int result = encodeFile(mRawFile.getAbsolutePath(),
                            mEncodedFile.getAbsolutePath());
                    if (result == 0) {
                        String imagePath = mEncodedFile.getAbsolutePath();
                        Log.d(TAG, "file path = " + imagePath);
                        Toast.makeText(LameActivity.this, "Encoded to " + mEncodedFile.getName(),
                                Toast.LENGTH_SHORT).show();
                        //                                                Utils.uploadFile(mEncodedFile,Utils.FILE_TYPE_AUDIO);
                        Intent data = new Intent();
                        data.setData(Uri.fromFile(mEncodedFile));
                        setResult(RESULT_OK, data);
                        finish();
                    }
                }
            }
        });

        Button updateButton = (Button)findViewById(R.id.upload);
        updateButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //                Utils.uploadFile(new File("/storage/sdcard0/DCIM/doule_j0_.jpg"));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_lame, menu);
        return true;
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
                    Toast.makeText(LameActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } finally {
                    if (output != null) {
                        try {
                            output.flush();
                        } catch (IOException e) {
                            Toast.makeText(LameActivity.this, e.getMessage(), Toast.LENGTH_SHORT)
                                    .show();
                        } finally {
                            try {
                                output.close();
                            } catch (IOException e) {
                                Toast.makeText(LameActivity.this, e.getMessage(),
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
}
