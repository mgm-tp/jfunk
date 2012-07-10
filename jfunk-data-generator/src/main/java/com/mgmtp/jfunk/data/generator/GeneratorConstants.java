package com.mgmtp.jfunk.data.generator;

import com.mgmtp.jfunk.data.generator.data.IndexedFields;

/**
 * jFunk data generator specific constants.
 * 
 * @version $Id$
 */
public class GeneratorConstants {
	/**
	 * The value belonging to this key is the name of the generator configuration file.
	 */
	public static final String GENERATOR_CONFIG_FILE = "dataSource.generator.configFile";

	/**
	 * The value belonging to this key is the name of generator configuration file containing the
	 * definitions for all indexed fields. See {@link IndexedFields} for more information concerning
	 * this topic.
	 */
	public static final String INDEXED_FIELDS_FILE = "dataSource.generator.indexedFieldsFile";

	/**
	 * Key for global constraints which are available in all underlying constraint groups.
	 */
	public static final String ALL_CONSTRAINT = "all";

	private GeneratorConstants() {
		// don't allow instantiation
	}
}