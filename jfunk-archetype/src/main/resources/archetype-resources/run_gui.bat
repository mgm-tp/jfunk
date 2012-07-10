@ECHO OFF

CALL setenv.bat

START "RunGUI" "%JAVA_HOME%/bin/javaw" -cp ./config;./lib/jfunk-core-%JFUNK_VERSION%.jar com.mgmtp.jfunk.core.ui.JFunkFrame

