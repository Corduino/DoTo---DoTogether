<?php

include ('connection.php');

$username = $_POST["username"];
$email = $_POST["email"];
$mysql_qry_verify = "SELECT * FROM User where  name='$username' and email='$email'";
$result = mysqli_num_rows(mysqli_query($conn ,$mysql_qry_verify));

if(!$result || $result == 0){
    echo "invalid";
} 
else if($result > 0){
    echo "success ";
}
?>