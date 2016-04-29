package com.wdpm.personal_helper.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wdpm.personal_helper.R;
import com.wdpm.personal_helper.db.ReminderDatabase;
import com.wdpm.personal_helper.model.Reminder;
import com.wdpm.personal_helper.utils.Constant;
import com.wdpm.personal_helper.utils.DateAndTimeUtil;
import com.wdpm.personal_helper.utils.GsonUtil;
import com.wdpm.personal_helper.utils.HttpUtil;

import cz.msebera.android.httpclient.Header;

public class TestPush extends AppCompatActivity {

    private static final String TAG ="TestPush" ;
    private ReminderDatabase rb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_gson);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

//        AsyncHttpClient client = HttpUtil.getClient();
//        RequestParams params = new RequestParams();
//        String str=GsonUtil.remindersToJson(TestPush.this);
//        System.out.println(str);
//        params.put("content", str);
//        params.put("lastRefresh",DateAndTimeUtil.getCurrentTimeStamp());
//
//        client.post(Constant.TEST_PUSH, params, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int i, Header[] headers, byte[] bytes) {
//                String str=new String(bytes);
//                Log.i(TAG,"onSuccess"+str);
//
//            }
//
//            @Override
//            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//                String str=new String(bytes);
//                Log.i(TAG,"onFailure"+str);
//            }
//        });
        rb= new ReminderDatabase(this);
        Boolean b=rb.isReminderAlreadyExists(10000);
        Log.i(TAG,b.toString());
//        Reminder r2=rb.getReminder(30);
//        Log.i(TAG,r.toString());
    }

}
