<?php

//Les données du push (title, push_text)
$data = array('title' => "Préventes Run'INSA", 'push_text' => "Préventes Run'INSA ce midi à la sortie du self" );

//Les tokens des utilisateurs à qui envoyer le push
$tokens = array(
        'APA91bF0GgnrSCg94lJ60hpfT-WBdBNdliQYhvQuYVq_wM1Zoh2Wy2LD4gMPWO6nYcyvT2trK_lA0FRQjbjlteV_UoEc5zXNy05oupccHABqGP9efJhNbq8I43jgzcrAykWObH8uUIUS4cDioOm5jVU1A9JM4FXvMA'
    );

//La clé de l'API fournit par GCM
$apiKey = 'AIzaSyCYe99fv9PGM7IKp76KlVz0bkb6KkMN1-I';

$url = 'https://android.googleapis.com/gcm/send';

$header = "Authorization:key=".$apiKey."\r\n"."Content-Type: application/json\r\n";


// The data to send to the API
$arrayData = array(
    'data' => $data,
    'registration_ids' => $tokens);

// Create the context for the request
$context = array(
    'http' => array(
        // http://www.php.net/manual/en/context.http.php
        'method' => 'POST',
        'header' => $header,
        'content' => json_encode($arrayData)
    )
);

$context = @stream_context_create($context);
$result = @file_get_contents("https://android.googleapis.com/gcm/send", false, $context);

echo $result;