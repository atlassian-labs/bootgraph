package com.atlassian.bootgraph.api.filter

import com.atlassian.bootgraph.api.model.GraphModel
import com.atlassian.bootgraph.api.model.Node
import java.lang.IllegalStateException

/**
 * Filters the model to only include those nodes that are connected to the specified node via an outbound connection.
 * In other words: only includes a node A if there is a path from A to the specified end node B.
 */
class OutputPathFilter(
    private val endNode: String
) : GraphModelFilter {

    override fun applyTo(originalModel: GraphModel): GraphModel {

        val newModel = originalModel.shallowCopy()
        val endNode = originalModel.getNode(endNode)
                ?: throw IllegalStateException("Could not find node $endNode in ApplicationModel. The node to base this filter on must exist!")
        val newEndNode = endNode.shallowCopy()

        addInputsRecursively(endNode, newEndNode, originalModel, newModel)

        return newModel
    }

    private fun addInputsRecursively(originalNode: Node, newNode: Node, originalGraphModel: GraphModel, newGraphModel: GraphModel) {
        addInputsRecursively(originalNode, newNode, originalGraphModel, newGraphModel, HashSet())
    }

    private fun addInputsRecursively(originalNode: Node, newNode: Node, originalGraphModel: GraphModel, newGraphModel: GraphModel, visitedNodes: MutableSet<Node>) {

        for (input in originalNode.incomingEdges()) {

            val inputNode = input.from

            if (visitedNodes.contains(inputNode)) {
                // just add the connection and break recursion
                newGraphModel.getNode(inputNode.name)?.let {
                    newNode.addEdgeFrom(it)
                }
                continue
            }

            val newInputNode = input.from.shallowCopy()
            newNode.addEdgeFrom(newInputNode, input.label)
            newGraphModel.addNode(newInputNode)

            visitedNodes.add(originalNode)
            addInputsRecursively(input.from, newInputNode, originalGraphModel, newGraphModel, visitedNodes)
        }
    }
}
