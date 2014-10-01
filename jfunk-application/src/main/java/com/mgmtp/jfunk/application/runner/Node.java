package com.mgmtp.jfunk.application.runner;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import java.util.SortedSet;
import java.util.TreeSet;

public class Node<T extends Comparable<T>> implements Comparable<Node<T>> {

	protected Node parent;
	protected SortedSet<Node<T>> children = new TreeSet<>();
	protected T data;

	public Node(final T data) {
		this.data = Preconditions.checkNotNull(data, "'data' must not be null");
	}

	public T getData() {
		return data;
	}

	public String getDisplayString() {
		return data.toString();
	}

	public Node<T> getParent() {
		return parent;
	}

	/**
	 * Return the children of Node. The Tree is represented by a single
	 * root Node whose children are represented by a List<Node>. Each of
	 * these Node elements in the List can have children. The getChildren()
	 * method will return the children of a Node.
	 *
	 * @return the children of Node
	 */
	public SortedSet<Node<T>> getChildren() {
		return this.children;
	}

	/**
	 * Adds a child to the list of children for this Node. The addition of
	 * the first child will create a new List<Node>.
	 *
	 * @param child a Node object to set.
	 */
	public void addChild(Node<T> child) {
		child.parent = this;
		children.add(child);
	}

	public int getLevel() {
		int level = 0;
		Node current = this;
		for (; current.parent != null; level++) {
			current = current.parent;
		}
		return level;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Node node = (Node) o;

		if (!children.equals(node.children)) {
			return false;
		}
		if (!data.equals(node.data)) {
			return false;
		}
		if (parent != null ? !parent.equals(node.parent) : node.parent != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = parent != null ? parent.hashCode() : 0;
		result = 31 * result + children.hashCode();
		result = 31 * result + data.hashCode();
		return result;
	}

	@Override
	public int compareTo(final Node<T> o) {
		int result = 0;

		if (parent == null && o.parent == null) {
			result = 0;
		} else if (parent == null && o.parent != null) {
			result = -1;
		} else {
			result = 1;
		}

		if (result == 0) {
			result = data.compareTo(o.data);
		}

		return result;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("parent", parent).add("level", getLevel()).add("data", data).toString();
	}
}
