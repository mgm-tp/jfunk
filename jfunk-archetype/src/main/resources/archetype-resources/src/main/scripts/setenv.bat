
:: Do not edit! Automagically processed by Maven.
SET JFUNK_VERSION=${project.version}

:: Environment Variable Prequisites
:: JAVA_HOME   (Optional) Points at your Java Development Kit installation
:: JAVA_OPTS   (Optional) Java runtime options
:: APP_OPTS    (Optional) Application runtime options
SET JAVA_OPTS=%JAVA_OPTS% -Xmx1g -XX:MaxPermSize=128m
SET APP_OPTS=%APP_OPTS% -Dfile.encoding=UTF-8

IF NOT "%JAVA_HOME%" == "" SET EXEC_CMD="%JAVA_HOME%/bin/java"
IF "%JAVA_HOME%" == "" SET EXEC_CMD=java
