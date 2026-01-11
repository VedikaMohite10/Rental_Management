<?php
include 'db_config.php';
$user_id = $_POST['user_id'];

$sql = "SELECT p.id as propertyId, p.title, p.location, p.price, p.type, p.description, p.city, p.deposit, p.date, p.number, p.pic FROM wishlist w JOIN properties p ON w.property_id = p.id WHERE w.user_id = '$user_id'";
$result = $conn->query($sql);

$data = array();

if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $data[] = $row;
    }
}

echo json_encode($data);

$conn->close();
?>