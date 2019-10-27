# SocialGroupApplication

This application representing design solution for some aspects of abstract social network.

The application requires **Cassandra** database version >= 3.11 to be installed.
Schemas and key sets will be created automatically. 

Following key set names are used:
> social_network
> social_network_test

For simplicity configuration properties are hardcode in **com.example.groups.Main** class

>Username: *cassandra* 
>Password: *cassandra*
>Host: localhost
>Port: 9042

For simplicity http server configuration is also hardcoded
>http://localhost:8080/
