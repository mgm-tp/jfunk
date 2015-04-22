/*
 * Copyright (c) 2015 mgm technology partners GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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