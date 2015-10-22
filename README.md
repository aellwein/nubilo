[![Build Status](https://travis-ci.org/aellwein/nubilo.svg?branch=master)](https://travis-ci.org/aellwein/nubilo) [![Coverage Status](https://coveralls.io/repos/aellwein/nubilo/badge.svg?branch=master&service=github)](https://coveralls.io/github/aellwein/nubilo?branch=master) [![Stories in Ready](https://badge.waffle.io/aellwein/nubilo.png?label=ready&title=Ready)](https://waffle.io/aellwein/nubilo) [![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/aellwein/nubilo?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge) [![Licence](http://img.shields.io/badge/Licence-MIT-brightgreen.svg)](LICENSE)


Nubilo
======

[![Join the chat at https://gitter.im/aellwein/nubilo](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/aellwein/nubilo?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

What is it?
-----------

*Nubilo* aims to be a private cloud which can host services (a.k.a. apps). 

Current State
-------------

*In development, not ready for testing yet!*

Prerequisites
-------------

* luajit
* luarocks
* turbo (installed with LuaRocks)
* busted (used only for tests, installed with LuaRocks
* luacov (used only for tests coverage, installed with LuaRocks)

Building
--------

* ``sudo luarocks install luajit``
* ``sudo luarocks install turbo``
* ``sudo luarocks install busted``
* ``make``


Documentation
-------------

* Generated HTML docs can be found here: ./doc/build/html/index.html


Contributing
------------
* Please follow the guidelines for good Git commit messages as described
  [here](http://chris.beams.io/posts/git-commit/).


License
-------

*Nubilo* is licensed under the terms of [MIT License](LICENSE). 
 
 
