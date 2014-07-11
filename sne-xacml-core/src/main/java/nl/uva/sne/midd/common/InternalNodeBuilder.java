package nl.uva.sne.midd.common;

import java.lang.reflect.InvocationTargetException;

import nl.uva.sne.midd.nodes.AbstractNode;
import nl.uva.sne.midd.nodes.InternalNode;

/**
 * @author cngo
 * @version $Id$
 * @since 2014-07-11
 */
public class InternalNodeBuilder<NodeType extends InternalNode> implements NodeBuilder<NodeType> {
    @Override
    public NodeType createObject() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        return null;
    }
}
