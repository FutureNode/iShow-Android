
package com.samsung.sample.lame4android;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;

public class PhotoActivity extends Activity {

    public static final String TAG = "FutureNode_FutureNodeActivity";

    private ImageButton mCameraButton;
    private ImageButton mGalleryButton;
    private ImageView mPhotoImageView;
    private Button mNextButton;

    private Uri mPhotoUri;
    private MediaPlayer mMediaPlayer;

    private int SELECT_PICTURE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo);
        mCameraButton = (ImageButton)findViewById(R.id.camera_button);
        mCameraButton.setOnClickListener(new OnClickListener() {

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
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to 
                startActivityForResult(intent, Utils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
        mGalleryButton = (ImageButton)findViewById(R.id.gallery_button);
        mGalleryButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_PICTURE);
            }
        });

        mNextButton = (Button)findViewById(R.id.next);
        mNextButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Utils.sPhotoUri = " + Utils.sPhotoUri);
                if (Utils.sPhotoUri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                    Utils.uploadFile(
                            new File(getRealPathFromURI(getApplicationContext(), Utils.sPhotoUri)),
                            Utils.WEBSITE_IMAGE);
                } else {
                    Utils.uploadFile(Utils.sPhotoUri, Utils.WEBSITE_IMAGE);
                }
                Intent intent = new Intent(getApplicationContext(), TextActivity.class);
                startActivity(intent);
            }
        });

        mPhotoImageView = (ImageView)findViewById(R.id.photo);
        if (Utils.sPhotoUri != null) {
            setImage(Uri.parse("content://media/" + Utils.sPhotoUri.toString()));
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        ContentResolver cr = context.getContentResolver();
        String path = null;
        String scheme = contentUri.getScheme();
        if (scheme != null) {
            if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                String[] proj = { MediaStore.Images.Media.DATA };
                Cursor cursor = cr.query(contentUri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index);
                cursor.close();
                if (TextUtils.isEmpty(path)) {
                    path = contentUri.getPath();
                }
            } else if (scheme.equals(ContentResolver.SCHEME_FILE)) {
                path = contentUri.getPath();
            } else {
            }
        } else {
        }
        return path;
    }

    private void setImage(Uri uri) {
        mNextButton.setEnabled(true);
        mPhotoImageView.setVisibility(View.VISIBLE);
        mPhotoImageView.setImageURI(uri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Utils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                Uri thumbanilUri = Utils.getThumbnailUri(mPhotoUri);
                Log.d(TAG, "camera thumbanilUri = " + thumbanilUri.toString());
                Utils.sPhotoUri = thumbanilUri;
                setImage(Utils.sPhotoUri);
            } else if (requestCode == SELECT_PICTURE) {
                Uri thumbanilUri = data.getData();
                Log.d(TAG, "gallery thumbanilUri = " + thumbanilUri.toString());
                Utils.sPhotoUri = thumbanilUri;
                setImage(Utils.sPhotoUri);
            }
        }
    }
}
