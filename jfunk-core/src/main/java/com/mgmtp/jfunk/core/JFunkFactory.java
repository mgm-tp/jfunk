package com.mgmtp.jfunk.core;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * @author rnaegele
 * @version $Id$
 */
public interface JFunkFactory {

	JFunkBase create(int threadCount, boolean parallel, List<File> scripts, Properties scriptProperties);
}
