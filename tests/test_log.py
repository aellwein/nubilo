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

from unittest.case import TestCase
from unittest.runner import TextTestRunner
from unittest.suite import TestSuite
import sys
from test.support import captured_stderr

from core.log import Logger


class LogTest(TestCase):
    def test_log_info_with_stderr(self):
        with captured_stderr() as stderr:
            log = Logger(sys.stderr)
            log.info("info")
            log.warning("warning")
            log.error("error")
            log.debug("debug")
            self.assertNotEqual(stderr.getvalue().index("[INFO] info"), -1)
            self.assertNotEqual(stderr.getvalue().index("[WARNING] warning"), -1)
            self.assertNotEqual(stderr.getvalue().index("[ERROR] error"), -1)
            self.assertEqual(stderr.getvalue().find("[DEBUG] debug"), -1)

    def test_log_info_with_invalid_file(self):
        with captured_stderr() as stderr:
            log = Logger("", _debug=True)
            log.debug("seen on debug")
            self.assertNotEqual(stderr.getvalue().find("[DEBUG] seen on debug"), -1)


def suite():
    _suite = TestSuite()
    _suite.addTest(LogTest)
    return _suite


if __name__ == "__main__":
    runner = TextTestRunner()
    test_suite = suite()
    runner.run(test_suite)
