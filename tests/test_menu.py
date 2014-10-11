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
from core.menu import Menu


class MenuTest(TestCase):
    def test_add_item_with_empty_appname(self):
        menu = Menu()
        self.assertRaises(BaseException, menu.add_item, "")

    def test_add_item_with_wrong_typed_appname(self):
        menu = Menu()
        self.assertRaises(BaseException, menu.add_item, 0.2)

    def test_add_item_without_appname(self):
        menu = Menu()
        self.assertRaises(BaseException, menu.add_item, None)

    def test_add_menu_without_appname(self):
        menu = Menu()
        self.assertRaises(BaseException, menu.add_menu, None)

    def test_add_menu_with_empty_appname(self):
        menu = Menu()
        self.assertRaises(BaseException, menu.add_menu, "")

    def test_add_menu_with_wrong_typed_appname(self):
        menu = Menu()
        self.assertRaises(BaseException, menu.add_menu, 0.2)

    def test_add_submenu_without_menu(self):
        menu = Menu()
        self.assertRaises(BaseException, menu.add_menu, "app", None, None)

    def test_add_submenu_with_wrong_type_menu(self):
        menu = Menu()
        self.assertRaises(BaseException, menu.add_menu, "app", None, "ds")


def suite():
    _suite = TestSuite()
    _suite.addTest(MenuTest)
    return _suite


if __name__ == "__main__":
    runner = TextTestRunner()
    test_suite = suite()
    runner.run(test_suite)
