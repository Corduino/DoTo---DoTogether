<?php

$servername = "localhost";
$user = "dotoserv_doto";
$pass = "dotoapp123456789";
$dbname = "dotoserv_doto";

$conn = new mysqli($servername, $user, $pass, $dbname);

if ($conn->connect_error) {
   echo "error";
   die("Connection failed: " . $conn->connect_error);
}
else{
  //echo "connected";
}
?>