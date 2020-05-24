package com.atlassian.bootgraph.api.model

import com.atlassian.bootgraph.api.filter.GraphModelFilter
import org.slf4j.LoggerFactory

/**
 * A model from which to create a graph representing part of an application.
 */
data class GraphModel(

        /**
         * The name of the graph.
         */
        val name: String

) {

    companion object {
        private val logger = LoggerFactory.getLogger(GraphModel::class.java)
    }

    private val nodes: MutableMap<String, Node> = HashMap()

    /**
     * Returns all nodes in this model.
     */
    fun getNodes(): List<Node> {
        return nodes.values.filter { true }
    }

    /**
     * Returns only the nodes which are internal to the application.
     */
    fun getInternalNodes(): List<Node> {
        return nodes.values.filter { !it.isExternal }
    }

    /**
     * Returns only the nodes which are internal to the application and have edges pointing towards external nodes.
     */
    fun getInternalNodesWithEdgeToExternal(): List<Node> {
        return nodes.values.filter { node ->
            !node.isExternal &&
                    node.outputs.keys.any { it.isExternal }
        }
    }

    /**
     * Returns only the nodes which are internal to the application and have edges coming from external nodes.
     */
    fun getInternalNodesWithEdgeFromExternal(): List<Node> {
        return nodes.values.filter { node ->
            !node.isExternal &&
                    node.inputs.keys.any { it.isExternal }
        }
    }

    /**
     * Returns only the nodes which are external to the application.
     */
    fun getExternalNodes(): List<Node> {
        return nodes.values.filter { it.isExternal }
    }

    /**
     * Returns only the nodes which are external to the application and have edges towards internal nodes.
     */
    fun getExternalNodesWithEdgeToInternal(): List<Node> {
        return nodes.values.filter { node ->
            node.isExternal &&
                    node.outputs.keys.any { !it.isExternal }
        }
    }

    /**
     * Returns only the nodes which are external to the application and have edges from internal nodes.
     */
    fun getExternalNodesWithEdgeFromInternal(): List<Node> {
        return nodes.values.filter { node ->
            node.isExternal &&
                    node.inputs.keys.any { !it.isExternal }
        }
    }

    fun addNode(node: Node): GraphModel {
        nodes[node.name] = node

        for (inputEdge in node.incomingEdges()) {
            nodes[inputEdge.from.name] = inputEdge.from
        }

        for (outputEdge in node.outgoingEdges()) {
            nodes[outputEdge.to.name] = outputEdge.to
        }

        // TODO: iterate the whole graph starting from the input node to add all edges and not only the edges with depth = 1

        return this
    }

    fun addNodes(vararg nodes: Node): GraphModel {
        for (node in nodes) {
            addNode(node)
        }
        return this
    }

    fun getNode(name: String): Node? {
        return nodes[name]
    }

    fun containsNode(name: String): Boolean {
        return nodes.containsKey(name)
    }

    fun getEdge(fromNode: String, toNode: String): Edge? {
        val from = getNode(fromNode) ?: return null

        for (connection in from.outgoingEdges()) {
            if (connection.to.name == toNode) {
                return connection
            }
        }

        return null
    }

    /**
     * Copies this model into a new one, without the nodes and edges.
     */
    fun shallowCopy(): GraphModel {
        return GraphModel(name)
    }

    /**
     * Filters this model according to the rules of the specified filter to create a sub-view of the graph.
     * Does not modify this model.
     */
    fun filter(filter: GraphModelFilter): GraphModel {
        return filter.applyTo(this)
    }

    fun toDebugString(): String {
        val builder = StringBuilder(String.format("GraphModel[name ='%s'] with nodes:\n", this.name))
        for((nodeName, node) in nodes){
            builder.append(node.toDebugString())
        }
        return builder.toString()
    }

}
