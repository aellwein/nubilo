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
