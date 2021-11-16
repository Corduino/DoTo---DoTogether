<?php
include ('connection.php');
    
$title=$_POST["title"];
$id=$_POST["id"];
$time=$_POST["time"];
$other=$_POST["other"];
$location=$_POST["location"];
    
$sql ="UPDATE Tasks SET title='".$title."', time='".$time."',other='".$other."',location='".$location."'  where  id='".$id."' ";
$result = $conn->query($sql);
if(!$result){
   echo("Error description: " . mysqli_error($conn));
}
else{
    echo("Updated");
}
?>