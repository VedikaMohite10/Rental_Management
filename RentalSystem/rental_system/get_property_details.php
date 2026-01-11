<?php
include 'db_config.php';

if (isset($_GET['property_id'])) {
    $property_id = $_GET['property_id'];

    $sql = "SELECT * FROM properties WHERE id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $property_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows > 0) {
        $property = $result->fetch_assoc();

        // Ensure 'pic' is encoded correctly
        if (isset($property['pic'])) {
            $property['pic'] = base64_encode($property['pic']);
        }

        echo json_encode($property);
    } else {
        echo json_encode(["error" => "Property not found"]);
    }
} else {
    echo json_encode(["error" => "Invalid request"]);
}
?>
