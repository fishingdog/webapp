# webapp
CSYE6225_A3

1. Authenticate

Before interacting with the assignment endpoints, ensure you are authenticated to obtain the necessary token.

Endpoint: /api/v1/auth/authenticate

Method: POST

Headers: Content-Type: application/json

Body:
{
    "email": "your_email@example.com",
    "password": "your_password"
}
Note: Save the token you receive in the response. This will be used for subsequent authenticated requests.

2. Create Assignment

Endpoint: /assignment

Method: POST

Headers:

    Content-Type: application/json
    Authorization: Bearer YOUR_TOKEN_HERE

Body:
{
   "title": "Sample Assignment",
   "description": "This is a description for the sample assignment.",
   "points": 5
}

3. Update Assignment

Endpoint: /assignment/{id} (Replace {id} with the actual ID of the assignment)

Method: PUT

Headers:

    Content-Type: application/json
    Authorization: Bearer YOUR_TOKEN_HERE

Body:
{
   "title": "Updated Assignment Title",
   "description": "Updated description for the sample assignment.",
   "points": 7
}

4. Delete Assignment

Endpoint: /assignment/{id} (Replace {id} with the actual ID of the assignment)

Method: DELETE

Headers: Authorization: Bearer YOUR_TOKEN_HERE
Tips and Troubleshooting

    If you receive a 403 Forbidden error, check your token and ensure you have the required permissions.
    Always check the response body for any error messages. They might provide clues on what went wrong.
    For any 400 Bad Request responses, validate your input against the API's expected input format.
    If you're using the environment setup in Postman, make sure to select the environment from the drop-down list at the top right.


