@REM
@REM  Copyright (C) 2013 mgm technology partners GmbH, Munich.
@REM
@REM  See the LICENSE file distributed with this work for additional
@REM  information regarding copyright ownership and intellectual property rights.
@REM

@ECHO OFF

CALL setenv.bat

START "RunGUI" "%JAVA_HOME%/bin/javaw" -cp ./config;./lib/jfunk-core-%JFUNK_VERSION%.jar com.mgmtp.jfunk.core.ui.JFunkFrame

