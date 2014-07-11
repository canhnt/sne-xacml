package nl.uva.sne.midd.common;

import nl.uva.sne.midd.nodes.ExternalNode;

public class ExternalNodeBuilder implements NodeBuilder<ExternalNode> {

    @Override
    public ExternalNode createObject() {
        return new ExternalNode();
    }
}
