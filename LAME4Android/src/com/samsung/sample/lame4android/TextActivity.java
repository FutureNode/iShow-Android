
package com.samsung.sample.lame4android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class TextActivity extends Activity {

    public static final String TAG = "FutureNode_FutureNodeActivity";

    private EditText mEditText;
    private Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text);
        mEditText = (EditText)findViewById(R.id.text);

        mNextButton = (Button)findViewById(R.id.next);
        mNextButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.sText = mEditText.getText().toString();
                if (Utils.sText.length() > 0) {
                    Utils.uploadData(Utils.KEY_TEXT_ID, Utils.sText, Utils.WEBSITE_TEXT);
                }
                Intent intent = new Intent(getApplicationContext(), CreativeActivity.class);
                startActivity(intent);
            }
        });
    }

}
