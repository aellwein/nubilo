#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#
# Copyright (c) 2014 Jürgen Fickel, Alex Ellwein
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import os
import sqlite3
from flask import app, session, abort, Flask


class Database():
    def __init__(self, logger, database_file='bookmarks.db', schema_file='schema_sqlite.sql'):
        """
        Constructs a new Database object.

        Args:
            app: Application instance of Nubilo core.
            database_file: SQLite database file which is the base for
                this Database object.
            schema_file: The file which contains the DDL statements for
                creating the tables of the Bookmarks plugin.
        Returns:
            A new Database object.
        """
        self._logger = logger
        self._database_file = database_file
        self._schema_file = schema_file
        self._database = None

    def open(self):
        self._connect()
        self._initialise_if_necessary()

    def _initialise_if_necessary(self):
        if not self._is_initialised():
            self._initialise()

    def _connect(self):
        """Connects to the database."""
        self._logger.debug("Connecting to SQLite database \"%s\"" % self._database_file)
        self._database = sqlite3.connect(self._database_file)
        self._database.row_factory = sqlite3.Row

    def _is_initialised(self):
        """Indicates whether the database is already initialised or not."""
        result = True
        if None is self._database:
            result = False
        else:
            cur = self._database.execute("SELECT name FROM sqlite_master WHERE type='table';")
            number_tables = len(cur.fetchall())
            result = 0 < number_tables
        self._logger.debug("Is database already initialised? %s" % result)
        return result

    def _initialise(self):
        """Initialises the database.

        This drops all existing data and should be used with care!
        """
        self._logger.debug("Initialising database \"%s\"." % self._database_file)
        ddl_script = open(os.path.abspath(os.path.join("plugins/bookmarks", self._schema_file)))
        self._database.cursor().executescript(ddl_script.read())
        ddl_script.close()
        self._database.commit()

    def get_all_bookmarks(self):
        """Returns a list with all bookmarks in the database.

        Returns:
            A list with Bookmark objects of all bookmarks which are contained
            in the database. If the database contains no bookmarks an empty
            list is returned.
        """
        def get_tag_names(bookmark_id):
            """Delivers the tag names for the given Bookmark ID.

            Args:
                bookmark_id: The ID of the Bookmark of which to get the tags
                for.

            Returns:
                A set containing the found tag names for bookmark_id.
            """
            result = set()
            # TODO This can definitely be optimised
            for tagged_entry in self._database.execute("SELECT tag FROM tagged WHERE bookmark = ?", (bookmark_id,)):
                for tag_entry in self._database.execute("SELECT name FROM tags WHERE tag_id = ?", (tagged_entry[0],)):
                    result.add(tag_entry[0])
            return result

        result = []
        for bookmark_entry in self._database.execute("SELECT bookmark_id, uri, title, description FROM bookmarks"):
            bookmark_id = bookmark_entry[0]
            bookmark_uri = bookmark_entry[1]
            bookmark_title = bookmark_entry[2]
            bookmark_description = bookmark_entry[3]
            bookmark_tags = get_tag_names(bookmark_id)
            result.append(Bookmark(bookmark_uri, bookmark_title, bookmark_description, bookmark_tags))
        return result

    def insert_bookmark(self, bookmark):
        """Inserts the given bookmark into the database.

        Args:
            bookmark: The Bookmark object to insert into the database.
        """
        def get_tag_id_for(tag):
            result = None
            cur.execute("SELECT tag_id FROM tags WHERE name = ?", (tag,))
            tag_id_entry = cur.fetchone()
            if None is not tag_id_entry:
                result = tag_id_entry[0]
            return result

        cur = self._database.cursor()
        cur.execute("INSERT INTO bookmarks (uri, title, description) VALUES (?, ?, ?)",
                (bookmark.uri, bookmark.title, bookmark.description))
        self._database.commit()

        bookmark_id = -1
        # TODO make this nice
        for bookmark_entry in self._database.execute("SELECT bookmark_id FROM bookmarks WHERE uri = ? AND title = ? AND description = ?", (bookmark.uri, bookmark.title, bookmark.description)):
            if None is not bookmark_entry:
                bookmark_id = bookmark_entry[0]

        # Insert new tag entry if it does not already exist
        for tag in bookmark.tags:
            tag_id = get_tag_id_for(tag)
            if None is tag_id:
                # TODO test this branch
                cur.execute("INSERT INTO tags (name) VALUES (?)", (tag,))
                self._database.commit()
                tag_id = get_tag_id_for(tag)
            cur.execute("INSERT INTO tagged (bookmark, tag) VALUES (?, ?)", (bookmark_id, tag_id))
        self._database.commit()

    def close(self):
        self._database.close()

class Bookmark():
    """
    """
    def __init__(self, uri="", title="", description="", tags=set()):
        self._uri = uri
        self._title = title
        self._description = description
        self._tags = tags

    @property
    def uri(self):
        """Gets the bookmark's URI."""
        return self._uri

    @property
    def title(self):
        """Gets the title of the bookmark."""
        return self._title

    @property
    def description(self):
        """Gets the description of the bookmark."""
        return self._description

    @property
    def tags(self):
        """Returns the set of tags of this bookmark."""
        return self._tags


class BookmarkManager():
    def __init__(self, logger, database):
        self._logger = logger
        self._database = database

    def start(self):
        self._database.open()

    def stop(self):
        self._database.close()

    #@app.route("/")
    def get_all_bookmarks(self):
        if not session.get("logged_in"):
            abort(401)
        return self._database.get_all_bookmarks()


bookmark_manager = None

def plugin_load(**kwargs):
    app = kwargs["app"]
    config = app.config["nubilo_config"]
    logger = app.config["nubilo_logger"]

    global bookmark_manager
    bookmark_manager = BookmarkManager(logger, Database(logger))
    bookmark_manager.start()


def plugin_unload(**kwargs):
    bookmark_manager.stop()
