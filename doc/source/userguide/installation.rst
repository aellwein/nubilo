
Installation
============

Hardware requirements
---------------------

* a Raspberry Pi (model B or higher)
* an external USB HDD drive
* Internet access

Software requirements
---------------------
* LuaJIT 2.0.x
* luarocks
* turbo 
* busted (used for testing)

Example installation on Raspberry Pi (running Raspbian)
-------------------------------------------------------
* Install needed Raspbian packages: ``sudo apt-get install luarocks luajit libssl-dev``
* Install Lua packages: ``for i in turbo busted; do sudo luarocks install $i ; done``
