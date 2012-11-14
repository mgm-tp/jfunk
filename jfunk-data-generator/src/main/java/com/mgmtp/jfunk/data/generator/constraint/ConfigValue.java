package com.mgmtp.jfunk.data.generator.constraint;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Configuration;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.constraint.base.BaseConstraint;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * A constraint that returns a value from the current {@link Configuration}.
 * 
 * <pre>
 * {@code 
 * <constraint id="c1" class="ConfigValue">
 *   <constant>myConfigKey</constant>
 * </constraint>
 * }
 * </pre>
 * 
 * @version $Id$
 */
public class ConfigValue extends BaseConstraint {

	@Inject
	Provider<Configuration> configProvider;

	private final String configKey;

	public ConfigValue(final MathRandom random, final Element element, final Generator generator) {
		super(random, element, generator);
		configKey = element.getChildText(XMLTags.CONSTANT);
	}

	@Override
	public int getMaxLength() {
		return -1;
	}

	@Override
	public void resetValues() {
		//
	}

	@Override
	public int countCases() {
		return 1;
	}

	@Override
	public boolean hasNextCase() {
		return false;
	}

	@Override
	public void resetCase() {
		// no-op
	}

	@Override
	protected String initValuesImpl(final FieldCase c) throws Exception {
		return configProvider.get().get(configKey);
	}
}