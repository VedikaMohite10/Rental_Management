<?php
include 'db_config.php';

$user_id = $_POST['user_id'];
$username = $_POST['username'];
$email = $_POST['email'];
$mobileno = $_POST['mobileno'];
$password = $_POST['password'];

$response = array();

$sql = "UPDATE users SET username='$username', email='$email', mobileno='$mobileno', password='$password' WHERE id='$user_id'";

if ($conn->query($sql) === TRUE) {
    $response['success'] = true;
} else {
    $response['success'] = false;
    $response['message'] = 'Error updating profile: ' . $conn->error;
}

echo json_encode($response);

$conn->close();
?>
