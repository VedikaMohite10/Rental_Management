<?php
include 'db_config.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $property_id = filter_var($_POST['property_id'], FILTER_VALIDATE_INT);
    $user_id = filter_var($_POST['user_id'], FILTER_VALIDATE_INT);

    if ($property_id === false || $user_id === false) {
        echo json_encode(array("status" => "error", "message" => "Invalid input."));
        exit;
    }

    $conn->autocommit(FALSE); // Start transaction

    try {
        // Delete from wishlist
        $sql_wishlist = "DELETE FROM wishlist WHERE property_id = ?";
        $stmt_wishlist = $conn->prepare($sql_wishlist);
        $stmt_wishlist->bind_param("i", $property_id);
        $stmt_wishlist->execute();
        $stmt_wishlist->close();

        // Delete from properties
        $sql_properties = "DELETE FROM properties WHERE id = ? AND user_id = ?";
        $stmt_properties = $conn->prepare($sql_properties);
        $stmt_properties->bind_param("ii", $property_id, $user_id);
        $stmt_properties->execute();
        $stmt_properties->close();

        $conn->commit(); // Commit transaction

        echo json_encode(array("status" => "success", "message" => "Property deleted successfully."));
    } catch (Exception $e) {
        $conn->rollback(); // Rollback transaction in case of error
        error_log("Error deleting property: " . $e->getMessage(), 3, "/var/log/php_errors.log");
        echo json_encode(array("status" => "error", "message" => "Error deleting property."));
    }

    $conn->close();
}
?>
