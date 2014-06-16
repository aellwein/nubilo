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


class HeaderChecker():
    header = """#!/usr/bin/env python3
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

""".splitlines()

    def __init__(self):
        pass

    def check(self, filename):
        with open(filename, "r") as f:
            for line in range(len(self.header)):
                l = f.readline().rstrip()
                if l != self.header[line]:
                    print(filename, "does not have the expected license header")
                    return False
        return True

    def correct(self, filename):
        has_header = True
        with open(filename + ".tmp", "w") as newfile:
            for i in self.header:
                newfile.write(i + '\n')
            with open(filename, "r") as source:
                while True:
                    l = source.readline()
                    if not l:
                        break
                    if has_header and l.startswith('#'):
                        continue
                    if has_header and not l.startswith('#'):
                        has_header = False
                    newfile.write(l)
            newfile.flush()
        os.replace(filename + ".tmp", filename)


class PythonFileFinder():
    base_dir = None

    def __init__(self, checker):
        self.checker = checker

    def find(self, _dir):
        for filename in os.listdir(_dir):
            absname = os.path.join(_dir, filename)
            if os.path.isdir(absname):
                self.find(absname)
            else:
                if filename.endswith(".py"):
                    if not self.checker.check(absname):
                        self.checker.correct(absname)


if __name__ == "__main__":
    base_dir = os.path.abspath(os.path.join(os.getcwd(), os.path.dirname(__file__), ".."))
    PythonFileFinder(HeaderChecker()).find(base_dir)
