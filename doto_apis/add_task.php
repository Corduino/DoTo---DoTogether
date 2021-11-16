<?php

include ('connection.php');

$title=$_POST["title"];
$email=$_POST["email"];
$time=$_POST["time"];
$other=$_POST["other"];
$location=$_POST["location"];
  
$sqlinsert ="insert into Tasks ( title, email, time, other, location) VALUES ('".$title."','".$email."','".$time."','".$other."','".$location."' ) ";
$resultinser = $conn->query($sqlinsert);
$last_id = $conn->insert_id;

if(!$resultinser){
   echo("Error description: " . mysqli_error($conn));
}else{
   echo "Task Added";
}
?>
