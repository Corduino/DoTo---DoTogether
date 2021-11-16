<?php
include 'connection.php';
  
$chat_id1=$_POST["chat_id1"];
$chat_id2=$_POST["chat_id2"];

$data=array();
  
$sql ="SELECT * FROM Messages where chat_id='".$chat_id1."' OR chat_id='".$chat_id2."'";
$result = $conn->query($sql);

if(!$result){
    echo "error";
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
