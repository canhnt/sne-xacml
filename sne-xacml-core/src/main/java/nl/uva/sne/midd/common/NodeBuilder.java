package nl.uva.sne.midd.common;

import nl.uva.sne.midd.nodes.AbstractNode;

import java.lang.reflect.InvocationTargetException;

public interface NodeBuilder<NodeType extends AbstractNode> {
    NodeType createObject() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException;
}
