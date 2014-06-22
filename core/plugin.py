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


class PluginManager(object):
    """
    The plugin manager starts a separate polling thread to watch in a defined 'plugins' directory
    for creation of the new plugins or changes in already loaded plugins.
    """
    # (map of maps) contains the data of all loaded/tracked plugins (plugin cache)
    plugins = {}

    # contains a periodically updated list of plugin directory to compare against
    plugin_dir_content = []

    def __init__(self, app):
        """
        Creates a new plugin manager
        :param app: app instance which will be passed to loaded plugins
        """
        self.app = app
        self.config = self.app.config["nubilo_config"]
        self.logger = self.app.config["nubilo_logger"]
        self.plugin_path = self.config.nubilo_plugin_directory
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
                self.plugin_dir_content = os.listdir(self.plugin_path)
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
        for plugin in list(self.plugins.keys()):
            if plugin not in self.plugin_dir_content:
                self.unload_plugin(plugin)

    def unload_plugin(self, plugin):
        """
        Unloads given plugin after checking for its validity and state.
        :param plugin: plugin to unload
        """
        if self.is_loaded_plugin(plugin):
            try:
                self.logger.debug("Unloading plugin '%s'" % plugin)
                self.plugins[plugin]['plugin_unload'](app=self.app)
                self.logger.info("Plugin '%s' was unloaded successfully." % plugin)
                self.plugins[plugin]['loaded'] = False
            except BaseException as e:
                self.logger.error("Error while unloading plugin '%s': %s" % (plugin, e))
            finally:
                del sys.modules["plugins.%s.%s" % (plugin, plugin)]
                del self.plugins[plugin]
        else:
            self.logger.info("Change in directories detected, removing invalid plugin '%s' from plugin cache" % plugin)
            del self.plugins[plugin]

    def reload_changed_plugins(self):
        """
        Scans for plugins which are already loaded but were changed in the meantime, in this case they are unloaded
        and will be then loaded by the loader again, i.e. reloaded.
        """
        for plugin in self.plugins.keys():
            if self.is_loaded_plugin(plugin):
                if self.check_if_modified(plugin):
                    self.reload_plugin(plugin)

    def load_new_plugins(self):
        """
        Tries to load all not loaded plugins in the plugin directory.
        """
        for plugin in self.plugin_dir_content:
            if plugin not in self.plugins or not self.plugins[plugin]["loaded"]:
                if plugin in self.plugins.keys():
                    self.plugins[plugin].update(dict(valid=False, loaded=False))
                else:
                    self.plugins[plugin] = dict(valid=False, loaded=False)
                self.try_load_plugin(plugin)

    def wait_given_period(self):
        """
        Sleeps for defined period of time.
        Hint: to prevent problems with thread interruption, sleep intervals should be small.
        """
        for i in range(int(self.config.nubilo_plugin_poll_interval)):
            sleep(1)

    def is_valid_plugin(self, plugin):
        try:
            return self.plugins[plugin]["valid"]
        except KeyError:
            return False

    def is_loaded_plugin(self, plugin):
        try:
            return self.plugins[plugin]["loaded"]
        except KeyError:
            return False

    def plugin_has_valid_directory_structure(self, plugin, show_error=True):
        absname = os.path.abspath(os.path.join(self.plugin_path, plugin))
        if not os.path.exists(os.path.join(absname, "__init__.py")) or not os.path.exists(
                os.path.join(absname, "%s.py" % plugin)):
            if show_error:
                self.logger.error("Error occured while checking for plugin at %s:" % absname)
                self.logger.error("Plugin should be in a directory containing a Python module, containing __init__.py")
                self.logger.error("and a plugin loader named after the directory.")
                self.logger.error("For example, a plugin named \"%s\" should have: " % plugin)
                self.logger.error("\t%s/__init__.py" % plugin)
                self.logger.error("\t%s/%s.py" % (plugin, plugin))
                self.logger.debug("Invalid plugin found at %s" % absname)
                self.plugins[plugin]["show_error"] = False
            self.plugins[plugin]["valid"] = False
            return False
        self.logger.debug("Plugin at %s seems to have a valid directory structure" % absname)
        self.plugins[plugin]["valid"] = True
        return True

    def check_if_modified(self, plugin):
        """
        Check if plugin was changed (modification time, file structure).
        :param plugin: plugin to check for
        :return: True, if plugin has been modified since the last check
        """
        absname = os.path.abspath(os.path.join(self.plugin_path, plugin))
        try:
            dir_mtime = self.plugins[plugin]["dir_mtime"]
            module_mtime = self.plugins[plugin]["module_mtime"]
            if dir_mtime != os.stat(absname).st_mtime:
                self.logger.debug("Plugin directory '%s' has changed its modification time" % absname)
                self.plugins[plugin]["dir_mtime"] = dir_mtime
                return True
            if module_mtime != os.stat(os.path.join(absname, "%s.py" % plugin)).st_mtime:
                self.logger.debug("Plugin module '%s.py' has changed its modification time" % plugin)
                self.plugins[plugin]["module_mtime"] = module_mtime
                return True
        except KeyError:
            if self.plugin_has_valid_directory_structure(plugin, show_error=self.plugins[plugin]["show_error"]):
                self.plugins[plugin]["dir_mtime"] = os.stat(absname).st_mtime
                self.plugins[plugin]["module_mtime"] = os.stat(os.path.join(absname, "%s.py" % plugin)).st_mtime
                return False

    def try_load_plugin(self, plugin):
        """
        Tries to load a plugin.
        :param plugin: plugin name to load.
        """
        absname = os.path.abspath(os.path.join(self.plugin_path, plugin))
        if "show_error" not in self.plugins[plugin].keys():
            self.plugins[plugin]["show_error"] = True
        if not self.plugin_has_valid_directory_structure(plugin, show_error=self.plugins[plugin]["show_error"]):
            return
        self.plugins[plugin]["dir_mtime"] = os.stat(absname).st_mtime
        self.plugins[plugin]["module_mtime"] = os.stat(os.path.join(absname, "%s.py" % plugin)).st_mtime
        self.plugins[plugin]["module"] = __import__("plugins.%s.%s" % (plugin, plugin), fromlist=[plugin])
        try:
            self.plugins[plugin]["plugin_load"] = getattr(self.plugins[plugin]["module"], "plugin_load")
            self.plugins[plugin]["plugin_unload"] = getattr(self.plugins[plugin]["module"], "plugin_unload")
        except AttributeError:
            self.logger.error(
                "Plugin '%s' must implement 'plugin_load(**kwargs)' and 'plugin_unload(**kwargs)'" % plugin)
            self.logger.error("methods as its entry and exit points.")
            self.logger.error("Marking plugin '%s' as invalid." % plugin)
            self.plugins[plugin]["valid"] = False
            return
        try:
            self.logger.debug("Trying to load plugin '%s'" % plugin)
            self.plugins[plugin]["plugin_load"](app=self.app)
            self.logger.info("Plugin '%s' loaded successfully." % plugin)
            self.plugins[plugin]["loaded"] = True
        except BaseException as e:
            self.logger.error("Error while loading plugin '%s': %s" % (plugin, e))
            del sys.modules["plugins.%s.%s" % (plugin, plugin)]
            self.plugins[plugin]["loaded"] = False

    def reload_plugin(self, plugin):
        """
        Reloads a plugin
        :param plugin: plugin name to reload
        """
        self.unload_plugin(plugin)
        self.plugins[plugin] = dict(valid=False, loaded=False, show_error=True)
        self.try_load_plugin(plugin)
