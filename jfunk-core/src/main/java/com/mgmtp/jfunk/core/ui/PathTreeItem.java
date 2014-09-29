package com.mgmtp.jfunk.core.ui;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.mgmtp.jfunk.common.exception.JFunkException;

/**
 *
 * @author rnaegele
 * @since 3.1.0
 */
public class PathTreeItem extends TreeItem<ItemInfo> {
	private Boolean isLeaf = null;
	private ObservableList<TreeItem<ItemInfo>> children;

	public PathTreeItem(final ItemInfo itemInfo) {
		super(itemInfo);
		String res = Files.isDirectory(itemInfo.getPath()) ? "folder.png" : "text-x-generic.png";
		setGraphic(new ImageView(new Image(getClass().getResourceAsStream(res))));
	}

	@Override
	public ObservableList<TreeItem<ItemInfo>> getChildren() {
		if (children == null) {
			children = super.getChildren();
			children.setAll(retrieveChildren(this));
		}
		return children;
	}

	@Override
	public boolean isLeaf() {
		if (isLeaf == null) {
			isLeaf = !Files.isDirectory(getValue().getPath(), LinkOption.NOFOLLOW_LINKS);
		}
		return isLeaf.booleanValue();
	}

	private ObservableList<TreeItem<ItemInfo>> retrieveChildren(final TreeItem<ItemInfo> treeItem) {
		Path path = treeItem.getValue().getPath();
		if (path != null && Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
			ObservableList<TreeItem<ItemInfo>> childrenList = FXCollections.observableArrayList();
			try (DirectoryStream<Path> dirs = Files.newDirectoryStream(path)) {
				for (Path dir : dirs) {
					childrenList.add(new PathTreeItem(new ItemInfo(dir)));
				}
			} catch (IOException ex) {
				throw new JFunkException("Error retrieving children.", ex);
			}
			return childrenList;
		}

		return FXCollections.emptyObservableList();
	}
}