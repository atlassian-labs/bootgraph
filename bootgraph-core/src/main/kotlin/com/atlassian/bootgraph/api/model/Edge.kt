package com.atlassian.bootgraph.api.model

/**
 * An edge between two nodes in a graph, representing a dependency or data flow between two nodes.
 */
data class Edge(
        val from: Node,
        val to: Node,
        val label: String?
)
