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


class Menu(object):
    """
    Menu holder object for the application.
    A plugin can add its own menu item or a sub-menu to it.
    """
    _menu = None

    def __init__(self):
        self._menu = dict()

    def add_item(self, appname, label="Default app label", url="fixme"):
        """
        Adds a single menu item.
        :param appname: should be a unique app name
        :param label: label for the menu item
        :param url: url for the menu item
        """
        if appname is None or type(appname) != str or appname.strip() == "":
            raise BaseException("appname should be provided and not empty.")
        if appname in self._menu:
            menu = self._menu[appname]
            menu.append((label, url))
        else:
            menu = [(label, url)]
            self._menu[appname] = menu

    def add_menu(self, appname, label="Default submenu", menu=None):
        """
        Adds a sub-menu to the menu.
        :param appname: should be a unique app name
        :param label: label for the submenu
        :param menu: submenu to add
        """
        if appname is None or type(appname) != str or appname.strip() == "":
            raise BaseException("appname should be provided and not empty.")
        if menu is None or type(menu) != Menu:
            raise BaseException("menu should be provided and of type Menu")
        if appname in self._menu:
            m = self._menu[appname]
            m.append((label, menu))
        else:
            m = [(label, menu)]
            self._menu[appname] = m