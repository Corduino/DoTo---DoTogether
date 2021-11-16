<?php
include 'connection.php';
  
$email=$_POST["email"];

$data=array();
$sql ="SELECT * FROM Tasks WHERE `id` IN(SELECT task FROM Bookmark WHERE email='".$email."' )";
$result = $conn->query($sql);

if(!$result){
    echo "error"+$result;
}
else{
    if($result->num_rows > 0 )
    { 
        while( $row = $result->fetch_assoc()){
            $data[]=$row;
        }
        echo json_encode($data);
    }
}
?>
