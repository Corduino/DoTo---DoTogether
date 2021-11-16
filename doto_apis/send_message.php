<?php
include ('connection.php');

$sender=$_POST["sender"];
$receiver=$_POST["receiver"];
$msg=$_POST["msg"];
$time=$_POST["time"];
$chat_id=$_POST["chat_id"];
   
  
$sqlinsert ="insert into Messages ( sender, receiver, msg, time, chat_id) VALUES ('".$sender."','".$receiver."','".$msg."','".$time."','".$chat_id."') ";
$resultinser = $conn->query($sqlinsert);
$last_id = $conn->insert_id;

if(!$resultinser){
   echo("Error description: " . mysqli_error($conn));
}else{
   echo "Message sent";
}
?>
