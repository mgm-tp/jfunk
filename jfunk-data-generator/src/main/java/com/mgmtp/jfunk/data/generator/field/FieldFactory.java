/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.field;

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.Constructor;

import org.jdom.Element;

import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.data.generator.util.XMLTags;
import com.mgmtp.jfunk.data.generator.util.XmlElementFinder;

/**
 * Factory class to generate a field element from an XML element.
 * 
 * @version $Id$
 */
public class FieldFactory {

	private XmlElementFinder elementFinder;

	/**
	 * Returns the element finder. If it is not yet initialzed a new one is generated together with
	 * the document containing the element. Caution: This ElementFinder is only valid for the
	 * lifecycle of this Factory instance!
	 */
	private XmlElementFinder getElementFinder(final Element element) {
		if (elementFinder == null) {
			elementFinder = new XmlElementFinder(element.getDocument());
		}
		return elementFinder;
	}

	/**
	 * Searches for the Field Child tag in the specified element or takes the Field_Ref Tag and
	 * generates a new field instance based on the tag data. So this method has to be called with an
	 * element as parameter that contains a field or a field_ref element, respectively.
	 */
	public Field createField(final MathRandom random, final Element element, final String characterSetId) {
		Field field = null;
		Element fieldElement = null;
		Element fieldRefElement = element.getChild(XMLTags.FIELD_REF);
		if (fieldRefElement != null) {
			fieldElement = getElementFinder(element).findElementById(fieldRefElement);
		} else {
			fieldElement = element.getChild(XMLTags.FIELD);
		}

		checkState(fieldElement != null, "Could not find a Field tag or FieldRef tag");

		Class<? extends Field> classObject = null;
		try {
			classObject = getClassObject(fieldElement);
			Constructor<? extends Field> constructor = classObject.getConstructor(new Class[] { MathRandom.class, Element.class, String.class });
			field = constructor.newInstance(new Object[] { random, fieldElement, characterSetId });
		} catch (Exception e) {
			throw new IllegalStateException("Could not initialise object of class " + classObject, e);
		}
		return field;
	}

	/**
	 * This method returns the class object from which a new instance shall be generated. To achieve
	 * this the class attribute of the passed element is taken to determine the class name.
	 */
	private Class<? extends Field> getClassObject(final Element element) {
		String className = element.getAttributeValue(XMLTags.CLASS);
		Class<? extends Field> classObject = null;
		try {
			className = className.indexOf('.') > 0 ? className : getClass().getPackage().getName() + '.' + className;
			classObject = Class.forName(className).asSubclass(Field.class);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("could not get class " + className, e);
		}
		return classObject;
	}
}