<?php

include ('connection.php');
$id=$_POST["id"];
$email=$_POST["email"];
  
$sqlinsert ="insert into Bookmark ( task, email) VALUES ('".$id."','".$email."' ) ";

$resultinser = $conn->query($sqlinsert);

$last_id = $conn->insert_id;

if(!$resultinser){
   echo("Error: " . mysqli_error($conn));
}else{
   echo "Task Bookmarked";
}
?>
