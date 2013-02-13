#!/bin/sh
#
#  Copyright (C) 2013 mgm technology partners GmbH, Munich.
#
#  See the LICENSE file distributed with this work for additional
#  information regarding copyright ownership and intellectual property rights.
#


. ./setenv.sh

echo "Using JAVA_HOME:   $JAVA_HOME"
echo "Using JAVA_OPTS:   $JAVA_OPTS"
echo "Using APP_OPTS:    $APP_OPTS"

$JAVA_HOME/bin/java $JAVA_OPTS $APP_OPTS -cp ./config:./lib/jfunk-core-$JFUNK_VERSION.jar com.mgmtp.jfunk.core.JFunk -threadcount=4 $@

