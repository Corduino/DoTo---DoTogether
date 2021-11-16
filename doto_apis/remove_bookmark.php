<?php
include 'connection.php';
    
$id=$_POST["id"];
$email=$_POST["email"];

$data=array();
$sql ="Delete FROM Bookmark where  email='".$email."' AND task='".$id."' ";
$result = $conn->query($sql);

if(!$result){
   echo("Error description: " . mysqli_error($conn));
}
else{
    echo "Removed from Bookmarks";
}
?>