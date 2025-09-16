CREATE TABLE server
(
    id             UUID DEFAULT RANDOM_UUID() NOT NULL PRIMARY KEY,
    iface          TEXT UNIQUE                NOT NULL,
    public_key     TEXT                       NOT NULL,
    private_key    TEXT                       NOT NULL,
    public_address TEXT                       NOT NULL
);
CREATE TABLE peer
(
    id            UUID DEFAULT RANDOM_UUID() NOT NULL PRIMARY KEY,
    name          TEXT UNIQUE                NOT NULL,
    fourth_octet  INT                        NOT NULL,
    private_key   TEXT                       NOT NULL,
    public_key    TEXT                       NOT NULL,
    preshared_key TEXT                       NOT NULL,
    peer_config   TEXT                       NOT NULL,
    server_id     UUID                       NOT NULL,
    FOREIGN KEY (server_id) REFERENCES server (id)
);
