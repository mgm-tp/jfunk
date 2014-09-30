#!/bin/sh
#
# Copyright (c) 2014 mgm technology partners GmbH
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


# Do not edit! Automagically processed by Maven.
JFUNK_VERSION=${project.version}

# Environment Variable Prequisites
# JAVA_HOME   (Optional) Points at your Java Development Kit installation
# JAVA_OPTS   (Optional) Java runtime options
# APP_OPTS    (Optional) Application runtime options
JAVA_OPTS="$JAVA_OPTS "-Xmx1g" "-XX:MaxPermSize=128m
APP_OPTS="$APP_OPTS "-Dfile.encoding=UTF-8
