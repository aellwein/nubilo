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

from shutil import rmtree
from unittest.case import TestCase
from unittest.mock import MagicMock, call
from unittest.runner import TextTestRunner
from unittest.suite import TestSuite
import os
from os import mkdir

from utils.check_header import HeaderChecker, PythonFileFinder


class CheckHeaderTest(TestCase):
    dir = os.path.abspath(os.path.join(os.getcwd(), os.path.dirname(__file__), "test_data"))
    dir1 = os.path.join(dir, "test1")
    dir2 = os.path.join(dir, "test2")
    dir3 = os.path.join(dir, "test3")
    check_string = "here\ncomes\nthe\ncheck\nstring\n\n\n"

    def setUp(self):
        mkdir(self.dir)
        mkdir(self.dir1)
        mkdir(self.dir2)
        mkdir(self.dir3)
        for f in [os.path.join(self.dir1, "myfile1.py"), os.path.join(self.dir2, "myfile1.py"),
                  os.path.join(self.dir2, "myfile2.py"), os.path.join(self.dir3, "myfile1.py"),
                  os.path.join(self.dir3, "myfile2.py"), os.path.join(self.dir3, "myfile3.py")]:
            with(open(f, "w")) as outfile:
                outfile.write("#\n")
                outfile.write(self.check_string)
                outfile.flush()

    def tearDown(self):
        rmtree(self.dir, ignore_errors=True)

    def test_find_files_in_dir1(self):
        hc = HeaderChecker()
        hc.check = MagicMock(return_value=True)
        pff = PythonFileFinder(hc)
        pff.find(self.dir1)
        hc.check.assert_has_calls([call(os.path.join(self.dir1, "myfile1.py"))])

    def test_find_files_in_dir2(self):
        hc = HeaderChecker()
        hc.check = MagicMock(return_value=True)
        pff = PythonFileFinder(hc)
        pff.find(self.dir2)
        hc.check.assert_has_calls(
            [call(os.path.join(self.dir2, "myfile1.py")), call(os.path.join(self.dir2, "myfile2.py"))])

    def test_find_files_in_dir3(self):
        hc = HeaderChecker()
        hc.check = MagicMock(return_value=True)
        pff = PythonFileFinder(hc)
        pff.find(self.dir3)
        hc.check.assert_has_calls(
            [call(os.path.join(self.dir3, "myfile1.py")), call(os.path.join(self.dir3, "myfile2.py")),
             call(os.path.join(self.dir3, "myfile3.py"))])

    def test_correct_is_called(self):
        hc = HeaderChecker()
        hc.check = MagicMock(side_effect=[True, False, True])
        hc.correct = MagicMock()
        pff = PythonFileFinder(hc)
        pff.find(self.dir3)
        hc.correct.assert_has_calls([call(os.path.join(self.dir3, "myfile2.py"))])

    def test_correct_is_called_and_correction_was_made(self):
        hc = HeaderChecker()
        pff = PythonFileFinder(hc)
        pff.find(self.dir3)
        for i in [os.path.join(self.dir3, "myfile1.py"), os.path.join(self.dir3, "myfile2.py"),
                  os.path.join(self.dir3, "myfile3.py")]:
            with open(i, "r") as f:
                r = [x.rstrip() for x in f.readlines()]
            self.assertEqual(r[:len(hc.header)], hc.header)
            self.assertEqual(r[len(hc.header):], self.check_string.splitlines())

    def test_no_correction_is_made_afterwards(self):
        hc = HeaderChecker()
        pff = PythonFileFinder(hc)
        pff.find(self.dir3)
        flengths = {fname: os.stat(fname).st_size for fname in
                    [os.path.join(self.dir3, "myfile1.py"), os.path.join(self.dir3, "myfile2.py"),
                     os.path.join(self.dir3, "myfile3.py")]}
        pff.find(self.dir3)
        for i in [os.path.join(self.dir3, "myfile1.py"), os.path.join(self.dir3, "myfile2.py"),
                  os.path.join(self.dir3, "myfile3.py")]:
            self.assertEqual(flengths[i], os.stat(i).st_size, i)


def suite():
    _suite = TestSuite()
    _suite.addTest(CheckHeaderTest)
    return _suite


if __name__ == "__main__":
    runner = TextTestRunner()
    test_suite = suite()
    runner.run(test_suite)
