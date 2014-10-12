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

import hashlib

from tornado.web import RequestHandler, authenticated


class BaseHandler(RequestHandler):
    _database = None
    _config = None
    _logger = None

    def initialize(self):
        self._database = self.application.settings["nubilo_config"].database
        self._config = self.application.settings["nubilo_config"]
        self._logger = self._config.nubilo_logger

    def set_default_headers(self):
        self.set_header("Server", "SomeServer")

    def get_current_user(self):
        return self.get_secure_cookie("nubilo_user")


class FallbackNoOpHandler(BaseHandler):
    def get(self, *args, **kwargs):
        self.set_status(404, "Not Found")
        self.render("404.template")


class IndexHandler(BaseHandler):
    @authenticated
    def get(self, *args, **kwargs):
        self.render("index.template")


class LoginHandler(BaseHandler):
    def get(self, *args, **kwargs):
        self.render("login.template")

    def post(self, *args, **kwargs):

        _username = self.get_argument("username")
        _password = self.get_argument("password")

        if _username == "" or _password == "":
            self.send_error(403)
        _password = hashlib.sha512(bytes(_password, "utf-8")).hexdigest()

        with self._database:
            row = self._database.execute("SELECT * FROM users WHERE username=? and password=?",
                                         (_username, _password)).fetchone()
            if row is None:
                self.send_error(403)
                return  # thou shalt not pass :)
            self.set_secure_cookie("nubilo_user", row["username"], int(self._config.nubilo_cookie_expires))
        self.redirect(self.get_argument("next", "/"))
