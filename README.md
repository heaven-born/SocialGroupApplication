# SocialGroupApplication

This application represent a design solution for a few aspects of abstract social network.

Tech stack: Akka HTTP, cassandra, Phantom (orm), ZIO

The application requires **Cassandra** database version >= 3.11 which is embedded using sbt plugin (see Running section).
Both schemas and keyspaces will be created automatically on start. 

Following key set names are used

```
social_network
social_network_test
```

Copy of the database struture is also available in socail_network.cql file.


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

```sbt [-Drun_cassandra=true] run```  - starts http server on local host listening 8080 port and cassandra (optional)

```sbt [-Drun_cassandra=true] test``` - executes basic integration test.

# Scalability

Scalability up to 10M users is achievable by running multiple version of this application behind load balancer.

Scalability on group up to 1M members and 100M total posts is acheavable in automated, semiautomated or manual mode. In order to prevent overloading of one node containging all records from a singlre group, there is a "shard_id" field added on a partition key of "post" table. Once some group beomces too big, there is a possibility to insert new shard to table "shard". This new shard will be propogated to application automatically and allow record for a single group to be distrebuted across more than one partition key. 

The restriction in current version is that default shard id for each group is not published in shard table automatically, so it has to be added there manually ones group requires more that one shard. If default shard id will not be added with new shard, all data from default shard will become invisible for the application.

It may also worth to add some probability value to shards, so that new shards could be chosed for inserting data more often that for old shards.  

# Known issues

Requires some work on configuring blocking/non-blocing thread pools. Now all request to cassandra are considered as non-blocking. 

Shards information sharing and shceduling updates looks not very good now.

HTTP statuses are not used. All responces including errors return 200 status.

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

