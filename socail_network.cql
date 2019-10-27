DROP KEYSPACE IF EXISTS social_network;

CREATE KEYSPACE social_network
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 3};

drop table IF EXISTS posts;
CREATE TABLE posts (
    group_id int,
    shard_id uuid,
    post_id timeuuid,
    user_id uuid,
    user_name text,
    content text,
    PRIMARY KEY ((group_id,shard_id), post_id)
) WITH compaction = { 'class' : 'LeveledCompactionStrategy' };

drop table IF EXISTS shards;
CREATE TABLE shards (
    group_id int,
    shard_id uuid,
    PRIMARY KEY (group_id,shard_id)
) WITH compaction = { 'class' : 'LeveledCompactionStrategy' };

drop table IF EXISTS group_members;
CREATE TABLE group_members (
    user_id uuid,
    group_id int,
    PRIMARY KEY (user_id, group_id)
) WITH compaction = { 'class' : 'LeveledCompactionStrategy' };

drop table IF EXISTS users;
CREATE TABLE users (
    user_id uuid,
    user_name text,
    PRIMARY KEY (user_id)
) WITH compaction = { 'class' : 'LeveledCompactionStrategy' };