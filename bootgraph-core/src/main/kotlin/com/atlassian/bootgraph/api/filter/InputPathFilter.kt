package com.atlassian.bootgraph.api.filter

import com.atlassian.bootgraph.api.model.GraphModel
import com.atlassian.bootgraph.api.model.Node
import java.lang.IllegalStateException

/**
 * Filters the model to only include those nodes that are connected to the specified node via an inbound connection.
 * In other words: only includes a node B if there is a path from the start node A to the node B.
 */
class InputPathFilter(
    private val startNode: String
) : GraphModelFilter {

    override fun applyTo(originalModel: GraphModel): GraphModel {

        val newModel = originalModel.shallowCopy()
        val startNode = originalModel.getNode(startNode)
                ?: throw IllegalStateException("Could not find node $startNode in ApplicationModel. The node to base this filter on must exist!")
        val newStartNode = startNode.shallowCopy()

        addOutputsRecursively(startNode, newStartNode, originalModel, newModel)

        return newModel
    }

    private fun addOutputsRecursively(originalNode: Node, newNode: Node, originalGraphModel: GraphModel, newGraphModel: GraphModel) {
        addOutputsRecursively(originalNode, newNode, originalGraphModel, newGraphModel, HashSet())
    }

    private fun addOutputsRecursively(originalNode: Node, newNode: Node, originalGraphModel: GraphModel, newGraphModel: GraphModel, visitedNodes: MutableSet<Node>) {

        for (output in originalNode.outgoingEdges()) {

            val outputNode = output.to

            if (visitedNodes.contains(outputNode)) {
                // just add the connection and break recursion
                newGraphModel.getNode(outputNode.name)?.let {
                    newNode.addEdgeTo(it)
                }
                continue
            }

            val newOutputNode = output.to.shallowCopy()
            newNode.addEdgeTo(newOutputNode, output.label)
            newGraphModel.addNode(newOutputNode)

            visitedNodes.add(originalNode)
            addOutputsRecursively(output.to, newOutputNode, originalGraphModel, newGraphModel, visitedNodes)
        }
    }
}
