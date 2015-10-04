## Nubilo 


### What is it?

*Nubilo* aims to be a private cloud which can host services (a.k.a. apps). 


### Core principles

* cross-platform software
* has a liberal license
* lightweight and minimalistic
* ...but extendable

### Installation

* Install [Python 3.4](https://python.org/downloads) or greater.
* For installing **an official release** of *Nubilo*:
    * run ``python3 -m ensurepip`` (maybe using priviledged user)
    * run ``pip install nubilo`` (system-wide) or
    * ``pip install nubilo --user`` (user-local installation).
* for installing **a development version** of *Nubilo*:
    * run ``python3 -m ensurepip`` (maybe using priviledged user)
    * run ``pip install git+https://bitbucket.org/jufickel/nubilo`` (system-wide) or
    * ``pip install git+https://bitbucket.org/jufickel/nubilo --user`` for user-local installation.

### Documentation

* Please refer to the docs under doc/build/html.
* To build the docs: ``pip install Sphinx`` and ``make html`` in ``doc`` directory.

### License

*Nubilo* is licensed under the terms of [Apache 2 Software License](http://www.apache.org/licenses/LICENSE-2.0). 
 
 