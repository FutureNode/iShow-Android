
package com.samsung.sample.lame4android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    public static final int RECORD_VIDEO_ACTIVITY_REQUEST_CODE = 400;
    public static final int SELECT_ACTIVITY_REQUEST_CODE = 500;
    public static final int LOGIN_ACTIVITY_REQUEST_CODE = 600;

    public static final String WEBSITE = "http://211.78.254.238/";
    public static final String WEBSITE_RECORD = WEBSITE + "record";
    public static final String WEBSITE_IMAGE = WEBSITE + "image";
    public static final String WEBSITE_VIDEO = WEBSITE + "video";
    public static final String WEBSITE_LOGIN = WEBSITE + "login/";
    public static final String WEBSITE_PROGRAM = WEBSITE + "program";
    public static final String WEBSITE_MUSIC = WEBSITE + "json/music.json";

    public static final String KEY_PROGRAM_ID = "programId";
    public static final String KEY_FILE = "file";

    public static Cookie sCookie;
    public static String sId;

    private static DefaultHttpClient getCookiedHttpClient() {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        CookieStore cookieStore = new BasicCookieStore();
        cookieStore.addCookie(sCookie);
        httpclient.setCookieStore(cookieStore);
        return httpclient;
    }

    public static void getMusicList() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                DefaultHttpClient httpclient = getCookiedHttpClient();

                HttpPost post = new HttpPost(WEBSITE_MUSIC);
                HttpContext localContext = new BasicHttpContext();
                try {
                    HttpResponse response = httpclient.execute(post, localContext);
                    HttpEntity entity = response.getEntity();
                    if (entity == null) {
                        Log.d(TAG, "entity = null");
                    } else {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                entity.getContent(), "UTF-8"));
                        StringBuilder strB = new StringBuilder();
                        String str;
                        while ((str = reader.readLine()) != null) {
                            Log.i(TAG, "sResponse = " + str);
                            strB.append(str);
                        }
                        //                        JSONObject jObj = new JSONObject(strB.toString()).getJSONObject("program");
                        //                        strB.delete(0, strB.length() - 1);
                        //                        sId = jObj.getString("_id");
                        //                        Log.i(TAG, "sId = " + sId);
                    }
                } catch (Exception e) {
                    Log.i(TAG, e.toString());
                    e.printStackTrace();
                }
                httpclient.getConnectionManager().shutdown();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
            }
        }.execute();
    }

    public static void addProgram(final Activity activity) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Utils.addProgram();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                Intent intent = new Intent(activity.getApplicationContext(),
                        FutureNodeActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        }.execute();
    }

    public static void addProgram() {
        Log.i(TAG, "Add program");
        DefaultHttpClient httpclient = getCookiedHttpClient();
        ;

        HttpPost post = new HttpPost(WEBSITE_PROGRAM);
        HttpContext localContext = new BasicHttpContext();
        try {
            HttpResponse response = httpclient.execute(post, localContext);
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                Log.d(TAG, "entity = null");
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        entity.getContent(), "UTF-8"));
                StringBuilder strB = new StringBuilder();
                String str;
                while ((str = reader.readLine()) != null) {
                    //                                        Log.i(TAG, "sResponse = " + str);
                    strB.append(str);
                }
                JSONObject jObj = new JSONObject(strB.toString()).getJSONObject("program");
                strB.delete(0, strB.length() - 1);
                sId = jObj.getString("_id");
                Log.i(TAG, "sId = " + sId);
            }
        } catch (Exception e) {
            Log.i(TAG, e.toString());
            e.printStackTrace();
        }
        httpclient.getConnectionManager().shutdown();
    }

    public static boolean login(final String username, final String password) {
        Log.i(TAG, "start login");
        // Upload Image
        DefaultHttpClient httpclient = new DefaultHttpClient();

        HttpPost post = new HttpPost(WEBSITE_LOGIN);
        HttpContext localContext = new BasicHttpContext();
        try {
            MultipartEntity multiEntity = new MultipartEntity();
            multiEntity.addPart("username", new StringBody(username));
            multiEntity.addPart("password", new StringBody(password));
            Log.d(TAG, "Login username " + username + " password " + password);
            post.setEntity(multiEntity);

            HttpResponse response = httpclient.execute(post, localContext);
            HttpEntity entity = response.getEntity();
            if (entity == null) {

            } else {
                Log.d(TAG, "Login form get: " + response.getStatusLine());
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        entity.getContent(), "UTF-8"));

                String sResponse;
                while ((sResponse = reader.readLine()) != null) {
                    Log.i(TAG, "sResponse = " + sResponse);
                }

                getCookie(httpclient);
                httpclient.getConnectionManager().shutdown();
                return true;
            }

            // When HttpClient instance is no longer needed, 
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
        } catch (Exception e) {
            Log.i(TAG, e.toString());
            e.printStackTrace();
        }
        httpclient.getConnectionManager().shutdown();
        return false;
    }

    public static void getCookie(DefaultHttpClient httpclient) {
        Log.d(TAG, "Get cookies");
        List<Cookie> cookies = httpclient.getCookieStore().getCookies();
        if (cookies.isEmpty()) {
            Log.d(TAG, "None");
        } else {
            for (int i = 0; i < cookies.size(); i++) {
                Log.d(TAG, "- " + cookies.get(i).toString());
            }
            sCookie = cookies.get(0);
        }
    }

    public static void uploadFile(final Uri uri, final String action) {
        uploadFile(new File(uri.getPath()), action);
    }

    public static void uploadFile(final File file, final String action) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                Log.i(TAG, "start upload file");

                DefaultHttpClient httpclient = getCookiedHttpClient();

                HttpPost post = new HttpPost(action);
                HttpContext localContext = new BasicHttpContext();
                try {
                    MultipartEntity multiEntity = new MultipartEntity();
                    FileBody fileBody = new FileBody(file);
                    multiEntity.addPart(KEY_PROGRAM_ID, new StringBody(sId));
                    multiEntity.addPart(KEY_FILE, fileBody);
                    post.setEntity(multiEntity);

                    HttpResponse response = httpclient.execute(post, localContext);
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
                return null;
            }
        }.execute();
    }

    /** Create a file Uri for saving an image or video */
    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    public static File getOutputMediaFile(int type) {
        File mediaStorageDir = getMediaStorageDir();
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

    public static Uri getThumbnailUri(Uri uri) {
        File thumbnailFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

        int THUMBNAIL_SIZE = 400;
        File image = new File(uri.getPath());

        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image.getPath(), bounds);
        if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
            return null;

        int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
                : bounds.outWidth;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize / THUMBNAIL_SIZE;
        Bitmap bitmap = BitmapFactory.decodeFile(image.getPath(), opts);
        writeIntoFile(thumbnailFile, bitmap);
        return Uri.fromFile(thumbnailFile);
    }

    private static void writeIntoFile(File file, Bitmap bitmap) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getMediaStorageDir() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "LifeShow");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }
        return mediaStorageDir;
    }
}
