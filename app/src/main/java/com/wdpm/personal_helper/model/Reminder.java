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


package com.wdpm.personal_helper.model;

// Reminder class
public class Reminder {

    private int id;//事务
    private String title;//内容
    private String date;//年月日
    private String time;//时分
    private String is_repeat;//是否重复提醒
    private String repeat_no;//重复提醒的时间间隔
    private String repeat_type;//重复提醒的时间单位
    private String active;//是否响铃
    private String time_stamp;//时间戳

    public String getTimestamp() {
        return time_stamp;
    }

    public void setTimestamp(String Timestamp) {
        this.time_stamp = Timestamp;
    }


    public Reminder(int ID, String Title, String Date, String Time, String Repeat, String RepeatNo, String RepeatType, String Active, String TimeStamp) {
        id = ID;
        title = Title;
        date = Date;
        time = Time;
        is_repeat = Repeat;
        repeat_no = RepeatNo;
        repeat_type = RepeatType;
        active = Active;
        time_stamp = TimeStamp;
    }

    public Reminder(String Title, String Date, String Time, String Repeat, String RepeatNo, String RepeatType, String Active, String TimeStamp) {
        title = Title;
        date = Date;
        time = Time;
        is_repeat = Repeat;
        repeat_no = RepeatNo;
        repeat_type = RepeatType;
        active = Active;
        time_stamp = TimeStamp;
    }

    public Reminder() {
    }

    public int getID() {
        return id;
    }

    public void setID(int ID) {
        id = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String Title) {
        title = Title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String Date) {
        this.date = Date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String Time) {
        time = Time;
    }

    public String getRepeatType() {
        return repeat_type;
    }

    public void setRepeatType(String RepeatType) {
        repeat_type = RepeatType;
    }

    public String getRepeatNo() {
        return repeat_no;
    }

    public void setRepeatNo(String RepeatNo) {
        repeat_no = RepeatNo;
    }

    public String getRepeat() {
        return is_repeat;
    }

    public void setRepeat(String Repeat) {
        is_repeat = Repeat;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String Active) {
        active = Active;
    }
    @Override
    public String toString() {
        return "Reminder{" +
                "active='" + active + '\'' +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", repeat='" + is_repeat + '\'' +
                ", repeat_no='" + repeat_no + '\'' +
                ", repeat_type='" + repeat_type + '\'' +
                ", time_stamp='" + time_stamp + '\'' +
                '}';
    }


}
