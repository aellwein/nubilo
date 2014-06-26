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

from core.config import Config, ImmutableException


class ConfigTest(TestCase):
    def test_config_immutable_after_lock(self):
        config = Config()
        config.lock()
        try:
            config.bla = "fasel"
            self.fail("setting attribute should not be possible after lock")
        except ImmutableException:
            pass

    def test_config_is_not_empty_after_creation(self):
        del sys.modules["core.config"]
        from core.config import Config

        config = Config()
        self.assertNotEqual(config.config, {}, "should not be empty")

    def test_config_has_basic_settings_propagated(self):
        del sys.modules["core.config"]
        from core.config import Config

        config = Config()
        self.assertIsNotNone(config.nubilo_listen_port)
        self.assertIsNotNone(config.nubilo_plugin_directory)
        self.assertIsNotNone(config.nubilo_logfile)

    def test_read_config_on_wrong_file_does_not_raise(self):
        del sys.modules["core.config"]
        from core.config import Config

        config = Config()
        c = config.config.copy()
        config.read_config("")
        self.assertEqual(c, config.config)

    def test_str_config_does_contain_basic_settings(self):
        del sys.modules["core.config"]
        from core.config import Config

        config = Config()
        config.lock()
        s = str(config)
        self.assertNotEqual(s.index("nubilo_listen_port"), -1)
        self.assertNotEqual(s.index("nubilo_logfile"), -1)
        self.assertNotEqual(s.index("nubilo_plugin_directory"), -1)


def suite():
    _suite = TestSuite()
    _suite.addTest(ConfigTest)
    return _suite


if __name__ == "__main__":
    runner = TextTestRunner()
    test_suite = suite()
    runner.run(test_suite)
