package com.mgmtp.jfunk.application.runner.util;

import com.google.common.base.Predicate;
import com.mgmtp.jfunk.application.runner.ItemInfo;
import com.mgmtp.jfunk.application.runner.TreeItemVisitor;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static com.google.common.io.Resources.getResource;

/**
 * @author rnaegele
 * @since 3.1.0
 */
public class UiUtils {

	public static void traverseTreeItem(TreeItem<ItemInfo> startNode, TreeItemVisitor<ItemInfo> visitor) {
		if (visitor.visit(startNode)) {
			if (!startNode.isLeaf()) {
				for (TreeItem<ItemInfo> treeItem : startNode.getChildren()) {
					traverseTreeItem(treeItem, visitor);
				}
			}
		}
	}

	public static TreeItem<ItemInfo> findTreeItem(TreeItem<ItemInfo> startNode, Predicate<TreeItem<ItemInfo>> predicate) {
		for (TreeItem<ItemInfo> node : startNode.getChildren()) {
			if (predicate.apply(node)) {
				return node;
			}
			TreeItem<ItemInfo> result = findTreeItem(node, predicate);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	public static void setExpanded(final TreeItem<ItemInfo> startNode, final boolean expanded) {
		traverseTreeItem(startNode, new TreeItemVisitor<ItemInfo>() {
			@Override
			public boolean visit(final TreeItem<ItemInfo> treeItem) {
				if (!treeItem.isLeaf()) {
					treeItem.setExpanded(expanded);
				}
				return true;
			}
		});
	}

	public static Image createImage(String resource) {
		return new Image(getResource(resource).toExternalForm());
	}

	public static Image createImage(Class<?> contextClass, String resource) {
		return new Image(getResource(contextClass, resource).toExternalForm());
	}

	public static ImageView createImageView(String resource) {
		return new ImageView(createImage(resource));
	}

	public static ImageView createImageView(Class<?> contextClass, String resource) {
		return new ImageView(createImage(contextClass, resource));
	}
}
