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

from sqlite3 import Row

import sys
import os
import sqlite3

from tornado.ioloop import IOLoop
from tornado.locale import load_translations
from tornado.web import Application

from core.config import Config
from core.handlers import LoginHandler, FallbackNoOpHandler, IndexHandler
from core.log import Logger
from core.menu import Menu
from core.plugin import PluginManager


class NubiloCore():
    plugin_manager = None
    user_dir = None
    logger = None
    config = None

    def __init__(self, _app):
        self.app = _app
        self.config = _app.settings["nubilo_config"]
        self.logger = self.config.nubilo_logger

    def start(self):
        self.plugin_manager = PluginManager(app)
        self.plugin_manager.start()


if __name__ == "__main__":

    # calculate application's absolute path
    app_dir = os.path.dirname(os.path.abspath(__file__))

    # global settings
    settings = dict(
        login_url="/login",
        cookie_secret=os.urandom(20),
        # TODO: enable later
        xsrf_cookies=True,
        debug=False,
        static_path=os.path.join(app_dir, "static"),
        template_path=os.path.join(app_dir, "templates")
    )
    # setting debug mode if appropriate (affects autoreload and responses)
    if "--debug" in sys.argv:
        settings["debug"] = True

    config = Config()
    settings["nubilo_config"] = config

    # set logger
    logger = Logger(config.nubilo_logfile, config.nubilo_colored_log, settings["debug"])
    config.nubilo_logger = logger

    # set and connect database
    config.database = sqlite3.connect(os.path.join(app_dir, "data", "nubilo.db"))
    config.database.row_factory = Row

    # menu for the registered apps
    config.menu = Menu()

    # placeholder for plugins
    config.plugins = {}

    # load translations
    load_translations(os.path.join(app_dir, "translations"))

    # bind some default handlers
    _handlers = [(r"/login", LoginHandler),
                 (r"/", IndexHandler),
                 (r".*$", FallbackNoOpHandler)]

    # create application
    app = Application(_handlers, **settings)

    try:
        nubilo_core = NubiloCore(app)
        nubilo_core.start()
        app.listen(config.nubilo_listen_port)
        IOLoop.instance().start()

    except KeyboardInterrupt:
        logger.warning("Nubilo is stopped by KeyboardInterrupt.")
        sys.exit(1)
