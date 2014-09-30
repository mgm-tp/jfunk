@REM
@REM Copyright (c) 2014 mgm technology partners GmbH
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

@ECHO OFF

CALL setenv.bat

ECHO Using JAVA_HOME:   %JAVA_HOME%
ECHO Using JAVA_OPTS:   %JAVA_OPTS%
ECHO Using APP_OPTS:    %APP_OPTS%

CALL %EXEC_CMD% %JAVA_OPTS% %APP_OPTS% -cp ./config;./lib/jfunk-core-%JFUNK_VERSION%.jar com.mgmtp.jfunk.core.JFunk -threadcount=4 %*

PAUSE

IF NOT "%EXIT_AFTER_RUNNING%" == "true" GOTO end

EXIT

:end
