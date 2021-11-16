<?php
include 'connection.php';
  
$email=$_POST["email"];

$data=array();

$sql ="SELECT * FROM Tasks WHERE `email` IN(SELECT fav FROM Fav WHERE email='".$email."' )";
$result = $conn->query($sql);

if(!$result){
    echo "error"+$result;
}
else{
    if($result->num_rows > 0 )        { 
        while( $row = $result->fetch_assoc()){
            $data[]=$row;
        }
        echo json_encode($data);
    }
}
?>
