<?php
include 'db_config.php';

$username = $_POST['username'];
$password = $_POST['password'];

$sql = "SELECT * FROM users WHERE username='$username'AND password='$password'";
$result = mysqli_query($conn, $sql);
$tarray =array();

if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();
   
        $tarray['status'] = "success";
        $tarray['user_id'] = $row['id'];
    
} 
else {
    $tarray['status'] = "failure";
}
header('Content-Type:application/json');
echo json_encode($tarray);
$conn->close();
?>