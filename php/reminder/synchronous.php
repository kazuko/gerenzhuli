<?php
/**
 * Created by PhpStorm.
 * User: lenovo
 * Date: 2015/11/25
 * Time: 19:36
 */

require_once 'function.php';
$conn = connectDb();

$json = $_POST['content'];
$lastRefresh = $_POST['lastRefresh'];

echo $json;
echo $lastRefresh;
//
//$json ='[
//{
//    "active": "true",
//        "date": "27/11/2015",
//        "title": "A",
//        "is_repeat": "true",
//        "repeat_no": "5",
//        "repeat_type": "Minute",
//        "time": "16:25",
//        "time_stamp": "2015-11-27 03:25:51",
//        "id": 1
//    },
//{
//    "active": "true",
//        "date": "27/11/2015",
//        "title": "B",
//        "is_repeat": "true",
//        "repeat_no": "3",
//        "repeat_type": "Month",
//        "time": "16:30",
//        "time_stamp": "2015-11-27 03:26:31",
//        "id": 2
//    },
//{
//    "active": "true",
//        "date": "27/11/2015",
//        "title": "C",
//        "is_repeat": "true",
//        "repeat_no": "2",
//        "repeat_type": "Week",
//        "time": "17:26",
//        "time_stamp": "2015-11-27 03:26:58",
//        "id": 3
//    }
//]';
//$lastRefresh = "2010-00-00 00:00:00";

$arr = json_decode($json);


for($i=0;$i<sizeof($arr);$i++){

    $id = $arr[$i]->id;
    $title = $arr[$i]->title;
    $date = $arr[$i]->date;
    $time = $arr[$i]->time;
    $is_repeat = $arr[$i]->is_repeat;
    $repeat_no = $arr[$i]->repeat_no;
    $repeat_type = $arr[$i]->repeat_type;
    $active = $arr[$i]->active;
    $time_stamp = $arr[$i]->time_stamp;

    if($is_repeat == 'true')
        $is_repeat = 1;
    else
        $is_repeat = 0;

    if($active == 'true')
        $active = 1;
    else
        $active = 0;


    $result = mysqli_query($conn,"select * from reminder WHERE id = '$id'");
    $row = mysqli_fetch_array($result);


    if(!empty($row))
    {
        $sqlTimestamp = $row['time_stamp'];
        $appTimestamp = $arr[$i]->time_stamp;
        if (strtotime($sqlTimestamp) < strtotime($appTimestamp))
        {
            echo 'update';
            mysqli_query($conn, "UPDATE reminder SET title='$title',
                                                    date='$date',
                                                    time='$time',is_repeat='$is_repeat',
                                                   repeat_no='$repeat_no',
                                                   repeat_type='$repeat_type',
                                                    active='$active',
                                                    time_stamp='$time_stamp'
                                          WHERE id ='$id'",MYSQLI_STORE_RESULT);
        }
    }
    else
    {
        $appTimestamp = $arr[$i]->time_stamp;
        if(strtotime($lastRefresh)<strtotime($appTimestamp))
        {
            echo 'insert';
            mysqli_query($conn, "INSERT INTO reminder(`id`,`title`,`date`,`time`,`is_repeat`,`repeat_no`,`repeat_type`,`active`,`time_stamp`)
                                VALUES ('$id','$title','$date','$time','$is_repeat','$repeat_no','$repeat_type','$active','$time_stamp')",MYSQLI_STORE_RESULT);
        }
    }
}


$sqlArr = mysqli_query($conn,"select * from reminder");
//var_dump($sqlArr);
while($sqlRow = mysqli_fetch_array($sqlArr)){
    $is_in = 0;
    $sqlTimestamp = $sqlRow['time_stamp'];
    for($i=0;$i<sizeof($arr);$i++)
    {
        if ($sqlRow['id'] == $arr[$i]->id)
            $is_in = 1;
    }

    if(!$is_in&&(strtotime($sqlTimestamp) <= strtotime($lastRefresh)))
    {
        echo 'delete';
            $id = $sqlRow['id'];
            mysqli_query($conn,"DELETE FROM reminder WHERE id='$id'");
    }
}


echo '{"state":"SUCCESS"}';