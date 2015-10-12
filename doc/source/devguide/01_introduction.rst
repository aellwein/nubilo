
Introduction and Goals
======================

*Nubilo* is the current working name for the project, which aims to create a private cloud 
infrastructure for a small sized hardware, like Raspberry Pi or BeagleBone SBCs. We want to provide
approximately the same feature set as `OwnCloud`_ does.

.. _OwnCloud: http://owncloud.org/

.. note::
   `OwnCloud`_ is a simple and established solution for creating own private cloud infrastructure.
   An OwnCloud server instance can be operated by a `Raspberry Pi <http://www.raspberrypi.org/>`_.
   Especially since the Model B Raspberry has enough RAM and CPU to operate it. However, OwnCloud 
   running on a Raspberry behaves slow and a RAM consumption is high, because 
   OwnCloud is written in PHP and the code gets interpreted at runtime - every time. Caches like
   `opcache <http://www.php.net/manual/de/book.opcache.php>`_ or `APCu <http://pecl.php.net/package/APCu>`_ 
   can improve the performance, but not satisfactory enough.

Nubilo tries to provide a lightweight alternative for OwnCloud and decent performance even
on a weak hardware. This can be done by doing the only necessary work - preparation and processing of data - 
on the (resource-limited) server; the user interface can be handled completely on the client's side. 

Here are the important project goals:

* Simplicity
   * simple usage
   * simple setup and operation
* Performance
   * low CPU usage
   * low RAM usage
   * short response times
* Modularity
   * can be extended by plugins

Furthermore, there are some non-functional goals:

* Security
   * use modern encryption
   * provide high security already out-of-the-box
* An open and liberal software license (:doc:`MIT License <../userguide/license>`).

