<?php
include 'db_config.php';

$city = $_POST['city'];
$price_min = $_POST['price_min'];
$price_max = $_POST['price_max'];
$type = $_POST['type'];

$sql = "SELECT * FROM properties WHERE city='$city' AND price BETWEEN '$price_min' AND '$price_max' AND type='$type'";
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