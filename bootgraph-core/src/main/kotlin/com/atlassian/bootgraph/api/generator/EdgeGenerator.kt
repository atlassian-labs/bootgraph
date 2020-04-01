package com.atlassian.bootgraph.api.generator

import com.atlassian.bootgraph.api.mapper.NodeFactory
import com.atlassian.bootgraph.api.model.Node

/**
 * Generates an edge between two nodes. The nodes must exist in the graph model, otherwise an exception will be thrown.
 */
class EdgeGenerator(
        val fromNode: String,
        val toNode: String,
        val label: String?
) : NodeGenerator {

    override fun generateNode(factory: NodeFactory): Node {
        val from = factory.getExistingNode(fromNode)
        val to = factory.getExistingNode(toNode)
        from.addEdgeTo(to, label)
        return from
    }

}