<?php
include 'db_config.php';

header('Content-Type: application/json');

$user_id = $_GET['user_id']; // Ensure you properly handle this ID

$sql = "SELECT username, email, mobileno, password FROM users WHERE id='$user_id'";
$result = $conn->query($sql);

$response = array();

if ($result->num_rows > 0) {
    $response = $result->fetch_assoc();
} else {
    $response['error'] = "User not found";
}

echo json_encode($response);

$conn->close();
?>
