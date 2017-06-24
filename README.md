[![License: GPL v3](https://img.shields.io/badge/license-GPL%20v3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)
[![Build Status](https://travis-ci.org/lispyclouds/bob.svg?branch=master)](https://travis-ci.org/lispyclouds/bob)

# Bob the Builder

![](http://vignette2.wikia.nocookie.net/dreamlogos/images/8/8d/Btb1.png/revision/latest?cb=20150801085138)

This is what CI/CD should've been.

## Build requirements
- Preferably any *nix environment
- JDK 1.8+

## Running requirements
- JRE 1.8+

## Testing, building and running
- Run `./gradlew test` to run tests.
- Run `./gradlew shadowJar` to get the standalone JAR in `build/libs`.
- Run `java -jar <JAR_file_path>` to start the server on port **7777**.

**Bob the Builder image is ©BBC**
