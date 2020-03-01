# server
To run it in netbeans just put your IP address in the constructor for the client object in the main method of the Client project. 
Run the Server project, then the Client project. The server writes one string and then reads one string. 
If the user enters an incorrect password too many times the server shuts down. The PopulateDatabase() method in the Server project 
is the initial "database." So if you want to test things using the values in there you can. If the user that signed in is a patient 
they can only see their account balance. If the user is a doctor they have access to the database and the ability to change or add 
patients to the database.
