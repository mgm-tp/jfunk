/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.data.generator.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to identify XML elements using their id within a Jdom element tree.
 * 
 * @version $Id$
 */
public class XmlElementFinder {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Document document;
	private final Map<String, Element> cache;
	private final String idAttributeName;
	private String elementName;

	/**
	 * Creates a new instance
	 * 
	 * @param document
	 *            The document within which will be searched
	 * @param elementName
	 *            the name of the element to be searched
	 * @param idAttributeName
	 *            the name of the attribute that constitutes the "id" to be searched
	 */
	public XmlElementFinder(final Document document, final String elementName, final String idAttributeName) {
		this.document = document;
		cache = new HashMap<String, Element>();
		this.idAttributeName = idAttributeName;
		this.elementName = elementName;
	}

	/**
	 * Creates a new instance
	 * 
	 * @param document
	 *            The document within which will be searched
	 * @param idAttributeName
	 *            the name of the attribute that constitutes the "id" to be searched
	 */
	public XmlElementFinder(final Document document, final String idAttributeName) {
		this.document = document;
		cache = new HashMap<String, Element>();
		this.idAttributeName = idAttributeName;
	}

	/**
	 * Creates a new instance with the id atteribute name "id"
	 */
	public XmlElementFinder(final Document document) {
		this(document, "id");
	}

	/**
	 * Searches the attribute id in the given element first and using this element searches for the
	 * element with the attribute id and the value of this element.
	 * 
	 * @return the element with the id whose value is contained in the id attribute of the passed
	 *         element
	 * 
	 * @see #findElementById(String)
	 */
	public Element findElementById(final Element element) {
		Attribute a = element.getAttribute(idAttributeName);
		if (a == null) {
			return null;
		}
		return findElementById(a.getValue());
	}

	/**
	 * Searches first in the cache for the given id. If it is found the element saved there will be
	 * returned. If it is not found the search - starting at the RootElement- will continue until
	 * the first element in the tree hierarchy is found which 1. has the attribute id and 2. whose
	 * id attribute has the value of the parameter's id. Once found it will be saved in the cache.
	 * 
	 * @return the element with the respective id
	 */
	public Element findElementById(final String id) {
		Element element = cache.get(id);
		if (element == null) {
			if (log.isDebugEnabled()) {
				log.debug("Search for element with ID {}", id);
			}
			Element root = document.getRootElement();
			element = search(root, elementName, idAttributeName, id);
			if (element != null) {
				cache.put(id, element);
			}
		}
		return element;
	}

	/**
	 * Searches the ElementBaum starting with the given element for the ChildElement with the
	 * respective attribute value
	 * 
	 * @return the matching element
	 */
	public static Element search(final Element root, final String elementName, final String idAttributeName, final String id) {
		Element element = null;
		@SuppressWarnings("unchecked")
		List<Element> children = root.getChildren();
		for (Element e : children) {
			if (elementName == null || e.getName().equals(elementName)) {
				Attribute a = e.getAttribute(idAttributeName);
				if (a != null && id.equals(a.getValue())) {
					element = e;
				} else {
					element = search(e, elementName, idAttributeName, id);
				}
			}
			if (element != null) {
				break;
			}
		}
		return element;
	}

	/**
	 * Searches for the Child with the given name ignoring namespaces
	 * 
	 * @return the Child with the name ignoring namespaces
	 */
	public static Element getChild(final String name, final Element root) {
		@SuppressWarnings("unchecked")
		List<Element> allChildren = root.getChildren();
		for (Element child : allChildren) {
			if (child.getName().equals(name)) {
				return child;
			}
		}
		return null;
	}

	/**
	 * @return a list containing all child nodes whose tagname matches the given name ignoring
	 *         namespaces
	 */
	public static List<Element> getChildren(final String name, final Element root) {
		@SuppressWarnings("unchecked")
		List<Element> allChildren = root.getChildren();
		ArrayList<Element> children = new ArrayList<Element>(allChildren.size());
		for (Element child : allChildren) {
			if (child.getName().equals(name)) {
				children.add(child);
			}
		}
		return children;
	}
}