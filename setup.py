#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys
from setuptools import setup
from setuptools.command.test import test as TestCommand


class PyTest(TestCommand):
    def initialize_options(self):
        TestCommand.initialize_options(self)
        self.pytest_args = []

    def finalize_options(self):
        TestCommand.finalize_options(self)
        self.test_args = []
        self.test_suite = True

    def run_tests(self):
        # import here, cause outside the eggs aren't loaded
        import pytest

        errno = pytest.main(self.pytest_args)
        sys.exit(errno)


setup(
    name="nubilo",
    version="0.0.1+git",
    description="Nubilo Private Cloud",
    author=["Alexander Ellwein", "Jürgen Fickel"],
    author_email=["", ""],
    url="https://bitbucket.org/jufickel/nubilo",
    license="MIT License",
    install_requires=["tornado"],
    tests_require=["behave", "pytest"],
    cmdclass={'test': PyTest}
)
