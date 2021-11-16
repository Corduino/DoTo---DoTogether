<?php
include 'connection.php';

$id=$_POST["id"];

$data=array();
$sql ="Delete FROM Tasks where  id='".$id."' ";
$result = $conn->query($sql);

if(!$result){
   echo("Error description: " . mysqli_error($conn));
}
else{
    echo "Task deleted";
}
?>