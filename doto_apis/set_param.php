<?php

include ('connection.php');

$username = $_POST["username"];
$param_type = $_POST["param_type"];
$new_param = $_POST["new_param"];

$mysql_qry = "update `User` set $param_type = '$new_param' where name = '$username';";
$mysql_qry_verify = "select * from User where name ='$username';";

$result_num = mysqli_num_rows(mysqli_query($conn ,$mysql_qry));
$result_verify = mysqli_num_rows(mysqli_query($conn ,$mysql_qry_verify));
/*echo $qry_result;
echo mysqli_num_rows($verify_result);*/
if($result_verify == 0){
    echo "invalid";
} 
else if ($result_verify == 1){
    echo "success";
}
?>