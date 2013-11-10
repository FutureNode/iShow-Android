
package com.samsung.sample.lame4android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class SelectBGMusicActivity extends Activity {

    public static final String TAG = "FutureNode_SelectBGMusicActivity";

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_bgmusic);

        ArrayList<Data> datas = new ArrayList<Data>();
        Data data = new Data();
        data.type = "Classic";
        datas.add(data);
        data = new Data();
        data.type = "Jazz";
        datas.add(data);
        data = new Data();
        data.type = "Blue Jazz";
        datas.add(data);
        data = new Data();
        data.type = "Basanova";
        datas.add(data);
        MyAdapter adapter = new MyAdapter(getApplicationContext(), R.id.listview,
                R.layout.activity_lame, datas);

        mListView = (ListView)findViewById(R.id.listview);
        mListView.setAdapter(adapter);
        
        Utils.getMusicList();
    }

    public class Data {
        String type;
    }

}
