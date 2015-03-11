<?php
/*// The data to send to the API
$arrayData = array(
    'data' => array('message' => 'haha it works'),
    'registration_ids' => array('APA91bF0GgnrSCg94lJ60hpfT-WBdBNdliQYhvQuYVq_wM1Zoh2Wy2LD4gMPWO6nYcyvT2trK_lA0FRQjbjlteV_UoEc5zXNy05oupccHABqGP9efJhNbq8I43jgzcrAykWObH8uUIUS4cDioOm5jVU1A9JM4FXvMA'));

// Create the context for the request
$context = array(
    'http' => array(
        // http://www.php.net/manual/en/context.http.php
        'method' => 'POST',
        'header' => "Authorization:key=AIzaSyCYe99fv9PGM7IKp76KlVz0bkb6KkMN1-I\r\n".
            "Content-Type: application/json\r\n",
        'content' => json_encode($arrayData)
    )
);

$context = @stream_context_create($context);
$result = @file_get_contents("https://android.googleapis.com/gcm/send", false, $context);

echo $result;*/

//------------------------------
// Payload data you want to send 
// to Android device (will be
// accessible via intent extras)
//------------------------------

$data = array( 'message' => 'Hello World!' );

//------------------------------
// The recipient registration IDs
// that will receive the push
// (Should be stored in your DB)
// 
// Read about it here:
// http://developer.android.com/google/gcm/
//------------------------------

$ids = array(
        'APA91bF0GgnrSCg94lJ60hpfT-WBdBNdliQYhvQuYVq_wM1Zoh2Wy2LD4gMPWO6nYcyvT2trK_lA0FRQjbjlteV_UoEc5zXNy05oupccHABqGP9efJhNbq8I43jgzcrAykWObH8uUIUS4cDioOm5jVU1A9JM4FXvMA'
    );

//------------------------------
// Call our custom GCM function
//------------------------------

sendGoogleCloudMessage(  $data, $ids );

//------------------------------
// Define custom GCM function
//------------------------------

function sendGoogleCloudMessage( $data, $ids )
{

    echo var_dump($_SERVER);
    //------------------------------
    // Replace with real GCM API 
    // key from Google APIs Console
    // 
    // https://code.google.com/apis/console/
    //------------------------------

    $apiKey = 'AIzaSyCYe99fv9PGM7IKp76KlVz0bkb6KkMN1-I';

    //------------------------------
    // Define URL to GCM endpoint
    //------------------------------

    $url = 'https://android.googleapis.com/gcm/send';

    //------------------------------
    // Set GCM post variables
    // (Device IDs and push payload)
    //------------------------------

    $post = array(
                    'registration_ids'  => $ids,
                    'data'              => $data,
                    );

    //------------------------------
    // Set CURL request headers
    // (Authentication and type)
    //------------------------------

    $headers = array( 
                        'Authorization: key=' . $apiKey,
                        'Content-Type: application/json'
                    );

    //------------------------------
    // Initialize curl handle
    //------------------------------

    $ch = curl_init();

    //------------------------------
    // Set URL to GCM endpoint
    //------------------------------

    curl_setopt( $ch, CURLOPT_URL, $url );

    //------------------------------
    // Set request method to POST
    //------------------------------

    curl_setopt( $ch, CURLOPT_POST, true );

    //------------------------------
    // Set our custom headers
    //------------------------------

    curl_setopt( $ch, CURLOPT_HTTPHEADER, $headers );

    //------------------------------
    // Get the response back as 
    // string instead of printing it
    //------------------------------

    curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );

    //------------------------------
    // Set post data as JSON
    //------------------------------

    curl_setopt( $ch, CURLOPT_POSTFIELDS, json_encode( $post ) );

    //------------------------------
    // Actually send the push!
    //------------------------------

    $result = curl_exec( $ch );

    //------------------------------
    // Error? Display it!
    //------------------------------

    if ( curl_errno( $ch ) )
    {
        echo 'GCM error: ' . curl_error( $ch );
    }

    //------------------------------
    // Close curl handle
    //------------------------------

    curl_close( $ch );

    //------------------------------
    // Debug GCM response
    //------------------------------

    echo $result;
}