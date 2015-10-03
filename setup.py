#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from setuptools import setup
from setuptools_behave import behave_test

setup(
    name="nubilo",
    version="0.0.1+git",
    description="Nubilo Private Cloud",
    author=["Alexander Ellwein", "Jürgen Fickel"],
    author_email=["", ""],
    url="https://bitbucket.org/jufickel/nubilo",
    license="Apache 2 License",
    install_requires=["tornado"],
    tests_require=["behave>1.2.5", "selenium"],
    cmdclass={"behave_test": behave_test}
)
