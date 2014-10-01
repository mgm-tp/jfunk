package com.mgmtp.jfunk.application.runner;

import com.google.common.base.Predicate;

public class Tree {
 
    private Node<?> rootNode;

    /**
     * Return the root Node of the tree.
     * @return the root element.
     */
    public Node<?> getRootNode() {
        return this.rootNode;
    }
 
    /**
     * Set the root Element for the tree.
     * @param rootNode the root element to set.
     */
    public void setRootNode(Node<?> rootNode) {
        this.rootNode = rootNode;
    }

	public Node<?> findNode(Predicate<Node<?>> predicate) {
		return findNode(rootNode, predicate);
	}

	private Node<?> findNode(Node<?> startNode, Predicate<Node<?>> predicate) {
		for (Node<?> node : startNode.getChildren()) {
			if (predicate.apply(node)) {
				return node;
			}
			Node result = findNode(node, predicate);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return rootNode.toString();
	}
}
