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


class Config(object):
    """
        This class is responsible to read the configuration in the right order. Right order means that the
        different layers of configuration (default config values, installation's config, user's config) will be
        read exactly in this order to enable overlay of configuration parameters (i.e. user settings "win" over
        the defaults and the installation's default configuration.
    """
    # the config values defaults, will be overriden by the configuration layers.
    config = dict(nubilo_listen_port=8080,
                  nubilo_plugin_directory="plugins",
                  nubilo_plugin_poll_interval=5,
                  nubilo_logfile="/var/log/nubilo.log",
                  nubilo_cookie_expires=1,
                  nubilo_colored_log=False)

    def __init__(self):
        """
        Creates a Config object.
        """
        self.config_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "nubilo.conf"))
        # override defaults with installation config
        self.read_config(self.config_path)
        # override installation config with user config (if exists)
        self.read_config(self.get_user_config_path())
        # populate the config values as class member attributes for easier access.
        self.populate_member_attributes()

    @staticmethod
    def get_user_config_path():
        """
        Determines the path of the user's configuration file, usually ~/.nubilo/nubilo.conf.
        :return: path of the user's config file
        """
        user_dir = os.path.expanduser("~")
        if user_dir != "~":
            return os.path.abspath(os.path.join(user_dir, ".nubilo", "nubilo.conf"))
        return ""

    def read_config(self, path):
        """
        Reads the configuration file from the given path into the existing dictionary, thus overwriting
        the existing settings there.

        :param path: config file path to read
        """
        try:
            f = open(path, "r")
            for line in f.readlines():
                if line.lstrip().startswith('#'):
                    continue
                keyval = line.strip().split('=')
                if len(keyval) == 2:
                    self.config[keyval[0].strip()] = keyval[1].strip()
        except FileNotFoundError:
            pass

    def populate_member_attributes(self):
        """
        Puts the contents of the config into this class as attributes to be accessed in easy way.
        """
        for i in self.config.keys():
            setattr(self, i, self.config[i])

    @staticmethod
    def do_not_modify(*args):
        """
        Immutable function stub for setattr/delattr.
        :param args: method args
        """
        raise ImmutableException()

    def __str__(self):
        """
        :return: string representation of this config object
        """
        s = ["Config (\n"]
        for i in list(vars(self)):
            s.append("%s = %s\n" % (i, str(getattr(self, i))))
        s.append("\n)")
        return "".join(s)

    def lock(self):
        """
            Make the class immutable.
        """
        del self.config_path
        delattr(Config, "read_config")
        delattr(Config, "config")
        Config.__setattr__ = self.do_not_modify
        Config.__delattr__ = self.do_not_modify


class ImmutableException(BaseException):
    """
    This exception is thrown if there is an attempt to modify the immutable instance.
    """

    def __init__(self):
        super().__init__("Modifying immutable instance is not allowed.")
