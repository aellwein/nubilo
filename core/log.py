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
from time import localtime, strftime


class Logger():
    """
    A logger which can be used to log messages to a logfile or stderr/stdout with different loglevels (severities).
    """
    color_codes = dict(reset="\033[0m",
                       error="\033[31m",
                       warning="\033[33m",
                       info="\033[32m",
                       debug="\033[36m")

    def __init__(self, logfile, colored=False, _debug=False):
        """
        Constructs a logger with given logfile, optional ANSI coloring. Debug messages can be turned on or off.
        :param logfile: name of logfile to use. Also sys.stderr or sys.stdout is allowed.
        :param colored: True, if the log should color the output using ANSI escape sequences.
        :param _debug: True, if logger should also write debug messages to the logfile.
        """
        self.logfile = logfile
        self.colored = colored
        self._debug = _debug
        if logfile == sys.stderr or logfile == sys.stdout:
            self.log_fd = logfile
            return
        try:
            self.log_fd = open(self.logfile, "a")
        except IOError as e:
            sys.stderr.write(
                "ERROR: unable to open logfile \"%s\" (reason: %s). Further logging will go to stderr.\n" % (
                    self.logfile, e))
            self.log_fd = sys.stderr
        self.info("Log started.")

    def write_log(self, msg, level, color):
        """
        Logs a message with given level (severity) and color.
        :param msg: message to log
        :param level: level (severity) of the logging
        :param color: ANSI color sequence to use. Can be omitted.
        """
        time = strftime("%c", localtime())
        if self.colored:
            self.log_fd.write(
                "%s%s [%s] %s%s\n" % ( self.color_codes[color], time, level, msg, self.color_codes["reset"]))
        else:
            self.log_fd.write("%s [%s] %s\n" % (time, level, msg))
        self.log_fd.flush()

    def info(self, msg):
        """
        Logs a message with severity 'info'.
        :param msg: message to log
        """
        self.write_log(msg, "INFO", "info")

    def debug(self, msg):
        """
        Logs a message with severity 'debug'.
        :param msg: message to log
        """
        if self._debug:
            self.write_log(msg, "DEBUG", "debug")

    def error(self, msg):
        """
        Logs a message with severity 'error'.
        :param msg: message to log
        """
        self.write_log(msg, "ERROR", "error")

    def warning(self, msg):
        """
        Logs a message with severity 'warning'.
        :param msg: message to log
        """
        self.write_log(msg, "WARNING", "warning")
