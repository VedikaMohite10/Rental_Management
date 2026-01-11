<?php
include 'db_config.php';

$user_id = $_POST['user_id'];
$property_id = $_POST['property_id'];

$sql = "DELETE FROM wishlist WHERE user_id = '$user_id' AND property_id = '$property_id'";
if ($conn->query($sql) === TRUE) {
    echo "Success";
} else {
    echo "Error: " . $conn->error;
}

$conn->close();
?>