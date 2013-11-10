
package com.samsung.sample.lame4android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.view.Menu;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class RecordVideoActivity extends Activity {

    public static final String TAG = "FutureNode_RecordVideoActivity";

    public static final int ACTION_TAKE_VIDEO = 99;
    private Uri mVideoUri;
    private VideoView mVideoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lame);

        dispatchTakeVideoIntent();

    }

    private void dispatchTakeVideoIntent() {
        mVideoUri = Utils.getOutputMediaFileUri(Utils.MEDIA_TYPE_VIDEO);
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mVideoUri); // set the image file name

        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high
        startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
    }

    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_lame, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ACTION_TAKE_VIDEO) {
                Utils.uploadFile(mVideoUri, Utils.WEBSITE_VIDEO);
                Intent result = new Intent();
                result.setData(mVideoUri);
                setResult(RESULT_OK, result);
                finish();
            }
        }
    }
}
