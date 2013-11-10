
package com.samsung.sample.lame4android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AddProgramActivity extends Activity {

    public static final String TAG = "FutureNode_AddProgramActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_program);
        Button addProgramButton = (Button)findViewById(R.id.add_program_button);
        addProgramButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean hasLogin = Utils.sCookie != null;
                Log.d(TAG, "hasLogin = " + hasLogin);
                if (hasLogin) {
                    Utils.addProgram(AddProgramActivity.this);
                } else {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

}
