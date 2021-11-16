<?php
include 'connection.php';

$fav=$_POST["fav"];
$email=$_POST["email"];

$data=array();
$sql ="Delete FROM Fav where  email='".$email."' AND fav='".$fav."' ";
$result = $conn->query($sql);

if(!$result){
    echo("Error description: " . mysqli_error($conn));
}
else{
      echo "Removed from favourites";
}
?>