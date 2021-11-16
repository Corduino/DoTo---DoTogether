<?php
include 'connection.php';

$id=$_POST["id"];

$data=array();
 
$sql ="SELECT * FROM User where  id='".$id."' ";
$result = $conn->query($sql);

if(!$result){
   echo("Error description: " . mysqli_error($conn));
}
else{
    if($result->num_rows > 0 )
    { 
        while( $row = $result->fetch_assoc()){
            $data[]=$row;
        }
        echo json_encode($data);
    }else{
      echo "invalid id";
    }
}
?>