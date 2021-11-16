<?php
include ('connection.php');

$fav=$_POST["fav"];
$email=$_POST["email"];
  
$sqlinsert ="insert into Fav ( fav, email) VALUES ('".$fav."','".$email."' ) ";

$resultinser = $conn->query($sqlinsert);

if(!$resultinser){
   echo("Error description: " . mysqli_error($conn));
}else{
   echo "User added to favourites";
}
?>
