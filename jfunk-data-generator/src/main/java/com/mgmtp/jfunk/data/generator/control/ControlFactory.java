package com.mgmtp.jfunk.data.generator.control;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;
import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.common.util.Range;
import com.mgmtp.jfunk.data.generator.constraint.Constraint;
import com.mgmtp.jfunk.data.generator.util.XMLTags;
import com.mgmtp.jfunk.data.generator.util.XmlElementFinder;

/**
 * Returns the {@link FieldControl} instances for the {@link Constraint} objects or field instances,
 * respectively.
 * 
 * @version $Id$
 */
public final class ControlFactory {

	public static final Logger LOGGER = Logger.getLogger(ControlFactory.class);
	private XmlElementFinder finder = null;

	/**
	 * Returns a new {@link FieldControl}instance. The element passed either has to be of type
	 * {@link XMLTags#FIELD} or {@link XMLTags#FIELD_REF} or at least contain one of these tags.
	 */
	public FieldControl getControl(final MathRandom random, Element element, final Range range) {
		if (finder == null) {
			finder = new XmlElementFinder(element.getDocument());
		}
		Element fieldElement = element.getChild(XMLTags.FIELD);
		if (fieldElement != null) {
			element = fieldElement;
		} else {
			fieldElement = element.getChild(XMLTags.FIELD_REF);
			if (fieldElement != null) {
				element = finder.findElementById(fieldElement);
			} else if (XMLTags.FIELD_REF.equals(element.getName())) {
				// search for original field element
				element = finder.findElementById(element);
			}
		}
		element = element.getChild(XMLTags.CONTROL_REF);
		element = finder.findElementById(element);
		if (element == null) {
			throw new IllegalStateException("Could not find control object");
		}
		String className = null;
		Class<? extends FieldControl> classObject = null;
		try {
			className = element.getAttributeValue(XMLTags.CLASS);
			className = className.indexOf('.') > 0 ? className : getClass().getPackage().getName() + '.' + className;
			classObject = Class.forName(className).asSubclass(FieldControl.class);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Could  not load class " + className, e);
		}
		Constructor<? extends FieldControl> constructor = null;
		try {
			constructor = classObject.getConstructor(new Class[] { MathRandom.class, Element.class, Range.class });
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("Could not find constructor for class " + className, e);
		}
		try {
			return constructor.newInstance(new Object[] { random, element, range });
		} catch (Exception e) {
			throw new IllegalStateException("Could not call constructor for class " + className, e);
		}
	}
}