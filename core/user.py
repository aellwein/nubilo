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


from flask.ext.login import UserMixin, unicode


class User(UserMixin):
    authenticated = False
    active = True
    anonymous = False
    id = None
    password = None

    def __init__(self, username, password=None):
        self.id = username
        self.password = password

    def is_anonymous(self):
        return self.anonymous

    def is_active(self):
        return self.active

    def is_authenticated(self):
        return self.authenticated

    def get_id(self):
        return unicode(self.id)
