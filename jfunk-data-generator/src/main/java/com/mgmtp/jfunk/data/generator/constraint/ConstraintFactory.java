/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.constraint;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;

import com.google.inject.Injector;
import com.mgmtp.jfunk.common.exception.JFunkException;
import com.mgmtp.jfunk.common.random.MathRandom;
import com.mgmtp.jfunk.data.generator.Generator;
import com.mgmtp.jfunk.data.generator.control.FieldCase;
import com.mgmtp.jfunk.data.generator.control.FieldControl;
import com.mgmtp.jfunk.data.generator.exception.IdNotFoundException;
import com.mgmtp.jfunk.data.generator.util.ValueCallback;
import com.mgmtp.jfunk.data.generator.util.XMLTags;

/**
 * Factory to generate constraint objects from the associated xml element objects.
 * 
 */
public final class ConstraintFactory {

	private final Map<String, Constraint> map;
	private final Generator generator;
	private static final Logger LOG = Logger.getLogger(ConstraintFactory.class);
	private final Injector injector;

	public ConstraintFactory(final Generator callback, final Injector injector) {
		this.map = new HashMap<String, Constraint>();
		generator = callback;
		this.injector = injector;
	}

	/**
	 * Generates an instance based on the data in the given object. The object's class will be
	 * determined by the class attribute of the element. IF the element contains an id attribute the
	 * generated instance is stored in a map using this id as key.
	 */
	public Constraint createModel(final MathRandom random, final Element element) {
		if (element == null) {
			return null;
		}
		Class<? extends Constraint> classObject = null;
		Constraint object = null;
		try {
			classObject = getClassObject(element);
			Constructor<? extends Constraint> constructor = getConstructor(classObject);
			object = getObject(random, element, constructor);
		} catch (InvocationTargetException ex) {
			throw new JFunkException("Could not initialise object of class " + classObject, ex.getCause());
		} catch (Exception ex) {
			throw new JFunkException("Could not initialise object of class " + classObject, ex);
		}
		putToCache(element, object);
		return object;
	}

	/**
	 * Returns the constraint to the associated id attribute value of the passed element. To achieve
	 * this the id attribute of the passed element is searched first. If there is none an
	 * IdNotFoundException will be thrown. Then the constraint stored under the respective key is
	 * returned from the internal table. If this constraint is not found a wrapper is written to the
	 * table at the requested key. This way the sequence of the constraints in the XML file does not
	 * determine the cross-references by id. The wrapper will be replaced, if a constraint already
	 * represented by the wrapper in the table is generated later on.
	 * 
	 */
	public Constraint getModel(final Element element) throws IdNotFoundException {
		Attribute attr = element.getAttribute(XMLTags.ID);
		if (attr == null) {
			throw new IdNotFoundException(null);
		}
		Constraint c = null;
		final String id = attr.getValue();
		try {
			c = getModel(id);
		} catch (IdNotFoundException e) {
			LOG.debug("DummyConstraint fuer id " + id);
		}
		if (c == null) {
			c = new DummyConstraint();
			putToCache(id, c);
		}
		return c;
	}

	/**
	 * Returns the object in the map with the key id
	 */
	public Constraint getModel(final String id) throws IdNotFoundException {
		Constraint e = map.get(id);
		if (e == null) {
			throw new IdNotFoundException(id);
		}
		return e;
	}

	/**
	 * Returns all keys stored in the map
	 */
	public Collection<String> getModelIds() {
		return new ArrayList<String>(map.keySet());
	}

	/**
	 * This method returns the class object of which a new instance shall be generated. To achieve
	 * this the class attribute of the passed element will be used to determine the class name
	 */
	private Class<? extends Constraint> getClassObject(final Element element) throws ClassNotFoundException {
		String className = element.getAttributeValue(XMLTags.CLASS);
		className = className.indexOf('.') > 0 ? className : getClass().getPackage().getName() + '.' + className;
		return Class.forName(className).asSubclass(Constraint.class);

	}

	/**
	 * Searches for the matching constraint constructor with the parameter types MathRandom, Element
	 * and Generator.
	 */
	private Constructor<? extends Constraint> getConstructor(final Class<? extends Constraint> classObject) throws NoSuchMethodException {
		return classObject.getConstructor(new Class[] { MathRandom.class, Element.class, Generator.class });
	}

	/**
	 * Generates a new constraint instance using the given constructor, the element and the
	 * generateor callback as parameters.
	 */
	private Constraint getObject(final MathRandom random, final Element element, final Constructor<? extends Constraint> constructor)
			throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		LOG.debug("Creating constraint: " + element.getAttributes());
		Constraint instance = constructor.newInstance(new Object[] { random, element, generator });
		injector.injectMembers(instance);
		return instance;
	}

	/**
	 * If the element has an attribute with name id this attribute's value will be used as key to
	 * store the just generated object in a map. The object can in this case also be retrieved using
	 * this id.
	 * 
	 * @see #getModel(String)
	 */
	private void putToCache(final Element element, final Constraint object) {
		String id = element.getAttributeValue(XMLTags.ID);
		if (id != null && id.length() > 0) {
			Constraint old = putToCache(id, object);
			if (old != null) {
				LOG.warn("The id=" + id + " for object of type=" + old.getClass().getName()
						+ " was already found. Please make sure this is ok and no mistake.");
			}
		}
	}

	/**
	 * Puts the given constraint object into the cache using the key id. If an existing
	 * DummyConstraint object is found in the table, the reference will be set to the constraint and
	 * the constraint will not be put into the table.
	 */
	private Constraint putToCache(final String id, final Constraint object) {
		Constraint c = this.map.put(id, object);
		if (c instanceof DummyConstraint) {
			((DummyConstraint) c).constraint = object;
			return null;
		}
		return c;
	}

	/**
	 * Wrapper class to provide a constraint which is already linked by its id but which has not yet
	 * been generated (for example because it is rather at the end of the XML file). If the real
	 * constraint is generated later on with the matching id, a reference to this will be put to the
	 * existing Dummy constraint
	 */
	private static class DummyConstraint implements Constraint {
		private Constraint constraint;

		@Override
		public int countCases() {
			return constraint.countCases();
		}

		@Override
		public Set<String> getContainedIds() {
			return constraint.getContainedIds();
		}

		@Override
		public FieldControl getControl() {
			return constraint.getControl();
		}

		@Override
		public String getId() {
			return constraint.getId();
		}

		@Override
		public String getLastIdInHierarchy() {
			return constraint.getLastIdInHierarchy();
		}

		@Override
		public int getMaxLength() {
			return constraint.getMaxLength();
		}

		@Override
		public boolean hasNextCase() {
			return constraint.hasNextCase();
		}

		@Override
		public String initValues(final FieldCase c) {
			return constraint.initValues(c);
		}

		@Override
		public void resetCase() {
			constraint.resetCase();
		}

		@Override
		public void resetValues() {
			constraint.resetValues();
		}

		@Override
		public void setValueCallback(final ValueCallback value) {
			constraint.setValueCallback(value);
		}
	}
}