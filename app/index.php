<?php
require 'vendor/autoload.php';
if(1)
$stripe = new \Stripe\StripeClient('sk_test_51MWmAWAN6SGb0UM2Q779ZFV8IuLThGCZ7tMao8PBYWg5rVhTY1I7M0eKBYbxi3F17IJ2FAOPldLP7zl4eMR02ahy00U3G94ekP');


$customer = $stripe->customers->create(
[
        'name' => 'Name Of Test',
        'address' => [
        'line1' => 'Addres Demo',
        'postal_code' => '123123',
        'city' => 'Sligo',
        'state' => 'Sligo',
        'country' => 'Ireland'

        ]
]);
$ephemeralKey = $stripe->ephemeralKeys->create([
  'customer' => $customer->id,
], [
  'stripe_version' => '2022-08-01',
]);
$paymentIntent = $stripe->paymentIntents->create([
  'amount' => 1099,
  'currency' => 'eur',
  'description' => 'Payment for test',
  'customer' => $customer->id,
  'automatic_payment_methods' => [
    'enabled' => 'true',
  ],
]);

echo json_encode(
  [
    'paymentIntent' => $paymentIntent->client_secret,
    'ephemeralKey' => $ephemeralKey->secret,
    'customer' => $customer->id,
    'publishableKey' => 'pk_test_51MWmAWAN6SGb0UM210EmE3aLhc7I39xyR9pUVQcENjv1815rhrvKxPg3nk8B0NndbtHM7rXE8APYlShccWgRHgji00jB0n7kk1'
  ]
);
http_response_code(200);