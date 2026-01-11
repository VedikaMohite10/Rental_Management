<?php
include 'db_config.php';

$user_id = $_POST['user_id'];
$property_id = $_POST['property_id'];

// Check if property is already in the wishlist
$sql_check = "SELECT * FROM wishlist WHERE user_id='$user_id' AND property_id='$property_id'";
$result_check = $conn->query($sql_check);

if ($result_check->num_rows > 0) {
    echo "Property already in wishlist";
} else {
    $sql = "INSERT INTO wishlist (user_id, property_id) VALUES ('$user_id', '$property_id')";
    if ($conn->query($sql) === TRUE) {
        echo "1";
    } else {
        echo "0";
    }
}

$conn->close();
?>