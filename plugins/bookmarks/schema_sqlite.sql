DROP TABLE IF EXISTS tags;
CREATE TABLE tags (
    tags_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL
);

DROP TABLE IF EXISTS bookmarks;
CREATE TABLE bookmarks (
    bookmarks_id INTEGER PRIMARY KEY AUTOINCREMENT,
    uri TEXT NOT NULL,
    title TEXT,
    description TEXT
);

DROP TABLE IF EXISTS tagged;
CREATE TABLE tagged (
    tagged_id INTEGER PRIMARY KEY AUTOINCREMENT,
    bookmark INTEGER,
    tag INTEGER,
    FOREIGN KEY(bookmark) REFERENCES bookmarks(bookmark_id),
    FOREIGN KEY(tag) REFERENCES tags(tags_id)
);
