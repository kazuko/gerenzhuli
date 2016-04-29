<?php
/**
 * Created by PhpStorm.
 * User: lenovo
 * Date: 2015/11/25
 * Time: 16:14
 */

require_once 'function.php';

$conn = connectDb();

$n = 0;
$result = mysqli_query($conn,"select * from reminder");
while ($row = mysqli_fetch_array($result)){
    $arr[$n++] = array("id" => $row['id'],
        "title" => $row['title'],
        "data" => $row['data'],
        "time" => $row['time'],
        "is_repeat" => $row['is_repeat'],
        "repeat_no" => $row['repeat_no'],
        "repeat_type" => $row['repeat_type'],
        "active" => $row['active'],
        "time_stamp" => $row['time_stamp']
    );

//    var_dump($row['time_stamp']);
}

//数组转换为JSON字符串
echo json_encode($arr);



