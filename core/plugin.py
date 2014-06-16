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

import imp
import importlib

import os
from threading import Thread
from time import sleep


class PluginManager():
    plugins = {}

    def __init__(self, app):
        self.app = app
        self.poll_interval = self.app.config["nubilo_plugin_poll_interval"]
        self.plugin_path = self.app.config["nubilo_plugin_path"]
        self.poll_thread = Thread(target=self.run_polling, daemon=True, name="PluginPollingThread")

    def run_polling(self):
        while True:
            try:
                for i in range(int(self.poll_interval)):
                    sleep(1)
                for fname in os.listdir(self.plugin_path):
                    absname = os.path.join(self.plugin_path, fname)
                    if os.path.isdir(absname):
                        if fname not in self.plugins:
                            self.load_plugin(fname)
                        else:

            except (InterruptedError, KeyboardInterrupt):
                return

    def start(self):
        self.poll_thread.start()

    def rescan_plugins(self):
        for fname in os.listdir(self.plugin_path):
            absname = os.path.join(self.plugin_path, fname)
            if os.path.isdir(absname):
                if fname not in self.plugins:
                    if self.load_plugin(fname):
                        self.plugins[fname] = os.stat(absname).st_mtime
                else:
                    if self.plugins[fname] != os.stat(absname).st_mtime:
                        self.unload_plugin(fname)
                        self.load_plugin(fname)
                        self.plugins[fname] = os.stat(absname).st_mtime

    def load_plugin(self, fname):
        print("loading plugin '%s'" % fname)
        plugin = __import__("plugins.%s.%s" % (fname, fname), fromlist=[fname])
        try:
            plugin_main = getattr(plugin, "plugin_main")
            plugin_main({'app': self.app})
            self.plugins =
            return True
        except AttributeError:
            print("plugin '%s' should implement 'plugin_main(*kwargs)' method as an entry point" % fname)
            del plugin
            return False

    def unload_plugin(self, fname):
        print("unloading plugin '%s'" % fname)
        pass
