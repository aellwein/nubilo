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

from tornado.web import RequestHandler, authenticated


class BaseHandler(RequestHandler):
    def set_default_headers(self):
        self.set_header("Server", "SomeServer")


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