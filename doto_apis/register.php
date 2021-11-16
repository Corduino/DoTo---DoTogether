<?php
include ('connection.php');

$name=$_POST["name"];
$email=$_POST["email"];
$password=$_POST["password"];
$bd=$_POST["bd"];
$gender=$_POST["gender"];
$location=$_POST["location"];
$bio=$_POST["bio"];
$pic=$_POST["pic"];
$latitude=$_POST["latitude"];
$longitude=$_POST["longitude"];

$sqlinsert ="insert into User ( name, email, password, birthday, gender, location, bio, pic, lat, longi) VALUES ('".$name."','".$email."','".$password."','".$bd."','".$gender."','".$location."','".$bio."','".$pic."','".$latitude."','".$longitude."' ) ";
$resultinser = $conn->query($sqlinsert);
$last_id = $conn->insert_id;

if(!$resultinser){
   echo("Error description: " . mysqli_error($conn));
}else{
   echo "Successfully Registered";
}
?>
