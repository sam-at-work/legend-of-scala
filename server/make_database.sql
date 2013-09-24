CREATE TABLE adventurers(
    id INTEGER PRIMARY KEY NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,

    x INTEGER NOT NULL,
    y INTEGER NOT NULL,
    realm_id INTEGER NOT NULL,

    hp INTEGER NOT NULL,
    xp INTEGER NOT NULL,
    level INTEGER NOT NULL,

    FOREIGN KEY(realm_id) REFERENCES realms(id)
);

CREATE TABLE realms(
    id INTEGER PRIMARY KEY NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE items(
    id INTEGER PRIMARY KEY NOT NULL,
    kind VARCHAR(255) NOT NULL,
    owner_id INTEGER,
    attrs TEXT,

    FOREIGN KEY(owner_id) REFERENCES adventurers(id) ON DELETE CASCADE
);

CREATE TABLE features(
    id INTEGER PRIMARY KEY NOT NULL,

    x INTEGER NOT NULL,
    y INTEGER NOT NULL,
    realm_id INTEGER NOT NULL,

    kind VARCHAR(255),
    attrs TEXT,

    FOREIGN KEY(realm_id) REFERENCES realms(id)
);

INSERT INTO realms(name) VALUES("world");
