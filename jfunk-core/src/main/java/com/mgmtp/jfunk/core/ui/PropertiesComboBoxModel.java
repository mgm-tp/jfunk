/*
 * Copyright (c) 2014 mgm technology partners GmbH
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

/**
 * Model class for the properties selector list.
 * 
 */
public class PropertiesComboBoxModel extends AbstractListModel implements ComboBoxModel {

	private static final long serialVersionUID = 1L;
	private final List<String> items = Lists.newArrayList();
	private String selectedItem;
	private final String propsPrefix;
	private final String propsSuffix;
	private final String path;
	private final String filter;
	private final boolean includeSuffix;

	public PropertiesComboBoxModel(final String path, final String propsPrefix, final String propsSuffix, final String filter,
			final boolean includeSuffix) {
		if (StringUtils.isBlank(path)) {
			throw new IllegalArgumentException("Path must be set.");
		}
		this.path = path;
		if (StringUtils.isBlank(propsPrefix)) {
			this.propsPrefix = "";
		} else {
			this.propsPrefix = propsPrefix;
		}
		if (StringUtils.isBlank(propsSuffix)) {
			this.propsSuffix = "";
		} else {
			this.propsSuffix = propsSuffix;
		}
		this.filter = filter;
		this.includeSuffix = includeSuffix;
		initItems();
		if (items.isEmpty()) {
			throw new IllegalStateException("No valid entry found in " + path);
		}
	}

	private void initItems() {
		File dir = new File(path);
		if (items.isEmpty()) {
			List<File> files = Arrays.asList(dir.listFiles(new FileFilter() {
				@Override
				public boolean accept(final File file) {
					if (file.getName().matches(propsPrefix + ".*\\." + propsSuffix)) {
						if (StringUtils.isNotBlank(filter)) {
							InputStream is = null;
							try {
								Properties props = new Properties();
								is = new FileInputStream(file);
								props.load(is);
								if (props.get(filter) == null) {
									return false;
								}
							} catch (IOException ex) {
								return false;
							} finally {
								IOUtils.closeQuietly(is);
							}
						}
						return true;
					}
					return false;
				}
			}));

			for (File file : files) {
				String fileName = file.getName();
				String itemName;
				itemName = includeSuffix ? fileName : fileName.substring(0, fileName.indexOf("." + propsSuffix));
				items.add(itemName);
			}
		}
	}

	@Override
	public String getElementAt(final int index) {
		return items.get(index);
	}

	@Override
	public int getSize() {
		return items.size();
	}

	@Override
	public String getSelectedItem() {
		if (selectedItem == null) {
			selectedItem = items.get(0);
		}
		return selectedItem;
	}

	@Override
	public void setSelectedItem(final Object anItem) {
		if (!getSelectedItem().equals(anItem) || getSelectedItem() == null && anItem != null) {
			selectedItem = anItem.toString();
			fireContentsChanged(this, -1, -1);
		}
	}

	public boolean isIncludeSuffix() {
		return includeSuffix;
	}
}