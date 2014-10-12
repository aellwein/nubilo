CREATE TABLE IF NOT EXISTS tags (
    tag_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS bookmarks (
    bookmark_id INTEGER PRIMARY KEY AUTOINCREMENT,
    uri TEXT NOT NULL,
    title TEXT,
    description TEXT
);

CREATE TABLE IF NOT EXISTS tagged (
    tagged_id INTEGER PRIMARY KEY AUTOINCREMENT,
    bookmark INTEGER,
    tag INTEGER,
    FOREIGN KEY(bookmark) REFERENCES bookmarks(bookmark_id),
    FOREIGN KEY(tag) REFERENCES tags(tags_id)
);
