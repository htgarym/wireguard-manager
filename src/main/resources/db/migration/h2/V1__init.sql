CREATE TABLE servers
(
    id                 UUID DEFAULT RANDOM_UUID() NOT NULL PRIMARY KEY,
    interface          TEXT UNIQUE,
    first_three_octets TEXT,
    port               INTEGER
);
CREATE TABLE users
(
    id           UUID DEFAULT RANDOM_UUID() NOT NULL PRIMARY KEY,
    name         TEXT UNIQUE                NOT NULL,
    fourth_octet INT                        NOT NULL,
    server_id    UUID                       NOT NULL,
    FOREIGN KEY (server_id) REFERENCES servers (id)
);
