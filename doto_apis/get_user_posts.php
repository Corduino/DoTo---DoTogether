<?php

include 'connection.php';

$email=$_POST["email"];

$data=array();
 
$sql ="SELECT * FROM Tasks where  email='".$email."' ";
$result = $conn->query($sql);

if(!$result){
    echo "error";
}
else{
    if($result->num_rows > 0)
    {
        while( $row = $result->fetch_assoc()){
            $data[]=$row;
        }
        echo json_encode($data);
    }
}
?>
