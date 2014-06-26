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

from core.user import User


class UserTest(TestCase):
    def test_default_user_is_not_authenticated(self):
        self.user = User("Alex")
        self.assertFalse(self.user.is_authenticated(), "default user may not be authenticated")

    def test_default_user_is_not_anonymous(self):
        self.user = User("Alex")
        self.assertFalse(self.user.is_anonymous(), "default user may not be anonymous")

    def test_default_user_is_active(self):
        self.user = User("Alex")
        self.assertTrue(self.user.is_active())


def suite():
    _suite = TestSuite()
    _suite.addTest(UserTest)
    return _suite


if __name__ == "__main__":
    runner = TextTestRunner()
    test_suite = suite()
    runner.run(test_suite)
