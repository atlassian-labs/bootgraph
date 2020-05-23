package com.atlassian.bootgraph.api.filter

import com.atlassian.bootgraph.api.model.GraphModel
import com.atlassian.bootgraph.api.model.Node

/**
 * Filters the model to excludes nodes which are in a cluster. Edges to/from the node will then go
 * to/from the cluster. This effectively raises the abstraction level of the resulting graph.
 */
class ClusterFilter : GraphModelFilter {

    override fun applyTo(originalModel: GraphModel): GraphModel {

        val filteredModel = originalModel.shallowCopy()
        val visitedNodes: MutableMap<String, Node> = HashMap()

        for (node in originalModel.getNodes()) {

            val newNode =
                    node.cluster?.let {
                        visitedNodes.getOrPut(node.cluster, { Node(node.cluster, node.isExternal) })
                    } ?: run {
                        visitedNodes.getOrPut(node.name, { node.shallowCopy() })
                    }

            filterEdges(node, newNode, visitedNodes)
            filteredModel.addNode(newNode)
        }

        return filteredModel
    }

    private fun filterEdges(originalNode: Node, newNode: Node, visitedNodes: MutableMap<String, Node>) {

        // Replace all incoming edge source nodes with their cluster, if needed.
        for (incomingEdge in originalNode.incomingEdges()) {

            if (incomingEdge.from.isInCluster()) {
                incomingEdge.from.cluster?.let {
                    val fromNode = visitedNodes.getOrPut(it, { Node(incomingEdge.from.cluster, incomingEdge.from.isExternal) })
                    newNode.addEdgeFrom(fromNode, incomingEdge.label)
                }
            }

            if (!incomingEdge.from.isInCluster()) {
                val fromNode = visitedNodes.getOrPut(incomingEdge.from.name, { incomingEdge.from.shallowCopy() })
                newNode.addEdgeFrom(fromNode, incomingEdge.label)
            }
        }

        // Replace all outgoing edge target nodes with their cluster, if needed.
        for (outgoingEdge in originalNode.outgoingEdges()) {

            if (outgoingEdge.to.isInCluster()) {
                outgoingEdge.to.cluster?.let {
                    val toNode = visitedNodes.getOrPut(it, { Node(outgoingEdge.to.cluster, outgoingEdge.to.isExternal) })
                    newNode.addEdgeTo(toNode, outgoingEdge.label)
                }
            }

            if (!outgoingEdge.to.isInCluster()) {
                val toNode = visitedNodes.getOrPut(outgoingEdge.to.name, { outgoingEdge.to.shallowCopy() })
                newNode.addEdgeTo(toNode, outgoingEdge.label)
            }
        }
    }

}
