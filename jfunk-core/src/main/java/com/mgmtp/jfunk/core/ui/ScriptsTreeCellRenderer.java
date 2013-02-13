/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.ui;

import java.awt.Component;
import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * @version $Id$
 */
public class ScriptsTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean isSelected, final boolean expanded,
			final boolean leaf, final int row, final boolean isFocused) {

		String val;
		if (value instanceof String) {
			val = value.toString();
		} else {
			val = ((File) value).getName();
		}
		return super.getTreeCellRendererComponent(tree, val, isSelected, expanded, leaf, row, isFocused);
	}
}