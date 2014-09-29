package com.mgmtp.jfunk.core.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DynamicTreeNodeModel {

	private DynamicTreeNodeModel parent;
	private String name;
	private List<DynamicTreeNodeModel> children = null;
	private final List<File> childfiles_all;
	private final File startPoint;

	public DynamicTreeNodeModel(final DynamicTreeNodeModel parent, final String name, final File startPoint) {
		this.parent = parent;
		this.name = name;
		this.startPoint = startPoint;
		this.childfiles_all = new ArrayList<>();
		// this.childfiles_files = new ArrayList();
		// this.childfiles_directories = new ArrayList();

		if (startPoint.isDirectory()) {
			try {
				childfiles_all.addAll(Arrays.asList(startPoint.listFiles()));
			} catch (Exception nofiles) {
				System.out.println("No Files for Volume " + startPoint.getAbsolutePath());
			}
		}

		/*
		 * for(File f :childfiles_all){ if (f.isDirectory()){ childfiles_directories.add(f); } else
		 * { childfiles_files.add(f); } }
		 */

	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public boolean isPopulated() {
		if (children == null) {
			return false;
		}
		return true;
	}

	public boolean areChildenPopulated() {
		if (!this.isPopulated()) {
			return false;
		}
		for (DynamicTreeNodeModel child : this.children) {
			if (!child.isPopulated()) {
				return false;
			}
		}
		return true;
	}

	public List<DynamicTreeNodeModel> getChildren() {
		return children;
	}

	public void setChildren(final List<DynamicTreeNodeModel> children) {
		this.children = children;
	}

	public DynamicTreeNodeModel getParent() {
		return parent;
	}

	public void setParent(final DynamicTreeNodeModel parent) {
		this.parent = parent;
	}

	public void populateToDepth(final int depth) {
		if (depth == 0) {
			return;
		}

		if (children == null) {
			children = new ArrayList<>();
			for (File f : childfiles_all) {
				//Directories only - these FIRST
				if (f.isDirectory()) {
					if (f.getName().isEmpty()) {
						children.add(new DynamicTreeNodeModel(this, f.getAbsolutePath(), f));
					} else {
						children.add(new DynamicTreeNodeModel(this, f.getName(), f));
					}
				}
				//Files - these SECOND
				/*
				 * if (!f.isDirectory()) { if (f.getName().isEmpty()) { children.add(new
				 * DynamicDirTreeNodeModel(this, f.getAbsolutePath(), f)); } else { children.add(new
				 * DynamicDirTreeNodeModel(this, f.getName(), f)); } }
				 */

			}
		}

		int childdepth = depth - 1;
		for (DynamicTreeNodeModel child : children) {
			child.populateToDepth(childdepth);
		}
	}

	@Override
	public String toString() {
		return this.name;
	}
}