<?php
include 'db_config.php';

$user_id = $_POST['user_id'];


$sql = "SELECT * FROM properties WHERE user_id='$user_id' ";
$result = $conn->query($sql);

$properties = array();
if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $properties[] = $row;
    }
}

echo json_encode($properties);

$conn->close();
?>