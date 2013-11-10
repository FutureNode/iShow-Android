
package com.samsung.sample.lame4android;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

    public static final String TAG = "FutureNode_LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Button submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Utils.sCookie == null) {
                    EditText usernameET = (EditText)findViewById(R.id.username);
                    EditText passwordET = (EditText)findViewById(R.id.password);
                    final String username = usernameET.getText().toString();
                    final String password = passwordET.getText().toString();
                    if (username != null && password != null) {
                        AsyncTask<Void, Void, Boolean> mLoginTask = new AsyncTask<Void, Void, Boolean>() {

                            @Override
                            protected void onPostExecute(Boolean result) {
                                super.onPostExecute(result);
                                if (result) {
                                    Utils.addProgram(LoginActivity.this);
                                } else
                                    Toast.makeText(getApplicationContext(), "Login fail!",
                                            Toast.LENGTH_LONG).show();
                            }

                            @Override
                            protected Boolean doInBackground(Void... params) {
                                return Utils.login(username, password);
                            }
                        };
                        mLoginTask.execute();
                    }
                }
            }
        });
    }
}
