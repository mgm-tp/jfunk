@REM
@REM Copyright (c) 2015 mgm technology partners GmbH
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM     http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM


:: Do not edit! Automagically processed by Maven.
SET JFUNK_VERSION=3.2.0-SNAPSHOT

:: Environment Variable Prequisites
:: JAVA_HOME   (Optional) Points at your Java Development Kit installation
:: JAVA_OPTS   (Optional) Java runtime options
:: APP_OPTS    (Optional) Application runtime options
SET JAVA_OPTS=%JAVA_OPTS% -Xmx1g -XX:MaxPermSize=128m
SET APP_OPTS=%APP_OPTS% -Dfile.encoding=UTF-8

IF NOT "%JAVA_HOME%" == "" SET EXEC_CMD="%JAVA_HOME%/bin/java"
IF "%JAVA_HOME%" == "" SET EXEC_CMD=java
