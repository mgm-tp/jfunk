package com.mgmtp.jfunk.application.runner;

import javafx.scene.control.TreeItem;

/**
 * Created by rnaegele on 02.10.2014.
 */
public interface TreeItemVisitor<T> {

	public boolean visit(TreeItem<T> treedItem);
}
