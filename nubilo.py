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

import sys

from flask.app import Flask
from tornado.httpserver import HTTPServer
from tornado.ioloop import IOLoop
from tornado.wsgi import WSGIContainer

from core.config import Config
from core.log import Logger
from core.plugin import PluginManager


class NubiloCore():
    plugin_manager = None
    user_dir = None
    logger = None
    config = None

    def __init__(self, _app):
        self.app = _app
        self.config = self.app.config["nubilo_config"]
        self.logger = self.app.config["nubilo_logger"]

    def start(self):
        self.plugin_manager = PluginManager(app)
        self.plugin_manager.start()


if __name__ == "__main__":

    app = Flask(__name__)
    if "--debug" in sys.argv:
        app.config["DEBUG"] = True

    config = Config()
    app.config["nubilo_config"] = config

    logger = Logger(config.nubilo_logfile, config.nubilo_colored_log, app.config["DEBUG"])
    app.config["nubilo_logger"] = logger

    try:
        nubilo_core = NubiloCore(app)
        nubilo_core.start()

        http_server = HTTPServer(WSGIContainer(app))
        http_server.listen(config.nubilo_listen_port)
        IOLoop.instance().start()

    except KeyboardInterrupt:
        logger.warning("Nubilo is stopped by KeyboardInterrupt.")
        sys.exit(1)