import sys

from unittest.mock import MagicMock
from unittest.case import TestCase
from unittest.runner import TextTestRunner
from unittest.suite import TestSuite

from core.config import Config
from core.log import Logger

class BookmarksTest(TestCase):
    pass

class DatabaseTest(TestCase):
    def __init__(self):
        self._db = None

    def setUp(self):
        app = MagicMock()
        app.config = dict(nubilo_logger=Logger(sys.stderr), nubilo_config=Config())
        self._db = Database(app.config["nubilo_logger"])
        self._db.open()
        self._db._initialise()

    def database_is_initialised(self):
        self.assertTrue(self._db._is_initialised())

    def tearDown(self):
        self._db.close()

def suite():
    result = TestSuite()
    result.addTest(BookmarksTest)
    result.addTest(DatabaseTest)
    return result

if __name__ == "__main__":
    runner = TextTestRunner()
    test_suite = suite()
    runner.run(test_suite)
