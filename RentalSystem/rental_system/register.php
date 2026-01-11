<?php
include 'db_config.php';

$username = $_POST['username'];
$email = $_POST['email'];
$mobileno = $_POST['mobileno'];
$password = $_POST['password'];
$sql = "INSERT INTO users (username, email,mobileno,password) VALUES ('$username', '$email','$mobileno','$password')";

if ($conn->query($sql) === TRUE) {
    echo "success";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();
?>