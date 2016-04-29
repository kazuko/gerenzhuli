<?php
/**
 * 定义常用函数
 */

require_once 'config.php';

//连接数据库
function connectDb(){
    $conn = mysqli_connect(MYSQL_HOST,MYSQL_USER,MYSQL_PW);
    if(!$conn){
        die('Can not connect db');
    }
    mysqli_select_db($conn,'gerenzhuli');

    //设置字符集为utf8
    mysqli_query($conn,"SET NAMES 'utf8'");
    mysqli_query($conn,"SET CHARACTER SET utf8");
    mysqli_query($conn,"SET CHARACTER_SET_RESULT=utf8");

    return $conn;
}