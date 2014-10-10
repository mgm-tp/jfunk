package com.mgmtp.jfunk.application.runner;

import javafx.scene.control.TreeItem;

/**
 * @author rnaegele
 * @since 3.1.0
 */
public interface TreeItemVisitor<T> {

	public boolean visit(TreeItem<T> treedItem);
}
