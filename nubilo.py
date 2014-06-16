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

from flask.app import Flask
import sys
from tornado.httpserver import HTTPServer
from tornado.ioloop import IOLoop
from tornado.wsgi import WSGIContainer
from core.plugin import PluginManager


def plugin_injector(func):
    def wrapped(*args, **kwargs):
        func(*args, **kwargs)
    wrapped()

class NubiloCore():
    config = {}
    config_path = os.path.abspath(os.path.join(os.getcwd(), os.path.dirname(__file__), "nubilo.conf"))
    plugin_path = os.path.abspath(os.path.join(os.getcwd(), os.path.dirname(__file__), "plugins"))
    plugin_manager = None

    def __init__(self, _app):
        self.app = _app

    def start(self):
        self.read_config()
        app.config.update(self.config)
        app.config['nubilo_plugin_path'] = self.plugin_path
        self.plugin_manager = PluginManager(app)
        self.plugin_manager.start()

    def read_config(self):
        with open(self.config_path, "r") as f:
            for line in f.readlines():
                if line.lstrip().startswith('#'):
                    continue
                keyval = line.strip().split('=')
                if len(keyval) == 2:
                    self.config[keyval[0].strip()] = keyval[1].strip()


if __name__ == "__main__":
    try:
        app = Flask(__name__)
        nubilo_core = NubiloCore(app)
        nubilo_core.start()
        http_server = HTTPServer(WSGIContainer(app))
        http_server.listen(app.config['nubilo_listen_port'])
        IOLoop.instance().start()
    except KeyboardInterrupt:
        sys.exit(1)