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

$sql ="UPDATE User SET name='".$name."', email='".$email."',password='".$password."',birthday='".$bd."',gender='".$gender."',location='".$location."',bio='".$bio."',pic='".$pic."',lat='".$latitude."',longi='".$longitude."'  where  email='".$email."' ";
$result = $conn->query($sql);
if(!$result){
    echo("Error description: " . mysqli_error($conn));
}
else{
    echo("Updated");
}
?>
