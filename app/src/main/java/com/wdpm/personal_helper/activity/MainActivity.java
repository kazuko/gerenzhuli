/*
 * Copyright 2015 Blanyal D'Souza.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.wdpm.personal_helper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wdpm.personal_helper.R;
import com.wdpm.personal_helper.db.ReminderDatabase;
import com.wdpm.personal_helper.model.DateTimeSorter;
import com.wdpm.personal_helper.model.Reminder;
import com.wdpm.personal_helper.receiver.AlarmReceiver;
import com.wdpm.personal_helper.utils.Constant;
import com.wdpm.personal_helper.utils.DateAndTimeUtil;
import com.wdpm.personal_helper.utils.GsonUtil;
import com.wdpm.personal_helper.utils.HttpUtil;
import com.wdpm.personal_helper.utils.SharedPreferenceUtil;
import com.wdpm.personal_helper.utils.SnackbarUtil;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "MainActivity";
    private RecyclerView mList;
    private SimpleAdapter mAdapter;
    private Toolbar mToolbar;
    private TextView mNoReminderView;
    private FloatingActionButton mAddReminderButton;
    private LinkedHashMap<Integer, Integer> IDmap = new LinkedHashMap<>();
    private ReminderDatabase rb;
    private MultiSelector mMultiSelector = new MultiSelector();//multiSelect
    private AlarmReceiver mAlarmReceiver;

    //初始化各种控件，照着xml中的顺序写
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static final String FIRST_RUN = "first";
    private boolean first;

    SharedPreferenceUtil spu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        spu= new SharedPreferenceUtil(MainActivity.this, "LastRefresh");
        first = spu.getFirstInFlag(FIRST_RUN);
        if (first) {
            Toast.makeText(this, "The Application is first run",
                    Toast.LENGTH_LONG).show();
              spu.setKeyValue("lastRefresh", "1970-01-01 00:00:00");
        } else {
            Toast.makeText(this, "The Application is not first run",
                    Toast.LENGTH_LONG).show();
        }



        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerlayout);
//        mToolbar = (Toolbar) findViewById(R.id.id_toolbar);
        mNavigationView = (NavigationView) findViewById(R.id.id_navigationview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.id_swiperefreshlayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_blue_light, R.color.main_blue_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        // Initialize reminder database
        rb = new ReminderDatabase(getApplicationContext());

        // Initialize views
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mAddReminderButton = (FloatingActionButton) findViewById(R.id.add_reminder);
        mAddReminderButton.setStrokeVisible(true);
        mList = (RecyclerView) findViewById(R.id.reminder_list);
        mNoReminderView = (TextView) findViewById(R.id.no_reminder_text);

        // To check is there are saved reminders
        // If there are no reminders display a message asking the user to create reminders
        List<Reminder> mTest = rb.getAllReminders();

        if (mTest.isEmpty()) {
            mNoReminderView.setVisibility(View.VISIBLE);
        }

        // Create recycler view
        mList.setLayoutManager(getLayoutManager());
        registerForContextMenu(mList);
        mAdapter = new SimpleAdapter();
        mAdapter.setItemCount(getDefaultItemCount());
        mList.setAdapter(mAdapter);

        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.app_name);

        // On clicking the floating action button
        mAddReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ReminderAddActivity.class);
                startActivity(intent);
            }
        });

        // Initialize alarm
        mAlarmReceiver = new AlarmReceiver();
        // Setup toolbar

        // 对各种控件进行设置、适配、填充数据
        configViews();
    }

     // config View
    public void configViews() {
        // 设置显示Toolbar
//        setSupportActionBar(mToolbar);

        // 设置Drawerlayout开关指示器，即Toolbar最左边的那个icon
        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        //给NavigationView填充顶部区域，也可在xml中使用app:headerLayout="@layout/header_nav"来设置
        mNavigationView.inflateHeaderView(R.layout.header_nav);
        //给NavigationView填充Menu菜单，也可在xml中使用app:menu="@menu/menu_nav"来设置
        mNavigationView.inflateMenu(R.menu.menu_nav);

        // 自己写的方法，设置NavigationView中menu的item被选中后要执行的操作
        onNavgationViewMenuItemSelected(mNavigationView);

    }

    /**
     * 设置NavigationView中menu的item被选中后要执行的操作
     *
     * @param mNav
     */
    private void onNavgationViewMenuItemSelected(NavigationView mNav) {
        mNav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                String msgString = "";

                switch (menuItem.getItemId()) {
                    case R.id.nav_menu_home:
                        msgString = (String) menuItem.getTitle();
                        break;
                    case R.id.nav_menu_categories:
                        msgString = (String) menuItem.getTitle();
                        break;
                    case R.id.nav_menu_feedback:
                        msgString = (String) menuItem.getTitle();
                        break;
                    case R.id.nav_menu_setting:
                        msgString = (String) menuItem.getTitle();
                        break;
                }

                // Menu item点击后选中，并关闭Drawerlayout
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                // android-support-design兼容包中新添加的一个类似Toast的控件。
                SnackbarUtil.show(mDrawerLayout, msgString, 0);

                return true;
            }
        });
    }

    // Create context menu for long press actions
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
    }

    // Multi select items in recycler view
    private android.support.v7.view.ActionMode.Callback mDeleteMode = new ModalMultiSelectorCallback(mMultiSelector) {

        @Override
        public boolean onCreateActionMode(android.support.v7.view.ActionMode actionMode, Menu menu) {
            getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(android.support.v7.view.ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                // On clicking discard reminders
                case R.id.discard_reminder:
                    // Close the context menu
                    actionMode.finish();

                    // Get the reminder id associated with the recycler view item
                    for (int i = IDmap.size(); i >= 0; i--) {
                        if (mMultiSelector.isSelected(i, 0)) {
                            int id = IDmap.get(i);

                            // Get reminder from reminder database using id
                            Reminder temp = rb.getReminder(id);
                            // Delete reminder
                            rb.deleteReminder(temp);
                            // Remove reminder from recycler view
                            mAdapter.removeItemSelected(i);
                            //231
                            mAdapter.notifyItemRemoved(i);
                            // Delete reminder alarm
                            mAlarmReceiver.cancelAlarm(getApplicationContext(), id);
                        }
                    }

                    // Clear selected items in recycler view
                    mMultiSelector.clearSelections();
                    // Recreate the recycler items
                    // This is done to remap the item and reminder ids
                    mAdapter.onDeleteItem(getDefaultItemCount());

                    // Display toast to confirm delete
                    Toast.makeText(getApplicationContext(),
                            "Deleted",
                            Toast.LENGTH_SHORT).show();

                    // To check is there are saved reminders
                    // If there are no reminders display a message asking the user to create reminders
                    List<Reminder> mTest = rb.getAllReminders();

                    if (mTest.isEmpty()) {
                        mNoReminderView.setVisibility(View.VISIBLE);
                    } else {
                        mNoReminderView.setVisibility(View.GONE);
                    }

                    return true;

                // On clicking save reminders
                case R.id.save_reminder:
                    // Close the context menu
                    actionMode.finish();
                    // Clear selected items in recycler view
                    mMultiSelector.clearSelections();
                    return true;

                default:
                    break;
            }
            return false;
        }
    };

    // On clicking a reminder item
    private void selectReminder(int mClickID) {
        String mStringClickID = Integer.toString(mClickID);

        // Create intent to edit the reminder
        // Put reminder id as extra
        Intent i = new Intent(this, ReminderEditActivity.class);
        i.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, mStringClickID);
        startActivityForResult(i, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAdapter.setItemCount(getDefaultItemCount());
    }

    // Recreate recycler view
    // This is done so that newly created reminders are displayed
    @Override
    public void onResume() {
        super.onResume();

        // To check is there are saved reminders
        // If there are no reminders display a message asking the user to create reminders
        List<Reminder> mTest = rb.getAllReminders();

        if (mTest.isEmpty()) {
            mNoReminderView.setVisibility(View.VISIBLE);
        } else {
            mNoReminderView.setVisibility(View.GONE);
        }

        mAdapter.setItemCount(getDefaultItemCount());

    }

    // Layout manager for recycler view
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }

    protected int getDefaultItemCount() {
        return 100;
    }

    // Create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    // Setup menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // start licenses activity

//            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {

        // 刷新时数据的变化
        mSwipeRefreshLayout.setRefreshing(true);
        final AsyncHttpClient client = HttpUtil.getClient();

        client.get(Constant.GET_ALL_REMINDERS, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Toast.makeText(MainActivity.this,"从服务器获取数据成功",Toast.LENGTH_SHORT).show();
                String s = new String(bytes);
                System.out.println("GET_ALL_REMINDERS:" + s);
                List<Reminder> reminderList=null;
                int[] idInServer = new int[10000];
                int index=0;

                String lastRefresh = spu.getKeyValue("lastRefresh");
                Long lastRefereshTime = DateAndTimeUtil.getTime(lastRefresh);

                if (s.trim().equals("FALSE")) {
                    //the first time to get reminders from server,init lastRefreshTime
                    System.out.println("---------->server data is null");
                    //deal with the delete reminder in server
                    List<Reminder> reminderInSqlite = rb.getAllReminders();
                    for (Reminder r2 : reminderInSqlite) {
                        Log.i("r2-id",r2.getID()+"");
                        Boolean flag =false ;
                        for (int j=0;j<idInServer.length;j++){
                            if(r2.getID()==idInServer[j]){
                                flag=true;
                            }
                        }
                        Log.i("------------>", flag.toString());
                        //the reminder with this id has been deleted in server
                        if (!flag) {
                            Reminder rd = rb.getReminder(r2.getID());
                            String rd_time_stamp = rd.getTimestamp();
                            Long rdTimeStamp = DateAndTimeUtil.getTime(rd_time_stamp);
                            Log.i("rdTimeStamp-delete", rdTimeStamp + "");
                            Log.i("lastRefereshTime-delete", lastRefereshTime + "");
                            if (rdTimeStamp <= lastRefereshTime) {
                                //delete old reminder in sqlite
                                rb.deleteReminder(r2);
                                Log.i(TAG, "delete reminder");
                            } else {
                                //retain this reminder in sqlite,do nothing
                            }

                        }
                    }
                } else {
                    Gson gson = new Gson();
                    reminderList = gson.fromJson(s, new TypeToken<List<Reminder>>() {}.getType());

                    for (Reminder r : reminderList) {
                        int id = r.getID();
                        idInServer[index++] = id;
                        Log.i("idInServer---------->", "idInServer[" + (index-1) + "]" + "=" + id);
                        Boolean isExists = rb.isReminderAlreadyExists(id);

                        String time_stamp_in_server = r.getTimestamp();
                        Long timeStamp1 = DateAndTimeUtil.getTime(time_stamp_in_server);

                        //id exists,compare their time_stamp

                        if (isExists) {

                            Reminder reminder = rb.getReminder(id);
                            String time_stamp_in_sqlite = reminder.getTimestamp();
                            Long timeStamp2 = DateAndTimeUtil.getTime(time_stamp_in_sqlite);
                            Log.i("timeStamp1-update", timeStamp1 + "");
                            Log.i("lastRefereshTime-update", lastRefereshTime + "");

                            if (time_stamp_in_server.equals(time_stamp_in_sqlite)) {
                                //this reminder has not been changed,don't need to update

                            } else if (timeStamp1 > timeStamp2) {
                                //the reminder has been changed in server is the most newest
                                //need to update
                                int rows_update = rb.updateReminder(r);
                                Log.i(TAG, "rows_update:" + rows_update);

                            } else {
                                //the reminder in server is old,don't need to update
                            }
                        }
                        //id doesn't exists,it is new,insert it into sqlite
                        else {
                            Log.i("timeStamp1-insert", timeStamp1 + "");
                            Log.i("lastRefereshTime-insert", lastRefereshTime + "");
                            //represent it is a new inserted reminder in server
                            if (timeStamp1 > lastRefereshTime) {

                                int rows_insert = rb.addReminder(r);
                                Log.i(TAG, "rows_insert:" + rows_insert);
                            }

                        }

                    }

                    //deal with the delete reminder in server
                    List<Reminder> reminderInSqlite = rb.getAllReminders();
                    for (Reminder r2 : reminderInSqlite) {
                        Log.i("r2-id",r2.getID()+"");
                        Boolean flag =false ;
                        for (int j=0;j<idInServer.length;j++){
                            if(r2.getID()==idInServer[j]){
                                flag=true;
                            }
                        }
                        Log.i("------------>", flag.toString());
                        //the reminder with this id has been deleted in server
                        if (!flag) {
                            Reminder rd = rb.getReminder(r2.getID());
                            String rd_time_stamp = rd.getTimestamp();
                            Long rdTimeStamp = DateAndTimeUtil.getTime(rd_time_stamp);
                            Log.i("rdTimeStamp-delete", rdTimeStamp + "");
                            Log.i("lastRefereshTime-delete", lastRefereshTime + "");
                            if (rdTimeStamp <= lastRefereshTime) {
                                //delete old reminder in sqlite
                                rb.deleteReminder(r2);
                                Log.i(TAG, "delete reminder");
                            } else {
                                //retain this reminder in sqlite,do nothing
                            }

                        }
                    }
                }

                //        push all reminders to the server
                RequestParams params = new RequestParams();
                String str = GsonUtil.remindersToJson(MainActivity.this);
                System.out.println("current sqlite data is:---------------->" + str);
                params.put("content", str);
                params.put("lastRefresh", spu.getKeyValue("lastRefresh"));
                Log.i("PUSH-before", spu.getKeyValue("lastRefresh")+"----null---");
                client.post(Constant.PUSH_ALL_REMINDERS, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        String str = new String(bytes);
                        Log.i(TAG, "PUSH_ALL_REMINDERS---->" + "onSuccess" + str);
                        spu.setKeyValue("lastRefresh", DateAndTimeUtil.getCurrentTimeStamp());
                        Log.i("PUSH-after", spu.getKeyValue("lastRefresh"));
                        mAdapter.setItemCount(getDefaultItemCount());
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(MainActivity.this,"同步数据到服务器成功",Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        String str = new String(bytes);
                        Log.i(TAG, "PUSH_ALL_REMINDERS" + "onFailure" + str);
                    }
                });
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                System.out.println("GET_ALL_REMINDERS" + "onFailure" + new String(bytes));
                onCancel();
            }
        });



    }

    @Override
    protected void onStop() {
        super.onStop();
        if (first) {
            spu.setFirstInFlag(FIRST_RUN,false);
            Log.i(TAG,"setFirstInFlag");
        }
    }

    // Adapter class for recycler view
    public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.VerticalItemHolder> {
        private ArrayList<ReminderItem> mItems;

        public SimpleAdapter() {
            mItems = new ArrayList<>();
        }

        public void setItemCount(int count) {
            mItems.clear();
            mItems.addAll(generateData(count));
            notifyDataSetChanged();
        }

        public void onDeleteItem(int count) {
            mItems.clear();
            mItems.addAll(generateData(count));
        }

        public void removeItemSelected(int selected) {
            if (mItems.isEmpty()) return;
            mItems.remove(selected);
            notifyItemRemoved(selected);
        }

        // View holder for recycler view items
        @Override
        public VerticalItemHolder onCreateViewHolder(ViewGroup container, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            View root = inflater.inflate(R.layout.recycle_items, container, false);

            return new VerticalItemHolder(root, this);
        }

        @Override
        public void onBindViewHolder(VerticalItemHolder itemHolder, int position) {
            ReminderItem item = mItems.get(position);
            itemHolder.setReminderTitle(item.mTitle);
            itemHolder.setReminderDateTime(item.mDateTime);
            itemHolder.setReminderRepeatInfo(item.mRepeat, item.mRepeatNo, item.mRepeatType);
            itemHolder.setActiveImage(item.mActive);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        // Class for recycler view items
        public class ReminderItem {
            public String mTitle;
            public String mDateTime;
            public String mRepeat;
            public String mRepeatNo;
            public String mRepeatType;
            public String mActive;

            public ReminderItem(String Title, String DateTime, String Repeat, String RepeatNo, String RepeatType, String Active) {
                this.mTitle = Title;
                this.mDateTime = DateTime;
                this.mRepeat = Repeat;
                this.mRepeatNo = RepeatNo;
                this.mRepeatType = RepeatType;
                this.mActive = Active;
            }
        }

        // Class to compare date and time so that items are sorted in ascending order
        public class DateTimeComparator implements Comparator {
            DateFormat f = new SimpleDateFormat("dd/mm/yyyy hh:mm");

            public int compare(Object a, Object b) {
                String o1 = ((DateTimeSorter) a).getDateTime();
                String o2 = ((DateTimeSorter) b).getDateTime();

                try {
                    return f.parse(o1).compareTo(f.parse(o2));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }

        // UI and data class for recycler view items
        public class VerticalItemHolder extends SwappingHolder
                implements View.OnClickListener, View.OnLongClickListener {
            private TextView mTitleText, mDateAndTimeText, mRepeatInfoText;
            private ImageView mActiveImage, mThumbnailImage;
            private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
            private TextDrawable mDrawableBuilder;
            private SimpleAdapter mAdapter;

            public VerticalItemHolder(View itemView, SimpleAdapter adapter) {
                super(itemView, mMultiSelector);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
                itemView.setLongClickable(true);

                // Initialize adapter for the items
                mAdapter = adapter;

                // Initialize views
                mTitleText = (TextView) itemView.findViewById(R.id.recycle_title);
                mDateAndTimeText = (TextView) itemView.findViewById(R.id.recycle_date_time);
                mRepeatInfoText = (TextView) itemView.findViewById(R.id.recycle_repeat_info);
                mActiveImage = (ImageView) itemView.findViewById(R.id.active_image);
                mThumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnail_image);
            }

            // On clicking a reminder item
            @Override
            public void onClick(View v) {
                if (!mMultiSelector.tapSelection(this)) {
                    int mTempPost = mList.getChildAdapterPosition(v);

                    int mReminderClickID = IDmap.get(mTempPost);
                    selectReminder(mReminderClickID);

                } else if (mMultiSelector.getSelectedPositions().isEmpty()) {
                    mAdapter.setItemCount(getDefaultItemCount());
                }
            }

            // On long press enter action mode with context menu
            @Override
            public boolean onLongClick(View v) {
                AppCompatActivity activity = MainActivity.this;
                activity.startSupportActionMode(mDeleteMode);
                mMultiSelector.setSelected(this, true);
                return true;
            }

            // Set reminder title view
            public void setReminderTitle(String title) {
                mTitleText.setText(title);
                String letter = "A";

                if (title != null && !title.isEmpty()) {
                    letter = title.substring(0, 1);
                }

                int color = mColorGenerator.getRandomColor();

                // Create a circular icon consisting of  a random background colour and first letter of title
                mDrawableBuilder = TextDrawable.builder()
                        .buildRound(letter, color);
                mThumbnailImage.setImageDrawable(mDrawableBuilder);
            }

            // Set date and time views
            public void setReminderDateTime(String datetime) {
                mDateAndTimeText.setText(datetime);
            }

            // Set repeat views
            public void setReminderRepeatInfo(String repeat, String repeatNo, String repeatType) {
                if (repeat.equals("true")) {
                    mRepeatInfoText.setText("Every " + repeatNo + " " + repeatType + "(s)");
                } else if (repeat.equals("false")) {
                    mRepeatInfoText.setText("Repeat Off");
                }
            }

            // Set active image as on or off
            public void setActiveImage(String active) {
                if (active.equals("true")) {
                    mActiveImage.setImageResource(R.drawable.ic_notifications_on_white_24dp);
                } else if (active.equals("false")) {
                    mActiveImage.setImageResource(R.drawable.ic_notifications_off_grey600_24dp);
                }
            }
        }

        // Generate random test data
        public ReminderItem generateDummyData() {
            return new ReminderItem("望断缥缈", "11-12-2015 22:34", "true", "30", "Week", "true");
        }

        // Generate real data for each item
        public List<ReminderItem> generateData(int count) {
            ArrayList<SimpleAdapter.ReminderItem> items = new ArrayList<>();

            // Get all reminders from the database
            List<Reminder> reminders = rb.getAllReminders();

            // Initialize lists
            List<String> Titles = new ArrayList<>();
            List<String> Repeats = new ArrayList<>();
            List<String> RepeatNos = new ArrayList<>();
            List<String> RepeatTypes = new ArrayList<>();
            List<String> Actives = new ArrayList<>();
            List<String> DateAndTime = new ArrayList<>();
            List<Integer> IDList = new ArrayList<>();
            List<DateTimeSorter> DateTimeSortList = new ArrayList<>();

            // Add details of all reminders in their respective lists
            for (Reminder r : reminders) {
                Titles.add(r.getTitle());
                DateAndTime.add(r.getDate() + " " + r.getTime());
                Repeats.add(r.getRepeat());
                RepeatNos.add(r.getRepeatNo());
                RepeatTypes.add(r.getRepeatType());
                Actives.add(r.getActive());
                IDList.add(r.getID());
            }

            int key = 0;

            // Add date and time as DateTimeSorter objects
            for (int k = 0; k < Titles.size(); k++) {
                DateTimeSortList.add(new DateTimeSorter(key, DateAndTime.get(k)));
                key++;
            }

            // Sort items according to date and time in ascending order
            Collections.sort(DateTimeSortList, new DateTimeComparator());

            int k = 0;

            // Add data to each recycler view item
            for (DateTimeSorter item : DateTimeSortList) {
                int i = item.getIndex();

                items.add(new SimpleAdapter.ReminderItem(Titles.get(i), DateAndTime.get(i), Repeats.get(i),
                        RepeatNos.get(i), RepeatTypes.get(i), Actives.get(i)));
                IDmap.put(k, IDList.get(i));
                k++;
            }
            return items;
        }
    }
}
