package com.mgmtp.jfunk.application.runner;

import com.google.common.base.Predicate;
import org.testng.annotations.Test;

import java.nio.file.Paths;

/**
 * Created by rnaegele on 01.10.2014.
 */
public class TreeTest {

	@Test
	public void testFind() {
		Tree tree = new Tree();
		Node root = new StringNode("root");
		tree.setRootNode(root);
		PathNode child = new PathNode(Paths.get("target/test-classes/com"));
		root.addChild(child);
		PathNode child1 = new PathNode(Paths.get("target/test-classes/com/mgmtp"));
		child.addChild(child1);
		PathNode child2 = new PathNode(Paths.get("target/test-classes/com/mgmtp/jfunk"));
		child1.addChild(child2);
		PathNode child3 = new PathNode(Paths.get("target/test-classes/com/mgmtp/jfunk/samples"));
		child2.addChild(child3);
		PathNode child4 = new PathNode(Paths.get("target/test-classes/com/mgmtp/jfunk/samples/unit"));
		child3.addChild(child4);
		PathNode child5 = new PathNode(Paths.get("target/test-classes/com/mgmtp/jfunk/samples/unit/ContainerModuleTest"));
		child4.addChild(child5);
		PathNode child6 = new PathNode(Paths.get("target/test-classes/com/mgmtp/jfunk/samples/unit/JUnitGoogleTest"));
		child5.addChild(child6);
		PathNode child7 = new PathNode(Paths.get("target/test-classes/com/mgmtp/jfunk/samples/unit/TestNGGoogleTest"));
		child6.addChild(child7);
		System.out.println(tree);

		System.out.println(tree.findNode(new Predicate<Node<?>>() {
			@Override
			public boolean apply(final Node<?> input) {
				return input.getDisplayString().endsWith("samples");
			}
		}));
	}
}
