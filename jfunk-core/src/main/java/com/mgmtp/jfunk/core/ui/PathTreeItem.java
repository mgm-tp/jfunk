package com.mgmtp.jfunk.core.ui;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

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
	private final PathMatcher matcher;

	public PathTreeItem(final ItemInfo itemInfo, final PathMatcher matcher) {
		super(itemInfo);
		this.matcher = matcher;
		if (itemInfo.getPath() != null) {
			String res = Files.isDirectory(itemInfo.getPath()) ? "/com/famfamfam/silk/folder.png" : "/com/famfamfam/silk/page.png";
			setGraphic(new ImageView(new Image(getClass().getResource(res).toExternalForm())));
		}
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
			Path path = getValue().getPath();
			if (path == null) {
				isLeaf = Boolean.FALSE;
			} else {
				isLeaf = !Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS);
			}
		}
		return isLeaf.booleanValue();
	}

	private ObservableList<TreeItem<ItemInfo>> retrieveChildren(final TreeItem<ItemInfo> treeItem) {
		Path path = treeItem.getValue().getPath();
		if (path != null && Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
			ObservableList<TreeItem<ItemInfo>> childrenList = FXCollections.observableArrayList();
			try (DirectoryStream<Path> filesAndDirs = Files.newDirectoryStream(path)) {
				for (Path fileOrDir : filesAndDirs) {
					if (Files.isDirectory(fileOrDir) || matcher.matches(fileOrDir)) {
						childrenList.add(new PathTreeItem(new ItemInfo(fileOrDir), matcher));
					}
				}
			} catch (IOException ex) {
				throw new JFunkException("Error retrieving children.", ex);
			}
			return childrenList;
		}

		return FXCollections.emptyObservableList();
	}
}