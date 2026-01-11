<?php
include 'db_config.php';
$user_id = $_GET['user_id'];

if(isset($_POST['title'])) {
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
    $filename = "";
    $user_id = $conn->real_escape_string($_POST['user_id']);

    if(isset($_POST['image'])) {
        $base64Image = $_POST['image'];

        // Decode the base64-encoded image data
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
            $filename = uniqid() . '.jpg';

            // Set the complete path to save the image
            $filePath = $uploadPath . $filename;

            // Save the image to the specified path
            $success = file_put_contents($filePath, $imageData);

            if (!$success) {
                echo "Error saving the image.";
                exit;
            }
        } else {
            echo "Invalid image type.";
            exit;
        }
    }

    // Insert the property details into the database
    $sql = "INSERT INTO properties (title, location, price, type, description, pic,city,deposit,date,number,user_id) 
            VALUES ('$title', '$location', '$price', '$type', '$description', '$filename','$city','$deposit','$date','$number','$user_id')";

    if ($conn->query($sql) === TRUE) {
        echo "Property added successfully";
    } else {
        echo "Error: " . $sql . "<br>" . $conn->error;
    }

    $conn->close();
} else {
    echo "Form data is missing.";
}
?>
