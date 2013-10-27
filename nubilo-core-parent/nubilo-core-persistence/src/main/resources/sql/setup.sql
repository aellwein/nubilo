CREATE USER nubilo PASSWORD '23foo42';

CREATE TABLE IF NOT EXISTS digests (
    digest_id VARCHAR(64) PRIMARY KEY,
    denotation VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    uid VARCHAR(64) PRIMARY KEY,
    displayname VARCHAR(64),
    salt VARCHAR(64) NOT NULL,
    digest VARCHAR(64),
    FOREIGN KEY (digest) REFERENCES digests(digest_id),
    password VARCHAR(255) NOT NULL
);
