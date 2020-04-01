package com.atlassian.bootgraph.api.generator

import com.atlassian.bootgraph.api.mapper.NodeFactory
import com.atlassian.bootgraph.api.model.Node

/**
 * Contributes an internal node with the given name to the graph.
 */
class InternalNodeGenerator(
        val name: String
) : NodeGenerator {

    override fun generateNode(factory: NodeFactory): Node {
        return factory.internalNode(name)
    }

}