<?php
include 'connection.php';
  
$data=array();

$sql ="SELECT DISTINCT chat_id FROM Messages ";
$result = $conn->query($sql);

if(!$result){
    echo "error"+$result;
}
else{
    if($result->num_rows > 0 ){ 
        while( $row = $result->fetch_assoc()){
            $data[]=$row;
        }
         echo json_encode($data);
    }
}
?>
