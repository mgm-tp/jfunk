/*
 *  Copyright (C) 2013 mgm technology partners GmbH, Munich.
 *
 *  See the LICENSE file distributed with this work for additional
 *  information regarding copyright ownership and intellectual property rights.
 */
package com.mgmtp.jfunk.core.ui;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FilenameUtils;

/**
 */
public class ScriptsTreeModel implements Serializable, TreeModel {

	private static final long serialVersionUID = 1L;

	private static final String THE_REAL_ROOT = "root";
	private final List<File> roots;

	public ScriptsTreeModel(final List<File> roots) {
		this.roots = roots;
	}

	@Override
	public Object getChild(final Object parent, final int index) {
		if (THE_REAL_ROOT.equals(parent)) {
			return roots.get(index);
		}
		File[] files = listContents((File) parent);
		return files[index];
	}

	@Override
	public int getChildCount(final Object parent) {
		if (THE_REAL_ROOT.equals(parent)) {
			return roots.size();
		}
		File[] files = listContents((File) parent);
		return files.length;
	}

	@Override
	public int getIndexOfChild(final Object parent, final Object child) {
		if (THE_REAL_ROOT.equals(parent)) {
			return roots.indexOf(child);
		}
		File[] files = listContents((File) parent);
		for (int i = 0; i < files.length; ++i) {
			if (files[i].equals(child)) {
				return i;
			}
		}
		return -1;
	}

	private File[] listContents(final File dir) {
		File[] files = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(final File file) {
				String ext = FilenameUtils.getExtension(file.getName());
				return file.isDirectory() && !".svn".equals(file.getName()) || "script".equals(ext) || "groovy".equals(ext);
			}
		});
		return files;
	}

	@Override
	public Object getRoot() {
		return THE_REAL_ROOT;
	}

	@Override
	public boolean isLeaf(final Object node) {
		return node instanceof File && ((File) node).isFile();
	}

	@Override
	public void valueForPathChanged(final TreePath path, final Object newValue) {
		// Keine Implementierung nötig. Tree ist read-only.
	}

	@Override
	public void addTreeModelListener(final TreeModelListener l) {
		// Keine Implementierung nötig. Tree ist read-only.
	}

	@Override
	public void removeTreeModelListener(final TreeModelListener l) {
		// Keine Implementierung nötig. Tree ist read-only.
	}
}