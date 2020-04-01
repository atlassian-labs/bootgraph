package com.atlassian.bootgraph.api.generator

import com.atlassian.bootgraph.api.mapper.NodeFactory
import com.atlassian.bootgraph.api.model.Node

/**
 * Contributes an external node with a given name to the graph.
 */
class ExternalNodeGenerator(
        val name: String
) : NodeGenerator {

    override fun generateNode(factory: NodeFactory): Node {
        return factory.externalNode(name)
    }

}