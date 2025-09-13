PRAGMA foreign_keys = ON;

CREATE TABLE server(
  id TEXT PRIMARY KEY,
  interface TEXT,
  first_three_octets TEXT,
  port INTEGER
);
CREATE TABLE user(
  id TEXT PRIMARY KEY,
  server TEXT REFERENCES server(id),
  name TEXT
);
