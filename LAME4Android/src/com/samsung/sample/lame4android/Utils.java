
package com.samsung.sample.lame4android;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static final String TAG = "FutureNode_Utils";

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    public static final String FILE_TYPE_IMAGE = "image";
    public static final String FILE_TYPE_AUDIO = "audio";
    public static final String FILE_TYPE_VIDEO = "video";

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    public static final int RECORD_AUDIO_ACTIVITY_REQUEST_CODE = 300;
    public static final int RECORD_VEDIO_ACTIVITY_REQUEST_CODE = 400;

    public void uploadFile(final File file, final String type) {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                Log.i(TAG, "start upload file");
                // Upload Image
                HttpClient client = new DefaultHttpClient();

                HttpPost post = new HttpPost("http://211.78.254.238/record/");
                HttpContext localContext = new BasicHttpContext();
                try {
                    MultipartEntity multiEntity = new MultipartEntity();
                    //            File f = new File(imagePath);
                    FileBody fileBody = new FileBody(file);
                    multiEntity.addPart("filetype", new StringBody(type));
                    multiEntity.addPart("file", fileBody);
                    post.setEntity(multiEntity);

                    HttpResponse response = client.execute(post, localContext);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response
                            .getEntity().getContent(), "UTF-8"));

                    String sResponse;
                    while ((sResponse = reader.readLine()) != null) {
                        Log.i(TAG, "sResponse = " + sResponse);
                    }
                } catch (Exception e) {
                    Log.i(TAG, e.toString());
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(r);
        thread.start();
    }

    /** Create a file Uri for saving an image or video */
    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    public static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp
                    + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp
                    + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}
