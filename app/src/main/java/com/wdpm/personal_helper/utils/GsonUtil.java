package com.wdpm.personal_helper.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.wdpm.personal_helper.db.ReminderDatabase;
import com.wdpm.personal_helper.model.Reminder;

import java.util.List;

/**
 * Created by 2341 on 2015/11/26.
 */
public class GsonUtil {

    static ReminderDatabase rb;

    public static  String remindersToJson(Context context) {

        Gson gson=new Gson();
        rb=new ReminderDatabase(context);
        List<Reminder> reminderList=rb.getAllReminders();
        String reminders=gson.toJson(reminderList);
        rb.close();
        return reminders;
    }


}
