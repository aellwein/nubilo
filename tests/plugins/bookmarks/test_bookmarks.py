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
        app.config = dict(nubilo_logger=Logger(sys.stderr), nubilo_config=Config())
        # TODO use dedicated test database instead of real one
        self._db = Database(app.config["nubilo_logger"])
        self._db.open()
        self._db._initialise()

    def test_database_is_initialised(self):
        self.assertTrue(self._db._is_initialised())

    def test_database_contains_no_bookmarks_initially(self):
        bookmarks = self._db.get_all_bookmarks()
        self.assertTrue(0 == len(bookmarks))

    def test_database_contains_one_bookmark_after_insertion(self):
        self._db.insert_bookmark(Bookmark("foo", "bar", "baz"))
        bookmarks = self._db.get_all_bookmarks()

        self.assertTrue(1 == len(bookmarks))

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
