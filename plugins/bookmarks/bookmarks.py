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
from tornado.web import authenticated
from core.handlers import BaseHandler


class Database():
    def __init__(self, logger, database, schema_file='schema_sqlite.sql'):
        """
        Constructs a new Database object.

        Args:
            app: Application instance of Nubilo core.
            database: SQLite database connection.
            schema_file: The file which contains the DDL statements for
                creating the tables of the Bookmarks plugin.
        Returns:
            A new Database object.
        """
        self._logger = logger
        self._database = database
        self._schema_file = schema_file
        self._mydir = os.path.dirname(os.path.abspath(__file__))

    def open(self):
        self._initialise_if_necessary()

    def _initialise_if_necessary(self):
        if not self._is_initialised():
            self._initialise()

    def _is_initialised(self):
        """Indicates whether the database is already initialised or not."""
        result = True
        if None is self._database:
            result = False
        else:
            cur = self._database.execute("SELECT name FROM sqlite_master WHERE type='table' AND name='bookmarks';")
            number_tables = len(cur.fetchall())
            result = 0 < number_tables
        self._logger.debug("Is database already initialised? %s" % result)
        return result

    def _initialise(self):
        """Initialises the database.

        This drops all existing data and should be used with care!
        """
        self._logger.debug("Initialising database")
        ddl_script = open(os.path.join(self._mydir, self._schema_file))
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
                for tag_entry in self._database.execute("SELECT name FROM tags WHERE tag_id = ?",
                        (tagged_entry["tag"],)):
                    result.add(tag_entry["name"])
            return result

        result = []
        for bookmark_entry in self._database.execute("SELECT bookmark_id, uri, title, description FROM bookmarks"):
            bookmark_id = bookmark_entry["bookmark_id"]
            bookmark_uri = bookmark_entry["uri"]
            bookmark_title = bookmark_entry["title"]
            bookmark_description = bookmark_entry["description"]
            bookmark_tags = get_tag_names(bookmark_id)
            result.append(Bookmark(bookmark_uri, bookmark_title, bookmark_description, bookmark_tags))
        return result

    def insert_bookmark(self, bookmark):
        """Inserts the given bookmark into the database.

        Args:
            bookmark: The Bookmark object to insert into the database.
        """
        def insert_bookmark_entry():
            cur.execute("INSERT INTO bookmarks (uri, title, description) VALUES (?, ?, ?)",
                    (bookmark.uri, bookmark.title, bookmark.description))
            self._database.commit()

        def get_bookmark_id():
            result = -1
            for bookmark_entry in self._database.execute(("SELECT bookmark_id FROM bookmarks "
                    "WHERE uri = ? AND title = ? AND description = ?"),
                    (bookmark.uri, bookmark.title, bookmark.description)):
                if None is not bookmark_entry:
                    result = bookmark_entry["bookmark_id"]
            return result

        def get_tag_id_for(tag):
            result = None
            cur.execute("SELECT tag_id FROM tags WHERE name = ?", (tag,))
            tag_id_entry = cur.fetchone()
            if None is not tag_id_entry:
                result = tag_id_entry["tag_id"]
            return result

        def insert_tag_entry(tag_id, tag):
            cur.execute("INSERT INTO tags (name) VALUES (?)", (tag,))
            self._database.commit()

        def insert_tagged_entry(bookmark_id, tag_id):
            cur.execute("INSERT INTO tagged (bookmark, tag) VALUES (?, ?)", (bookmark_id, tag_id))

        cur = self._database.cursor()
        insert_bookmark_entry()
        bookmark_id = get_bookmark_id()

        for tag in bookmark.tags:
            tag_id = get_tag_id_for(tag)
            if None is tag_id:
                insert_tag_entry(tag_id, tag)
            tag_id = get_tag_id_for(tag)
            insert_tagged_entry(bookmark_id, tag_id)
        self._database.commit()


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


class BookmarkManager(BaseHandler):
    _db = None

    def initialize(self):
        super().initialize()
        self._db = Database(self._logger, self._config.database)
        self._db.open()
        self._config.plugins["bookmark_manager"] = self

    @authenticated
    def get(self, *args, **kwargs):
        for i in self._db.get_all_bookmarks():
            self.write("%s\n" % i)


def plugin_load(**kwargs):
    app = kwargs["app"]
    config = app.settings["nubilo_config"]

    # add request handler to app so that the plugin is routed
    app.add_handlers(r".*$", [(r"/bookmarks(.*)$", BookmarkManager)])

    # add a menu to the UI
    config.menu.add_item("bookmarks", "Get bookmarks", "/bookmarks")


def plugin_unload(**kwargs):
    app = kwargs["app"]
    config = app.settings["nubilo_config"]

    # remove the menu entries
    config.menu.delete_app("bookmarks")

    # remove the plugin
    del config.plugins["bookmark_manager"]
