<?php
include 'db_config.php';

if (isset($_POST['property_id'])) {
    $property_id = $_POST['property_id'];

    // Sanitize inputs to prevent SQL injection
    $title = $conn->real_escape_string($_POST['title']);
    $location = $conn->real_escape_string($_POST['location']);
    $price = $conn->real_escape_string($_POST['price']);
    $type = $conn->real_escape_string($_POST['type']);
    $description = $conn->real_escape_string($_POST['description']);
    $city = $conn->real_escape_string($_POST['city']);
    $deposit = $conn->real_escape_string($_POST['deposit']);
    $date = $conn->real_escape_string($_POST['date']);
    $number = $conn->real_escape_string($_POST['number']);
    $user_id = $conn->real_escape_string($_POST['user_id']);
    $filename = "";

    // Retrieve existing image filename
    $sql = "SELECT pic FROM properties WHERE id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $property_id);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        $property = $result->fetch_assoc();
        $existingImage = $property['pic'];
    } else {
        echo "Property not found.";
        exit;
    }

    // Check if new image is provided
    if (isset($_POST['image']) && !empty($_POST['image'])) {
        $base64Image = $_POST['image'];
        $imageData = base64_decode($base64Image);

        // Validate the MIME type to ensure it's an image
        $finfo = new finfo(FILEINFO_MIME_TYPE);
        $mimeType = $finfo->buffer($imageData);
        $allowedMimeTypes = ['image/jpeg', 'image/png', 'image/gif'];

        if (in_array($mimeType, $allowedMimeTypes)) {
            // Set the path where you want to save the image
            $uploadPath = 'productpics/';

            // Create the 'productpics' directory if it doesn't exist
            if (!file_exists($uploadPath)) {
                mkdir($uploadPath, 0777, true);
            }

            // Generate a unique filename for the image
            $filename = uniqid() . '.' . explode('/', $mimeType)[1];

            // Set the complete path to save the image
            $filePath = $uploadPath . $filename;

            // Save the image to the specified path
            if (file_put_contents($filePath, $imageData) === false) {
                echo "Error saving the image.";
                exit;
            }

            // Delete old image file if a new image is uploaded
            if (!empty($existingImage)) {
                $oldFilePath = $uploadPath . $existingImage;
                if (file_exists($oldFilePath)) {
                    unlink($oldFilePath);
                }
            }
        } else {
            echo "Invalid image type.";
            exit;
        }
    } else {
        // Use the existing image if no new image is uploaded
        $filename = $existingImage;
    }

    // Prepare the SQL statement to prevent SQL injection
    $stmt = $conn->prepare("UPDATE properties SET title = ?, location = ?, price = ?, type = ?, description = ?, pic = ?, city = ?, deposit = ?, date = ?, number = ?, user_id = ? WHERE id = ?");

    // Bind parameters
    $stmt->bind_param("sssssssssssi", $title, $location, $price, $type, $description, $filename, $city, $deposit, $date, $number, $user_id, $property_id);

    if ($stmt->execute()) {
        echo "Property updated successfully";
    } else {
        echo "Error: " . $stmt->error;
    }

    $stmt->close();
    $conn->close();
} else {
    echo "Form data is missing.";
}
?>
