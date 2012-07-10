#!/bin/sh

# Do not edit! Automagically processed by Maven.
JFUNK_VERSION=${project.version}

# Environment Variable Prequisites
# JAVA_HOME   (Optional) Points at your Java Development Kit installation
# JAVA_OPTS   (Optional) Java runtime options
# APP_OPTS    (Optional) Application runtime options
JAVA_OPTS="$JAVA_OPTS "-Xmx1g" "-XX:MaxPermSize=128m
APP_OPTS="$APP_OPTS "-Dfile.encoding=UTF-8
