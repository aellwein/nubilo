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
from threading import Thread
from time import sleep
import sys


class Plugin(object):
    def __init__(self, plugin_path, name, logger):
        self._name = name
        self._absolute_name = os.path.abspath(os.path.join(plugin_path, self._name))
        self._logger = logger
        self._loaded = False
        self._valid = False
        self._dir_mtime = 0
        self._module_mtime = 0
        self._module = None
        self._show_error = True
        self._plugin_load = callable(self)
        self._plugin_unload = callable(self)

    @property
    def name(self):
        """Gets the plugin's name."""
        return self._name

    @property
    def absolute_name(self):
        """Gets the absolute name (full path name) of the plugin."""
        return self._absolute_name

    @property
    def dir_mtime(self):
        return self._dir_mtime

    @property
    def module_mtime(self):
        return self._module_mtime

    @property
    def valid(self):
        """True, if this plugin has a valid structure."""
        return self._valid

    @property
    def loaded(self):
        """True, if this plugin is loaded."""
        return self._loaded

    def load(self, app):
        if not self.check_is_modified():
            return False
        self._module = __import__("plugins.%s.%s" % (self._name, self._name), fromlist=[self._name])
        try:
            self._plugin_load = getattr(self._module, "plugin_load")
            self._plugin_unload = getattr(self._module, "plugin_unload")
        except AttributeError as e:
            self._logger.error("Error searching for valid entry points: %s" % e)
            self._logger.error("Plugin '%s' must implement 'plugin_load(**kwargs)'\
                    and 'plugin_unload(**kwargs)'" % self._name)
            self._logger.error("methods as its entry and exit points.")
            self._logger.error("Marking plugin '%s' as invalid." % self._name)
            del sys.modules["plugins.%s.%s" % (self._name, self._name)]
            self._valid = False
            return False
        try:
            self._logger.debug("Trying to load plugin '%s'" % self._name)
            self._plugin_load(app=app)
            self._logger.info("Plugin '%s' loaded successfully." % self._name)
            self._loaded = True
            return True
        except BaseException as e:
            self._logger.error("Error while loading plugin '%s': %s" % (self._name, e))
            del sys.modules["plugins.%s.%s" % (self._name, self._name)]
            self._loaded = False
            return False

    def unload(self, app):
        if self._loaded:
            try:
                self._logger.debug("Unloading plugin '%s'" % self._name)
                self._plugin_unload(app=app)
                self._logger.info("Plugin '%s' was unloaded successfully." % self._name)
                self._loaded = False
                self._dir_mtime = 0     # damit der bestehende eintrag "fresh" ist beim naechsten lookup
                self._module_mtime = 0
            except BaseException as e:
                self._logger.error("Error while unloading plugin '%s': %s" % (self._name, e))
            finally:
                del sys.modules["plugins.%s.%s" % (self._name, self._name)]

    def reload(self, app):
        self.unload(app)
        return self.load(app)

    def has_valid_structure(self):
        if not os.path.exists(os.path.join(self._absolute_name, "__init__.py")) or \
                not os.path.exists(os.path.join(self._absolute_name, "%s.py" % self._name)):
            if self._show_error:
                self._logger.error("Error occured while checking for plugin at %s:" % self._absolute_name)
                self._logger.error("Plugin should be in a directory containing a Python module, containing __init__.py")
                self._logger.error("and a plugin loader named after the directory.")
                self._logger.error("For example, a plugin named \"%s\" should have: " % self._name)
                self._logger.error("\t%s/__init__.py" % self._name)
                self._logger.error("\t%s/%s.py" % (self._name, self._name))
                self._show_error = False
            self._valid = False
            return False
        self._logger.debug("Plugin at %s seems to have a valid directory structure" % self._absolute_name)
        self._valid = True
        return True

    def check_is_modified(self):
        """
        Check if plugin was changed (modification time, file structure).
        :return: True, if plugin has been modified since the last check
        """
        if not self.has_valid_structure():
            return False
        dir_mtime = os.stat(self._absolute_name).st_mtime
        module_mtime = os.stat(os.path.join(self._absolute_name, "%s.py" % self._name)).st_mtime
        if self._dir_mtime is None or self._dir_mtime != dir_mtime:
            self._logger.debug("Plugin directory '%s' has changed its modification time" % self._absolute_name)
            self._dir_mtime = dir_mtime
            self._module_mtime = module_mtime
            return True
        if self._module_mtime is None or self._module_mtime != module_mtime:
            self._logger.debug("Plugin module '%s.py' has changed its modification time" % self._name)
            self._dir_mtime = dir_mtime
            self._module_mtime = module_mtime
            return True
        return False

    def __str__(self):
        return "(Plugin %s, valid=%s, loaded=%s, absolute_name=%s, dir_mtime=%f, module_mtime=%f, \
            module=%s, plugin_load=%s, plugin_unload=%s)" % (
            self._name, self._valid, self._loaded, self._absolute_name, self._dir_mtime, self._module_mtime,
            self._module, self._plugin_load, self._plugin_unload)


class PluginManager(object):
    """
    The plugin manager starts a separate polling thread to watch in a defined 'plugins' directory
    for creation of the new plugins or changes in already loaded plugins.
    """

    def __init__(self, app):
        """
        Creates a new plugin manager
        :param app: app instance which will be passed to loaded plugins
        """
        self.app = app
        self.config = self.app.config["nubilo_config"]
        self.logger = self.app.config["nubilo_logger"]
        self.plugins = dict()
        self.plugin_dir_content = list()
        self.install_dir = os.path.abspath(os.path.join(os.path.os.getcwd(), os.path.dirname(__file__), ".."))
        self.plugin_path = os.path.join(self.install_dir, self.config.nubilo_plugin_directory)
        sys.path.append(os.path.abspath(os.path.join(self.plugin_path, "..")))
        self.poll_thread = Thread(target=self.run_polling, daemon=True, name="PluginPollingThread")

    def start(self):
        """
        Starts the plugin manager.
        """
        self.poll_thread.start()

    def run_polling(self):
        """
        Thread, which is polling periodically and tries to load/unload/reload plugins found on disk.
        """
        self.logger.debug("Starting plugin polling thread to poll in \"%s\"" % self.plugin_path)
        while True:
            try:
                self.plugin_dir_content = [f for f in os.listdir(self.plugin_path) if
                                           os.path.isdir(os.path.join(self.plugin_path, f))]
                # unload
                self.unload_removed_plugins()
                # reload
                self.reload_changed_plugins()
                # load
                self.load_new_plugins()
                self.wait_given_period()
            except (InterruptedError, KeyboardInterrupt):
                return

    def unload_removed_plugins(self):
        """
        Unloads plugins which are loaded but where physically removed from the disk.
        """
        if len(self.plugins) <= len(self.plugin_dir_content):
            return
        self.logger.debug("Some plugins were removed.")
        for plug in list(self.plugins.keys()):
            if plug not in self.plugin_dir_content:
                plugin = self.plugins[plug]
                plugin.unload(self.app)
                del self.plugins[plug]

    def reload_changed_plugins(self):
        """
        Scans for plugins which are already loaded but were changed in the meantime, in this case they are unloaded
        and will be then loaded by the loader again, i.e. reloaded.
        """
        for plug in list(self.plugins.keys()):
            plugin = self.plugins[plug]
            if plugin.loaded:
                if plugin.check_is_modified():
                    plugin.reload(self.app)

    def load_new_plugins(self):
        """
        Tries to load all not loaded or new plugins in the plugin directory.
        """
        for plug in self.plugin_dir_content:
            if plug not in self.plugins.keys() or not self.plugins[plug].loaded:
                if plug not in self.plugins.keys():
                    plugin = Plugin(self.plugin_path, plug, self.logger)
                    self.plugins[plug] = plugin
                else:
                    plugin = self.plugins[plug]
                plugin.load(self.app)

    def wait_given_period(self):
        """
        Sleeps for defined period of time.
        Hint: to prevent problems with thread interruption, sleep intervals should be small.
        """
        for i in range(int(self.config.nubilo_plugin_poll_interval)):
            sleep(1)
