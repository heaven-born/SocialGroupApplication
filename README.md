# SocialGroupApplication

This application representing design solution for some aspects of abstract social network.

The application requires **Cassandra** database version >= 3.11 to be installed.
Schemas and key sets will be created automatically. 

Following key set names are used

```
social_network
social_network_test
```

Database struture is also available in socail_network.cql file.


For simplicity configuration properties are hardcode in **com.example.groups.Main** class

```
Username: cassandra 
Password: cassandra
Host: localhost
Port: 9042
```

For simplicity http server configuration is also hardcoded
```
http://localhost:8080/
```

# Running

*sbt run* - starts http server on local host listening 8080 port

*sbt test* - executes some basic integration test. Tests require *Cassandra* DB running, but don't require http server.

# Scalability

Scalability up to 10M users is achievable by adding multiple HTTP server nodes behind load balancer.

Scalability on group up to 1M members and 100M total posts is acheavable in automated, semiautomated or manual mode. In order to preved overloading one node containging all records from a singlre group, I added "shard_id" filed into partition key on "post" table. Once some group beomces too big, it's possible to add new shard to table "shard". This new shard will be propogated to application automatically. The only restriction in current version is that default shard id for each group is not published in shard table  automatically, so it has to be added there manually ones group requires more that one shard. If default shard id will not be added with new shard, all data from default shard will become invisible for the application.


# APIs

POST http://localhost:8080/register-user 

Registers user in system and returns user UUID

```
{
    "name": "some_user_name"
}
```
------------------------
POST http://localhost:8080/register-group-member 

Registers group member (groupId - any number)

```
{
    "userId": "bc52de56-b159-4cba-9163-9e7fc5cfc4fb",
    "groupId": 5
}
```
------------------------
GET http://localhost:8080/all-groups-feed

Returns feed from all groups where **:user_uuid** is registered. Returns feed starting **:post_timestampuuid** and there will be **:number_of_posts** returned. 

```
userId=:user_uuid
start-from-post=:post_timestampuuid
number-posts-to-load=:number_of_posts default 100
```
-------------------------
GET http://localhost:8080/group-feed

Returns feed only from group **:group_id**. Fails if user not registered in **:group_id**

```
groupId=:group_id 
userId=:user_uuid
start-from-post=:post_timestampuuid
number-posts-to-load=:number_of_posts
```
-------------------------

GET http://localhost:8080/list-groups

Returns all groups where **:user_id** is registered. 

```
userId=:user_id
```

