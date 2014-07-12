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
from flask import app, session, abort
from flask.app import Flask


class Database():
    def __init__(self, logger, database_file='bookmarks.db', schema_file='schema_sqlite.sql'):
        """
        Constructs a new Database object.
        :param app: Application instance of Nubilo core.
        :param database_file: SQLite database file which is the base for this Database object.
        :param schema_file: The file which contains the DDL statements for creating the tables of the Bookmarks plugin.
        :return: A new Database object.
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
        """Initialises the database. This drops all existing data and should be used with care!"""
        self._logger.debug("Initialising database \"%s\"." % self._database_file)
        ddl_script = open(self._schema_file)
        self._database.cursor().executescript(ddl_script.read())
        ddl_script.close()
        self._database.commit()

    def get_all_bookmarks(self):
        cur = self._database.cursor()
        cur.execute("SELECT * FROM bookmarks")
        all_bookmarks = cur.fetchall()
        for bm in all_bookmarks:
            # TODO create Bookmark objects based on database data
            pass

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

    @app.route("/")
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
