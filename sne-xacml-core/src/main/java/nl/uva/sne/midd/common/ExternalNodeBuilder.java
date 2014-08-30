package nl.uva.sne.midd.common;

import nl.uva.sne.midd.nodes.ExternalNode;

@Deprecated
public class ExternalNodeBuilder implements NodeBuilder<ExternalNode> {

    @Deprecated
    @Override
    public ExternalNode createObject() {
        return ExternalNode.newInstance();
    }
}
