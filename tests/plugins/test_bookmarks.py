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

import sys

from unittest.mock import MagicMock
from unittest.case import TestCase
from unittest.runner import TextTestRunner
from unittest.suite import TestSuite

from core.config import Config
from core.log import Logger
from plugins.bookmarks.bookmarks import Bookmark, Database


class DatabaseTest(TestCase):
    def setUp(self):
        app = MagicMock()
        app.settings = dict(nubilo_logger=Logger(sys.stderr), nubilo_config=Config())
        # TODO use dedicated test database instead of real one
        self._db = Database(app.settings["nubilo_logger"])
        self._db.open()
        self._db._initialise()

    def test_database_is_initialised(self):
        self.assertTrue(self._db._is_initialised())

    def test_database_contains_no_bookmarks_initially(self):
        bookmarks = self._db.get_all_bookmarks()

        self.assertTrue(0 == len(bookmarks))

    def test_bookmark_insertion_works(self):
        uri = "uri"
        title = "title"
        description = "description"
        tags = {"tag1", "tag2"}
        self._db.insert_bookmark(Bookmark(uri, title, description, tags))
        bookmarks = self._db.get_all_bookmarks()

        self.assertTrue(1 == len(bookmarks))

        bookmark = bookmarks[0]

        self.assertEquals(uri, bookmark.uri)
        self.assertEquals(title, bookmark.title)
        self.assertEquals(description, bookmark.description)
        self.assertEquals(tags, bookmark.tags)

    def tearDown(self):
        self._db.close()


def suite():
    result = TestSuite()
    result.addTest(DatabaseTest)
    return result

if __name__ == "__main__":
    runner = TextTestRunner()
    test_suite = suite()
    runner.run(test_suite)
